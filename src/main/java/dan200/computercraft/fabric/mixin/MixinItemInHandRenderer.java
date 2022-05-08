/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.fabric.mixin;

import dan200.computercraft.client.render.ItemPocketRenderer;
import dan200.computercraft.client.render.ItemPrintoutRenderer;
import dan200.computercraft.shared.media.items.ItemPrintout;
import dan200.computercraft.shared.pocket.items.ItemPocketComputer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( HeldItemRenderer.class )
@Environment( EnvType.CLIENT )
public class MixinItemInHandRenderer
{
    @Inject( method = "renderArmWithItem", at = @At( "HEAD" ), cancellable = true )
    public void renderFirstPersonItem(
        AbstractClientPlayerEntity player, float var2, float pitch, Hand hand, float swingProgress,
        ItemStack stack, float equipProgress, MatrixStack matrixStack, VertexConsumerProvider provider, int light,
        CallbackInfo callback
    )
    {
        if( stack.getItem() instanceof ItemPrintout )
        {
            ItemPrintoutRenderer.INSTANCE.renderItemFirstPerson( matrixStack, provider, light, hand, pitch, equipProgress, swingProgress, stack );
            callback.cancel();
        }
        else if( stack.getItem() instanceof ItemPocketComputer )
        {
            ItemPocketRenderer.INSTANCE.renderItemFirstPerson( matrixStack, provider, light, hand, pitch, equipProgress, swingProgress, stack );
            callback.cancel();
        }
    }
}
