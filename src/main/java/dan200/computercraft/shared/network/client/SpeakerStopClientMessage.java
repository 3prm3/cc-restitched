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
import net.minecraft.network.PacketByteBuf;
import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Stops a sound on the client
 *
 * Called when a speaker is broken.
 *
 * @see dan200.computercraft.shared.peripheral.speaker.TileSpeaker
 */
public class SpeakerStopClientMessage implements NetworkMessage
{
    private final UUID source;

    public SpeakerStopClientMessage( UUID source )
    {
        this.source = source;
    }

    public SpeakerStopClientMessage( PacketByteBuf buf )
    {
        source = buf.readUuid();
    }

    @Override
    public void toBytes( @Nonnull PacketByteBuf buf )
    {
        buf.writeUuid( source );
    }

    @Override
    @Environment( EnvType.CLIENT )
    public void handle( PacketContext context )
    {
        SpeakerManager.stopSound( source );
    }
}
