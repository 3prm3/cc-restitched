/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.media.items;

import dan200.computercraft.api.media.IMedia;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.TranslatableText;

/**
 * An implementation of IMedia for ItemRecords.
 */
public final class RecordMedia implements IMedia
{
    public static final RecordMedia INSTANCE = new RecordMedia();

    private RecordMedia()
    {
    }

    @Override
    public String getLabel( @Nonnull ItemStack stack )
    {
        return getAudioTitle( stack );
    }

    @Override
    public String getAudioTitle( @Nonnull ItemStack stack )
    {
        Item item = stack.getItem();
        if( !(item instanceof MusicDiscItem) ) return null;

        return new TranslatableText( item.getTranslationKey() + ".desc" ).getString();
    }

    @Override
    public SoundEvent getAudio( @Nonnull ItemStack stack )
    {
        Item item = stack.getItem();
        if( !(item instanceof MusicDiscItem) ) return null;

        return ((MusicDiscItem) item).getSound();
    }
}
