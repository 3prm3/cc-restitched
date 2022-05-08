/*
 * This file is part of the public ComputerCraft API - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. This API may be redistributed unmodified and in full only.
 * For help using the API, and posting your mods, visit the forums at computercraft.info.
 */
package dan200.computercraft.api.peripheral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * A {@link net.minecraft.block.entity.BlockEntity} which may act as a peripheral.
 *
 * If you need more complex capabilities (such as handling TEs not belonging to your mod), you should use {@link IPeripheralProvider}.
 */
public interface IPeripheralTile
{
    /**
     * Get the peripheral on the given {@code side}.
     *
     * @param side The side to get the peripheral from.
     * @return A peripheral, or {@code null} if there is not a peripheral here.
     * @see IPeripheralProvider#getPeripheral(World, BlockPos, Direction)
     */
    @Nullable
    IPeripheral getPeripheral( @Nonnull Direction side );
}
