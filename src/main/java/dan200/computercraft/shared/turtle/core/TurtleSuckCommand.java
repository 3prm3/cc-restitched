/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleAnimation;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.util.InventoryUtil;
import dan200.computercraft.shared.util.ItemStorage;
import javax.annotation.Nonnull;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import java.util.List;

public class TurtleSuckCommand implements ITurtleCommand
{
    private final InteractDirection direction;
    private final int quantity;

    public TurtleSuckCommand( InteractDirection direction, int quantity )
    {
        this.direction = direction;
        this.quantity = quantity;
    }

    @Nonnull
    @Override
    public TurtleCommandResult execute( @Nonnull ITurtleAccess turtle )
    {
        // Sucking nothing is easy
        if( quantity == 0 )
        {
            turtle.playAnimation( TurtleAnimation.WAIT );
            return TurtleCommandResult.success();
        }

        // Get world direction from direction
        Direction direction = this.direction.toWorldDir( turtle );

        // Get inventory for thing in front
        World world = turtle.getLevel();
        BlockPos turtlePosition = turtle.getPosition();
        BlockPos blockPosition = turtlePosition.offset( direction );
        Direction side = direction.getOpposite();

        Inventory inventoryContainer = InventoryUtil.getInventory( world, blockPosition, side );

        if( inventoryContainer != null )
        {
            ItemStorage inventory = ItemStorage.wrap( inventoryContainer );

            // Take from inventory of thing in front
            ItemStack stack = InventoryUtil.takeItems( quantity, inventory );
            if( stack.isEmpty() ) return TurtleCommandResult.failure( "No items to take" );

            // Try to place into the turtle
            ItemStack remainder = InventoryUtil.storeItems( stack, turtle.getItemHandler(), turtle.getSelectedSlot() );
            if( !remainder.isEmpty() )
            {
                // Put the remainder back in the inventory
                InventoryUtil.storeItems( remainder, inventory );
            }

            // Return true if we consumed anything
            if( remainder != stack )
            {
                turtle.playAnimation( TurtleAnimation.WAIT );
                return TurtleCommandResult.success();
            }
            else
            {
                return TurtleCommandResult.failure( "No space for items" );
            }
        }
        else
        {
            // Suck up loose items off the ground
            Box aabb = new Box(
                blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(),
                blockPosition.getX() + 1.0, blockPosition.getY() + 1.0, blockPosition.getZ() + 1.0
            );
            List<ItemEntity> list = world.getEntitiesByClass( ItemEntity.class, aabb, EntityPredicates.VALID_ENTITY );
            if( list.isEmpty() ) return TurtleCommandResult.failure( "No items to take" );

            for( ItemEntity entity : list )
            {
                // Suck up the item
                ItemStack stack = entity.getStack().copy();

                ItemStack storeStack;
                ItemStack leaveStack;
                if( stack.getCount() > quantity )
                {
                    storeStack = stack.split( quantity );
                    leaveStack = stack;
                }
                else
                {
                    storeStack = stack;
                    leaveStack = ItemStack.EMPTY;
                }

                ItemStack remainder = InventoryUtil.storeItems( storeStack, turtle.getItemHandler(), turtle.getSelectedSlot() );

                if( remainder != storeStack )
                {
                    if( remainder.isEmpty() && leaveStack.isEmpty() )
                    {
                        entity.discard();
                    }
                    else if( remainder.isEmpty() )
                    {
                        entity.setStack( leaveStack );
                    }
                    else if( leaveStack.isEmpty() )
                    {
                        entity.setStack( remainder );
                    }
                    else
                    {
                        leaveStack.increment( remainder.getCount() );
                        entity.setStack( leaveStack );
                    }

                    // Play fx
                    world.syncGlobalEvent( 1000, turtlePosition, 0 ); // BLOCK_DISPENSER_DISPENSE
                    turtle.playAnimation( TurtleAnimation.WAIT );
                    return TurtleCommandResult.success();
                }
            }


            return TurtleCommandResult.failure( "No space for items" );
        }
    }
}
