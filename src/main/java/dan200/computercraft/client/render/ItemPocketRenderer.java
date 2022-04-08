/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.gui.FixedWidthFontRenderer;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.core.ClientComputer;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.pocket.items.ItemPocketComputer;
import dan200.computercraft.shared.util.Colour;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static dan200.computercraft.client.gui.FixedWidthFontRenderer.*;
import static dan200.computercraft.client.render.ComputerBorderRenderer.*;

/**
 * Emulates map rendering for pocket computers.
 */
public final class ItemPocketRenderer extends ItemMapLikeRenderer
{
    public static final ItemPocketRenderer INSTANCE = new ItemPocketRenderer();

    private ItemPocketRenderer()
    {
    }

    @Override
    protected void renderItem( PoseStack transform, MultiBufferSource bufferSource, ItemStack stack, int light )
    {
        ClientComputer computer = ItemPocketComputer.createClientComputer( stack );
        Terminal terminal = computer == null ? null : computer.getTerminal();

        int termWidth, termHeight;
        if( terminal == null )
        {
            termWidth = ComputerCraft.pocketTermWidth;
            termHeight = ComputerCraft.pocketTermHeight;
        }
        else
        {
            termWidth = terminal.getWidth();
            termHeight = terminal.getHeight();
        }

        int width = termWidth * FONT_WIDTH + MARGIN * 2;
        int height = termHeight * FONT_HEIGHT + MARGIN * 2;

        // Setup various transformations. Note that these are partially adapted from the corresponding method
        // in ItemRenderer
        transform.pushPose();
        transform.mulPose( Vector3f.YP.rotationDegrees( 180f ) );
        transform.mulPose( Vector3f.ZP.rotationDegrees( 180f ) );
        transform.scale( 0.5f, 0.5f, 0.5f );

        float scale = 0.75f / Math.max( width + BORDER * 2, height + BORDER * 2 + LIGHT_HEIGHT );
        // Avoid PoseStack#scale to preserve normal matrix, and fix the normals ourselves.
        transform.last().pose().multiply( Matrix4f.createScaleMatrix( scale, scale, -1.0f ) );
        transform.last().normal().mul( -1.0f );
        transform.translate( -0.5 * width, -0.5 * height, 0 );

        // Render the main frame
        ItemPocketComputer item = (ItemPocketComputer) stack.getItem();
        ComputerFamily family = item.getFamily();
        int frameColour = item.getColour( stack );

        renderFrame( transform, bufferSource, family, frameColour, light, width, height );

        // Render the light
        int lightColour = ItemPocketComputer.getLightState( stack );
        if( lightColour == -1 ) lightColour = Colour.BLACK.getHex();
        renderLight( transform, bufferSource, lightColour, width, height );

        if( computer != null && terminal != null )
        {
            FixedWidthFontRenderer.drawTerminal(
                    FixedWidthFontRenderer.toVertexConsumer( transform, bufferSource.getBuffer( RenderTypes.ITEM_POCKET_TERMINAL ) ),
                MARGIN, MARGIN, terminal, !computer.isColour(), MARGIN, MARGIN, MARGIN, MARGIN
            );
        }
        else
        {
            FixedWidthFontRenderer.drawEmptyTerminal(
                FixedWidthFontRenderer.toVertexConsumer( transform, bufferSource.getBuffer( RenderTypes.ITEM_POCKET_TERMINAL ) ),
                0, 0, width, height
            );
        }

        transform.popPose();
    }

    private static void renderFrame( PoseStack transform, MultiBufferSource render, ComputerFamily family, int colour, int light, int width, int height )
    {
        ResourceLocation texture = colour != -1 ? ComputerBorderRenderer.BACKGROUND_COLOUR : ComputerBorderRenderer.getTexture( family );

        float r = ((colour >>> 16) & 0xFF) / 255.0f;
        float g = ((colour >>> 8) & 0xFF) / 255.0f;
        float b = (colour & 0xFF) / 255.0f;

        VertexConsumer buffer = render.getBuffer( RenderTypes.itemPocketBorder( texture ) );
        ComputerBorderRenderer.render( transform, buffer, 0, 0, 0, light, width, height, true, r, g, b );
    }

    private static void renderLight( PoseStack transform, MultiBufferSource render, int colour, int width, int height )
    {
        byte r = (byte) ((colour >>> 16) & 0xFF);
        byte g = (byte) ((colour >>> 8) & 0xFF);
        byte b = (byte) (colour & 0xFF);
        var c = new byte[] { r, g, b, (byte) 255 };
        float z = 0.001f;

        VertexConsumer buffer = render.getBuffer( RenderTypes.ITEM_POCKET_LIGHT );
        FixedWidthFontRenderer.drawQuad(
            FixedWidthFontRenderer.toVertexConsumer( transform, buffer ),
            width - LIGHT_HEIGHT * 2, height + BORDER / 2.0f, 0.001f, LIGHT_HEIGHT * 2, LIGHT_HEIGHT,
            c, RenderTypes.FULL_BRIGHT_LIGHTMAP
        );
    }
}
