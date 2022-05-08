/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.media.items;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.Registry;
import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.util.Colour;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import java.util.List;

public class ItemDisk extends Item implements IMedia, IColouredItem
{
    private static final String NBT_ID = "DiskId";

    public ItemDisk( Settings settings )
    {
        super( settings );
    }

    @Nonnull
    public static ItemStack createFromIDAndColour( int id, String label, int colour )
    {
        ItemStack stack = new ItemStack( Registry.ModItems.DISK );
        setDiskID( stack, id );
        Registry.ModItems.DISK.setLabel( stack, label );
        IColouredItem.setColourBasic( stack, colour );
        return stack;
    }

    @Override
    public void appendStacks( @Nonnull ItemGroup tabs, @Nonnull DefaultedList<ItemStack> list )
    {
        if( !isIn( tabs ) ) return;
        for( int colour = 0; colour < 16; colour++ )
        {
            list.add( createFromIDAndColour( -1, null, Colour.VALUES[colour].getHex() ) );
        }
    }

    @Override
    public void appendTooltip( @Nonnull ItemStack stack, @Nullable World world, @Nonnull List<Text> list, TooltipContext options )
    {
        if( options.isAdvanced() )
        {
            int id = getDiskID( stack );
            if( id >= 0 )
            {
                list.add( new TranslatableText( "gui.computercraft.tooltip.disk_id", id )
                    .formatted( Formatting.GRAY ) );
            }
        }
    }

    @Override
    public String getLabel( @Nonnull ItemStack stack )
    {
        return stack.hasCustomName() ? stack.getName().getString() : null;
    }

    @Override
    public boolean setLabel( @Nonnull ItemStack stack, String label )
    {
        if( label != null )
        {
            stack.setCustomName( new LiteralText( label ) );
        }
        else
        {
            stack.removeCustomName();
        }
        return true;
    }

    @Override
    public IMount createDataMount( @Nonnull ItemStack stack, @Nonnull World world )
    {
        int diskID = getDiskID( stack );
        if( diskID < 0 )
        {
            diskID = ComputerCraftAPI.createUniqueNumberedSaveDir( world, "disk" );
            setDiskID( stack, diskID );
        }
        return ComputerCraftAPI.createSaveDirMount( world, "disk/" + diskID, ComputerCraft.floppySpaceLimit );
    }

    public static int getDiskID( @Nonnull ItemStack stack )
    {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.contains( NBT_ID ) ? nbt.getInt( NBT_ID ) : -1;
    }

    private static void setDiskID( @Nonnull ItemStack stack, int id )
    {
        if( id >= 0 ) stack.getOrCreateNbt().putInt( NBT_ID, id );
    }

    @Override
    public int getColour( @Nonnull ItemStack stack )
    {
        int colour = IColouredItem.getColourBasic( stack );
        return colour == -1 ? Colour.WHITE.getHex() : colour;
    }
}
