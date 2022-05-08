/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.network.server;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.network.NetworkMessage;
import dan200.computercraft.shared.network.PacketContext;
import javax.annotation.Nonnull;
import net.minecraft.network.PacketByteBuf;

public class RequestComputerMessage implements NetworkMessage
{
    private final int instance;

    public RequestComputerMessage( int instance )
    {
        this.instance = instance;
    }

    public RequestComputerMessage( @Nonnull PacketByteBuf buf )
    {
        instance = buf.readVarInt();
    }

    @Override
    public void toBytes( @Nonnull PacketByteBuf buf )
    {
        buf.writeVarInt( instance );
    }

    @Override
    public void handle( PacketContext context )
    {
        ServerComputer computer = ComputerCraft.serverComputerRegistry.get( instance );
        if( computer != null ) computer.sendComputerState( context.getPlayer() );
    }
}
