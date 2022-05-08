/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.fabric.mixin;

import dan200.computercraft.shared.command.ClientCommands;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( Screen.class )
@Environment( EnvType.CLIENT )
public class MixinScreen
{
    @Inject( method = "sendMessage(Ljava/lang/String;Z)V", at = @At( "HEAD" ), cancellable = true )
    public void sendClientCommand( String message, boolean add, CallbackInfo info )
    {
        if( ClientCommands.onClientSendMessage( message ) )
        {
            info.cancel();
        }
    }
}
