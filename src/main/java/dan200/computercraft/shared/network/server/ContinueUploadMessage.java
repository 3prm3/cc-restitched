/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.shared.network.server;

import dan200.computercraft.shared.computer.core.IContainerComputer;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.network.PacketContext;
import javax.annotation.Nonnull;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ContinueUploadMessage extends ComputerServerMessage
{
    private final boolean overwrite;

    public ContinueUploadMessage( int instanceId, boolean overwrite )
    {
        super( instanceId );
        this.overwrite = overwrite;
    }

    public ContinueUploadMessage( @Nonnull PacketByteBuf buf )
    {
        super( buf );
        overwrite = buf.readBoolean();
    }

    @Override
    public void toBytes( @Nonnull PacketByteBuf buf )
    {
        super.toBytes( buf );
        buf.writeBoolean( overwrite );
    }

    @Override
    protected void handle( PacketContext context, @Nonnull ServerComputer computer, @Nonnull IContainerComputer container )
    {
        ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
        if( player != null ) container.confirmUpload( player, overwrite );
    }
}
