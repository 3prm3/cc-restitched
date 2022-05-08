/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.pocket.peripherals;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPosition;
import dan200.computercraft.shared.peripheral.speaker.UpgradeSpeakerPeripheral;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PocketSpeakerPeripheral extends UpgradeSpeakerPeripheral
{
    private final IPocketAccess access;
    private World level;
    private Vec3d position = Vec3d.ZERO;

    public PocketSpeakerPeripheral( IPocketAccess access )
    {
        this.access = access;
    }

    @Nonnull
    @Override
    public SpeakerPosition getPosition()
    {
        Entity entity = access.getEntity();
        return entity == null ? SpeakerPosition.of( level, position ) : SpeakerPosition.of( entity );
    }

    @Override
    public boolean equals( IPeripheral other )
    {
        return other instanceof PocketSpeakerPeripheral;
    }

    @Override
    public void update()
    {
        Entity entity = access.getEntity();
        if( entity != null )
        {
            level = entity.world;
            position = entity.getPos();
        }

        super.update();

        access.setLight( madeSound() ? 0x3320fc : -1 );
    }
}
