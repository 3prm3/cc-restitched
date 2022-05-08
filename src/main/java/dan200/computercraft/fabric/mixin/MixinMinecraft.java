/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.fabric.mixin;

import dan200.computercraft.client.FrameInfo;
import dan200.computercraft.fabric.events.CustomClientEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( MinecraftClient.class )
public abstract class MixinMinecraft
{
    @Inject( method = "runTick", at = @At( "HEAD" ) )
    private void onRender( CallbackInfo info )
    {
        FrameInfo.onRenderFrame();
    }

    @Inject( method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At( "RETURN" ) )
    private void disconnectAfter( Screen screen, CallbackInfo info )
    {
        CustomClientEvents.CLIENT_UNLOAD_WORLD_EVENT.invoker().onClientUnloadWorld();
    }

    @Inject( method = "setLevel", at = @At( "RETURN" ) )
    private void setLevel( ClientWorld world, CallbackInfo info )
    {
        CustomClientEvents.CLIENT_UNLOAD_WORLD_EVENT.invoker().onClientUnloadWorld();
    }
}
