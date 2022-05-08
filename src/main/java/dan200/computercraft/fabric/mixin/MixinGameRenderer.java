/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.fabric.mixin;

import com.mojang.datafixers.util.Pair;
import dan200.computercraft.client.render.MonitorTextureBufferShader;
import dan200.computercraft.client.render.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gl.Program;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import net.minecraft.resource.ResourceManager;

@Mixin( GameRenderer.class )
public class MixinGameRenderer
{
    @Inject( method = "reloadShaders", at = @At( value = "INVOKE_ASSIGN", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 53 ), locals = LocalCapture.CAPTURE_FAILSOFT )
    private void reloadShaders( ResourceManager manager, CallbackInfo info, List<Program> list, List<Pair<Shader, Consumer<Shader>>> list2 ) throws IOException
    {
        list2.add( Pair.of( new MonitorTextureBufferShader(
            manager,
            "monitor_tbo",
            RenderTypes.MONITOR_TBO.getVertexFormat()
        ), shader -> RenderTypes.monitorTboShader = (MonitorTextureBufferShader) shader ) );
    }
}
