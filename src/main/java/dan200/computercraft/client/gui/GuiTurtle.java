/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.WidgetTerminal;
import dan200.computercraft.client.render.ComputerBorderRenderer;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.turtle.inventory.ContainerTurtle;
import javax.annotation.Nonnull;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static dan200.computercraft.shared.turtle.inventory.ContainerTurtle.*;

public class GuiTurtle extends ComputerScreenBase<ContainerTurtle>
{
    private static final Identifier BACKGROUND_NORMAL = new Identifier( ComputerCraft.MOD_ID, "textures/gui/turtle_normal.png" );
    private static final Identifier BACKGROUND_ADVANCED = new Identifier( ComputerCraft.MOD_ID, "textures/gui/turtle_advanced.png" );

    private static final int TEX_WIDTH = 254;
    private static final int TEX_HEIGHT = 217;

    private final ComputerFamily family;

    public GuiTurtle( ContainerTurtle container, PlayerInventory player, Text title )
    {
        super( container, player, title, BORDER );
        family = container.getFamily();

        backgroundWidth = TEX_WIDTH + ComputerSidebar.WIDTH;
        backgroundHeight = TEX_HEIGHT;
    }

    @Override
    protected WidgetTerminal createTerminal()
    {
        return new WidgetTerminal(
            computer, x + BORDER + ComputerSidebar.WIDTH, y + BORDER,
            ComputerCraft.turtleTermWidth, ComputerCraft.turtleTermHeight
        );
    }

    @Override
    protected void drawBackground( @Nonnull MatrixStack transform, float partialTicks, int mouseX, int mouseY )
    {
        boolean advanced = family == ComputerFamily.ADVANCED;
        RenderSystem.setShaderTexture( 0, advanced ? BACKGROUND_ADVANCED : BACKGROUND_NORMAL );
        drawTexture( transform, x + ComputerSidebar.WIDTH, y, 0, 0, TEX_WIDTH, TEX_HEIGHT );

        int slot = getScreenHandler().getSelectedSlot();
        if( slot >= 0 )
        {
            RenderSystem.setShaderColor( 1.0F, 1.0F, 1.0F, 1.0F );
            int slotX = slot % 4;
            int slotY = slot / 4;
            drawTexture( transform,
                x + TURTLE_START_X - 2 + slotX * 18, y + PLAYER_START_Y - 2 + slotY * 18,
                0, 217, 24, 24
            );
        }

        RenderSystem.setShaderTexture( 0, advanced ? ComputerBorderRenderer.BACKGROUND_ADVANCED : ComputerBorderRenderer.BACKGROUND_NORMAL );
        ComputerSidebar.renderBackground( transform, x, y + sidebarYOffset );
    }
}
