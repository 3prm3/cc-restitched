/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.util;

import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

public final class ColourUtils
{
    private ColourUtils() {}

    public static DyeColor getStackColour( ItemStack stack )
    {
        if( stack.isEmpty() ) return null;

        Item item = stack.getItem();
        return item instanceof DyeItem di ? di.getColor() : null;
    }
}
