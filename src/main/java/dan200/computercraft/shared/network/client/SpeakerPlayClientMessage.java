/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.network.client;

import dan200.computercraft.client.sound.SpeakerManager;
import dan200.computercraft.shared.network.NetworkMessage;
import dan200.computercraft.shared.network.PacketContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPosition;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Starts a sound on the client.
 *
 * Used by speakers to play sounds.
 *
 * @see dan200.computercraft.shared.peripheral.speaker.TileSpeaker
 */
public class SpeakerPlayClientMessage implements NetworkMessage
{
    private final UUID source;
    private final SpeakerPosition.Message pos;
    private final ResourceLocation sound;
    private final float volume;
    private final float pitch;

    public SpeakerPlayClientMessage( UUID source, SpeakerPosition pos, ResourceLocation event, float volume, float pitch )
    {
        this.source = source;
        this.pos = pos.asMessage();
        sound = event;
        this.volume = volume;
        this.pitch = pitch;
    }

    public SpeakerPlayClientMessage( FriendlyByteBuf buf )
    {
        source = buf.readUUID();
        pos = SpeakerPosition.Message.read( buf );
        sound = buf.readResourceLocation();
        volume = buf.readFloat();
        pitch = buf.readFloat();
    }

    @Override
    public void toBytes( @Nonnull FriendlyByteBuf buf )
    {
        buf.writeUUID( source );
        pos.write( buf );
        buf.writeResourceLocation( sound );
        buf.writeFloat( volume );
        buf.writeFloat( pitch );
    }

    @Override
    @Environment( EnvType.CLIENT )
    public void handle( PacketContext context )
    {
        SpeakerManager.getSound( source ).playSound( pos.reify(), sound, volume, pitch );
    }
}
