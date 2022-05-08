/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.client.sound;

import dan200.computercraft.shared.peripheral.speaker.SpeakerPosition;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maps speakers source IDs to a {@link SpeakerInstance}.
 */
public class SpeakerManager
{
    private static final Map<UUID, SpeakerInstance> sounds = new ConcurrentHashMap<>();

    // A return value of true cancels the event
    public static boolean playStreaming( SoundEngine engine, SoundInstance soundInstance, Channel channel )
    {
        if( !(soundInstance instanceof SpeakerSound sound) || sound.stream == null ) return false;

        channel.attachBufferStream( sound.stream );
        channel.play();

        sound.channel = channel;
        sound.executor = engine.executor;
        return true;
    }

    public static SpeakerInstance getSound( UUID source )
    {
        return sounds.computeIfAbsent( source, x -> new SpeakerInstance() );
    }

    public static void stopSound( UUID source )
    {
        SpeakerInstance sound = sounds.remove( source );
        if( sound != null ) sound.stop();
    }

    public static void moveSound( UUID source, SpeakerPosition position )
    {
        SpeakerInstance sound = sounds.get( source );
        if( sound != null ) sound.setPosition( position );
    }

    public static void reset()
    {
        sounds.clear();
    }
}
