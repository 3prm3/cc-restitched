/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.peripheral.modem.wired;

import com.google.common.collect.ImmutableMap;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.Registry;
import dan200.computercraft.shared.common.BlockGeneric;
import dan200.computercraft.shared.util.WaterloggableHelpers;
import dan200.computercraft.shared.util.WorldUtil;
import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.level.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;

import static dan200.computercraft.shared.util.WaterloggableHelpers.WATERLOGGED;
import static dan200.computercraft.shared.util.WaterloggableHelpers.getFluidStateForPlacement;

public class BlockCable extends BlockGeneric implements Waterloggable, BlockPickInteractionAware
{
    public static final EnumProperty<CableModemVariant> MODEM = EnumProperty.of( "modem", CableModemVariant.class );
    public static final BooleanProperty CABLE = BooleanProperty.of( "cable" );

    private static final BooleanProperty NORTH = BooleanProperty.of( "north" );
    private static final BooleanProperty SOUTH = BooleanProperty.of( "south" );
    private static final BooleanProperty EAST = BooleanProperty.of( "east" );
    private static final BooleanProperty WEST = BooleanProperty.of( "west" );
    private static final BooleanProperty UP = BooleanProperty.of( "up" );
    private static final BooleanProperty DOWN = BooleanProperty.of( "down" );

    static final EnumMap<Direction, BooleanProperty> CONNECTIONS =
        new EnumMap<>( new ImmutableMap.Builder<Direction, BooleanProperty>()
            .put( Direction.DOWN, DOWN ).put( Direction.UP, UP )
            .put( Direction.NORTH, NORTH ).put( Direction.SOUTH, SOUTH )
            .put( Direction.WEST, WEST ).put( Direction.EAST, EAST )
            .build() );

    public BlockCable( Settings settings )
    {
        super( settings, () -> Registry.ModBlockEntities.CABLE );

        setDefaultState( getStateManager().getDefaultState()
            .with( MODEM, CableModemVariant.None )
            .with( CABLE, false )
            .with( NORTH, false ).with( SOUTH, false )
            .with( EAST, false ).with( WEST, false )
            .with( UP, false ).with( DOWN, false )
            .with( WATERLOGGED, false )
        );
    }

    @Override
    protected void appendProperties( StateManager.Builder<Block, BlockState> builder )
    {
        builder.add( MODEM, CABLE, NORTH, SOUTH, EAST, WEST, UP, DOWN, WATERLOGGED );
    }

    public static boolean canConnectIn( BlockState state, Direction direction )
    {
        return state.get( BlockCable.CABLE ) && state.get( BlockCable.MODEM ).getFacing() != direction;
    }

    public static boolean doesConnectVisually( BlockState state, BlockView world, BlockPos pos, Direction direction )
    {
        if( !state.get( CABLE ) ) return false;
        if( state.get( MODEM ).getFacing() == direction ) return true;
        return ComputerCraftAPI.getWiredElementAt( world, pos.offset( direction ), direction.getOpposite() ) != null;
    }

    @Nonnull
    @Override
    @Deprecated
    public VoxelShape getOutlineShape( @Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos, @Nonnull ShapeContext context )
    {
        return CableShapes.getShape( state );
    }

    public boolean removedByPlayer( BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid )
    {
        if( state.get( CABLE ) && state.get( MODEM ).getFacing() != null )
        {
            BlockHitResult hit = world.raycast( new RaycastContext(
                WorldUtil.getRayStart( player ), WorldUtil.getRayEnd( player ),
                RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player
            ) );
            if( hit.getType() == HitResult.Type.BLOCK )
            {
                BlockEntity tile = world.getBlockEntity( pos );
                if( tile instanceof TileCable cable && tile.hasWorld() )
                {

                    ItemStack item;
                    BlockState newState;

                    if( WorldUtil.isVecInside( CableShapes.getModemShape( state ), hit.getPos().subtract( pos.getX(), pos.getY(), pos.getZ() ) ) )
                    {
                        newState = state.with( MODEM, CableModemVariant.None );
                        item = new ItemStack( Registry.ModItems.WIRED_MODEM );
                    }
                    else
                    {
                        newState = state.with( CABLE, false );
                        item = new ItemStack( Registry.ModItems.CABLE );
                    }

                    world.setBlockState( pos, correctConnections( world, pos, newState ), 3 );

                    cable.modemChanged();
                    cable.connectionsChanged();
                    if( !world.isClient && !player.getAbilities().creativeMode )
                    {
                        Block.dropStack( world, pos, item );
                    }

                    return false;
                }
            }
        }

        return true;
    }

    @Nonnull
    @Override
    public ItemStack getPickedStack( BlockState state, BlockView world, BlockPos pos, @Nullable PlayerEntity player, HitResult hit )
    {
        Direction modem = state.get( MODEM ).getFacing();
        boolean cable = state.get( CABLE );

        // If we've only got one, just use that.
        if( !cable ) return new ItemStack( Registry.ModItems.WIRED_MODEM );
        if( modem == null ) return new ItemStack( Registry.ModItems.CABLE );

        // We've a modem and cable, so try to work out which one we're interacting with
        return hit != null && WorldUtil.isVecInside( CableShapes.getModemShape( state ), hit.getPos().subtract( pos.getX(), pos.getY(), pos.getZ() ) )
            ? new ItemStack( Registry.ModItems.WIRED_MODEM )
            : new ItemStack( Registry.ModItems.CABLE );
    }

    @Override
    public void onPlaced( World world, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity placer, @Nonnull ItemStack stack )
    {
        BlockEntity tile = world.getBlockEntity( pos );
        if( tile instanceof TileCable cable )
        {
            if( cable.hasCable() ) cable.connectionsChanged();
        }

        super.onPlaced( world, pos, state, placer, stack );
    }

    @Nonnull
    @Override
    @Deprecated
    public FluidState getFluidState( @Nonnull BlockState state )
    {
        return WaterloggableHelpers.getFluidState( state );
    }

    @Nonnull
    @Override
    @Deprecated
    public BlockState getStateForNeighborUpdate( @Nonnull BlockState state, @Nonnull Direction side, @Nonnull BlockState otherState, @Nonnull WorldAccess world, @Nonnull BlockPos pos, @Nonnull BlockPos otherPos )
    {
        WaterloggableHelpers.updateShape( state, world, pos );
        // Should never happen, but handle the case where we've no modem or cable.
        if( !state.get( CABLE ) && state.get( MODEM ) == CableModemVariant.None )
        {
            return getFluidState( state ).getBlockState();
        }

        return state.with( CONNECTIONS.get( side ), doesConnectVisually( state, world, pos, side ) );
    }

    @Override
    @Deprecated
    public boolean canPlaceAt( BlockState state, @Nonnull WorldView world, @Nonnull BlockPos pos )
    {
        Direction facing = state.get( MODEM ).getFacing();
        if( facing == null ) return true;

        return sideCoversSmallSquare( world, pos.offset( facing ), facing.getOpposite() );
    }

    @Nullable
    @Override
    public BlockState getPlacementState( @Nonnull ItemPlacementContext context )
    {
        BlockState state = getDefaultState()
            .with( WATERLOGGED, getFluidStateForPlacement( context ) );

        if( context.getStack().getItem() instanceof ItemBlockCable.Cable )
        {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            return correctConnections( world, pos, state.with( CABLE, true ) );
        }
        else
        {
            return state.with( MODEM, CableModemVariant.from( context.getSide().getOpposite() ) );
        }
    }

    public static BlockState correctConnections( World world, BlockPos pos, BlockState state )
    {
        if( state.get( CABLE ) )
        {
            return state
                .with( NORTH, doesConnectVisually( state, world, pos, Direction.NORTH ) )
                .with( SOUTH, doesConnectVisually( state, world, pos, Direction.SOUTH ) )
                .with( EAST, doesConnectVisually( state, world, pos, Direction.EAST ) )
                .with( WEST, doesConnectVisually( state, world, pos, Direction.WEST ) )
                .with( UP, doesConnectVisually( state, world, pos, Direction.UP ) )
                .with( DOWN, doesConnectVisually( state, world, pos, Direction.DOWN ) );
        }
        else
        {
            return state
                .with( NORTH, false ).with( SOUTH, false ).with( EAST, false )
                .with( WEST, false ).with( UP, false ).with( DOWN, false );
        }
    }
}
