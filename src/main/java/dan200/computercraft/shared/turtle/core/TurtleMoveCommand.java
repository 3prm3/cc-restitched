/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleAnimation;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.TurtlePermissions;
import dan200.computercraft.shared.util.WorldUtil;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import java.util.List;

public class TurtleMoveCommand implements ITurtleCommand
{
    private final MoveDirection direction;

    public TurtleMoveCommand( MoveDirection direction )
    {
        this.direction = direction;
    }

    @Nonnull
    @Override
    public TurtleCommandResult execute( @Nonnull ITurtleAccess turtle )
    {
        // Get world direction from direction
        Direction direction = this.direction.toWorldDir( turtle );

        // Check if we can move
        World oldWorld = turtle.getLevel();
        BlockPos oldPosition = turtle.getPosition();
        BlockPos newPosition = oldPosition.offset( direction );

        TurtlePlayer turtlePlayer = TurtlePlayer.getWithPosition( turtle, oldPosition, direction );
        TurtleCommandResult canEnterResult = canEnter( turtlePlayer, oldWorld, newPosition );
        if( !canEnterResult.isSuccess() )
        {
            return canEnterResult;
        }

        // Check existing block is air or replaceable
        BlockState state = oldWorld.getBlockState( newPosition );
        if( !oldWorld.isAir( newPosition ) &&
            !WorldUtil.isLiquidBlock( oldWorld, newPosition ) &&
            !state.getMaterial().isReplaceable() )
        {
            return TurtleCommandResult.failure( "Movement obstructed" );
        }

        // Check there isn't anything in the way
        VoxelShape collision = state.getCollisionShape( oldWorld, oldPosition ).offset(
            newPosition.getX(),
            newPosition.getY(),
            newPosition.getZ()
        );

        if( !oldWorld.doesNotIntersectEntities( null, collision ) )
        {
            if( !ComputerCraft.turtlesCanPush || this.direction == MoveDirection.UP || this.direction == MoveDirection.DOWN )
            {
                return TurtleCommandResult.failure( "Movement obstructed" );
            }

            // Check there is space for all the pushable entities to be pushed
            List<Entity> list = oldWorld.getEntitiesByClass( Entity.class, getBox( collision ), x -> x != null && x.isAlive() && x.intersectionChecked );
            for( Entity entity : list )
            {
                Box pushedBB = entity.getBoundingBox().offset(
                    direction.getOffsetX(),
                    direction.getOffsetY(),
                    direction.getOffsetZ()
                );
                if( !oldWorld.doesNotIntersectEntities( null, VoxelShapes.cuboid( pushedBB ) ) )
                {
                    return TurtleCommandResult.failure( "Movement obstructed" );
                }
            }
        }

        // Check fuel level
        if( turtle.isFuelNeeded() && turtle.getFuelLevel() < 1 )
        {
            return TurtleCommandResult.failure( "Out of fuel" );
        }

        // Move
        if( !turtle.teleportTo( oldWorld, newPosition ) ) return TurtleCommandResult.failure( "Movement failed" );

        // Consume fuel
        turtle.consumeFuel( 1 );

        // Animate
        switch( this.direction )
        {
            case FORWARD:
            default:
                turtle.playAnimation( TurtleAnimation.MOVE_FORWARD );
                break;
            case BACK:
                turtle.playAnimation( TurtleAnimation.MOVE_BACK );
                break;
            case UP:
                turtle.playAnimation( TurtleAnimation.MOVE_UP );
                break;
            case DOWN:
                turtle.playAnimation( TurtleAnimation.MOVE_DOWN );
                break;
        }
        return TurtleCommandResult.success();
    }

    private static TurtleCommandResult canEnter( TurtlePlayer turtlePlayer, World world, BlockPos position )
    {
        if( world.isOutOfHeightLimit( position ) )
        {
            return TurtleCommandResult.failure( position.getY() < 0 ? "Too low to move" : "Too high to move" );
        }
        if( !world.isInBuildLimit( position ) ) return TurtleCommandResult.failure( "Cannot leave the world" );

        // Check spawn protection
        if( ComputerCraft.turtlesObeyBlockProtection && !TurtlePermissions.isBlockEnterable( world, position, turtlePlayer ) )
        {
            return TurtleCommandResult.failure( "Cannot enter protected area" );
        }

        if( !world.canSetBlock( position ) ) return TurtleCommandResult.failure( "Cannot leave loaded world" );
        if( !world.getWorldBorder().contains( position ) )
        {
            return TurtleCommandResult.failure( "Cannot pass the world border" );
        }

        return TurtleCommandResult.success();
    }

    private static Box getBox( VoxelShape shape )
    {
        return shape.isEmpty() ? EMPTY_BOX : shape.getBoundingBox();
    }

    private static final Box EMPTY_BOX = new Box( 0, 0, 0, 0, 0, 0 );
}
