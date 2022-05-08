/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.client.render;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.render.text.FixedWidthFontRenderer;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.core.ClientComputer;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.pocket.items.ItemPocketComputer;
import dan200.computercraft.shared.util.Colour;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import static dan200.computercraft.client.render.ComputerBorderRenderer.*;
import static dan200.computercraft.client.render.text.FixedWidthFontRenderer.FONT_HEIGHT;
import static dan200.computercraft.client.render.text.FixedWidthFontRenderer.FONT_WIDTH;

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
    protected void renderItem( MatrixStack transform, VertexConsumerProvider bufferSource, ItemStack stack, int light )
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
        transform.push();
        transform.multiply( Vec3f.POSITIVE_Y.getDegreesQuaternion( 180f ) );
        transform.multiply( Vec3f.POSITIVE_Z.getDegreesQuaternion( 180f ) );
        transform.scale( 0.5f, 0.5f, 0.5f );

        float scale = 0.75f / Math.max( width + BORDER * 2, height + BORDER * 2 + LIGHT_HEIGHT );
        // Avoid PoseStack#scale to preserve normal matrix, and fix the normals ourselves.
        transform.peek().getPositionMatrix().multiply( Matrix4f.scale( scale, scale, -1.0f ) );
        transform.peek().getNormalMatrix().multiply( -1.0f );
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

        transform.pop();
    }

    private static void renderFrame( MatrixStack transform, VertexConsumerProvider render, ComputerFamily family, int colour, int light, int width, int height )
    {
        Identifier texture = colour != -1 ? ComputerBorderRenderer.BACKGROUND_COLOUR : ComputerBorderRenderer.getTexture( family );

        float r = ((colour >>> 16) & 0xFF) / 255.0f;
        float g = ((colour >>> 8) & 0xFF) / 255.0f;
        float b = (colour & 0xFF) / 255.0f;

        VertexConsumer buffer = render.getBuffer( RenderTypes.itemPocketBorder( texture ) );
        ComputerBorderRenderer.render( transform, buffer, 0, 0, 0, light, width, height, true, r, g, b );
    }

    private static void renderLight( MatrixStack transform, VertexConsumerProvider render, int colour, int width, int height )
    {
        byte r = (byte) ((colour >>> 16) & 0xFF);
        byte g = (byte) ((colour >>> 8) & 0xFF);
        byte b = (byte) (colour & 0xFF);
        byte[] c = new byte[] { r, g, b, (byte) 255 };

        VertexConsumer buffer = render.getBuffer( RenderTypes.ITEM_POCKET_LIGHT );
        FixedWidthFontRenderer.drawQuad(
            FixedWidthFontRenderer.toVertexConsumer( transform, buffer ),
            width - LIGHT_HEIGHT * 2, height + BORDER / 2.0f, 0.001f, LIGHT_HEIGHT * 2, LIGHT_HEIGHT,
            c, RenderTypes.FULL_BRIGHT_LIGHTMAP
        );
    }
}
