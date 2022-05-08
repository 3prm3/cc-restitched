/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2022. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */
package dan200.computercraft.client.gui;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.client.gui.widgets.ComputerSidebar;
import dan200.computercraft.client.gui.widgets.WidgetTerminal;
import dan200.computercraft.client.render.ComputerBorderRenderer;
import dan200.computercraft.shared.computer.inventory.ContainerComputerBase;
import dan200.computercraft.shared.computer.inventory.ContainerViewComputer;
import javax.annotation.Nonnull;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import static dan200.computercraft.client.render.ComputerBorderRenderer.BORDER;
import static dan200.computercraft.client.render.RenderTypes.FULL_BRIGHT_LIGHTMAP;

public final class GuiComputer<T extends ContainerComputerBase> extends ComputerScreenBase<T>
{
    private final int termWidth;
    private final int termHeight;

    private GuiComputer(
        T container, PlayerInventory player, Text title, int termWidth, int termHeight
    )
    {
        super( container, player, title, BORDER );
        this.termWidth = termWidth;
        this.termHeight = termHeight;

        backgroundWidth = WidgetTerminal.getWidth( termWidth ) + BORDER * 2 + ComputerSidebar.WIDTH;
        backgroundHeight = WidgetTerminal.getHeight( termHeight ) + BORDER * 2;
    }

    @Nonnull
    public static GuiComputer<ContainerComputerBase> create( ContainerComputerBase container, PlayerInventory inventory, Text component )
    {
        return new GuiComputer<>(
            container, inventory, component,
            ComputerCraft.computerTermWidth, ComputerCraft.computerTermHeight
        );
    }

    @Nonnull
    public static GuiComputer<ContainerComputerBase> createPocket( ContainerComputerBase container, PlayerInventory inventory, Text component )
    {
        return new GuiComputer<>(
            container, inventory, component,
            ComputerCraft.pocketTermWidth, ComputerCraft.pocketTermHeight
        );
    }

    @Nonnull
    public static GuiComputer<ContainerViewComputer> createView( ContainerViewComputer container, PlayerInventory inventory, Text component )
    {
        return new GuiComputer<>(
            container, inventory, component,
            container.getWidth(), container.getHeight()
        );
    }

    @Override
    protected WidgetTerminal createTerminal()
    {
        return new WidgetTerminal( computer,
            x + ComputerSidebar.WIDTH + BORDER, y + BORDER, termWidth, termHeight
        );
    }

    @Override
    public void drawBackground( @Nonnull MatrixStack stack, float partialTicks, int mouseX, int mouseY )
    {
        // Draw a border around the terminal
        ComputerBorderRenderer.renderFromGui(
            ComputerBorderRenderer.getTexture( family ), terminal.x, terminal.y, getZOffset(),
            FULL_BRIGHT_LIGHTMAP, terminal.getWidth(), terminal.getHeight()
        );
        ComputerSidebar.renderBackground( stack, x, y + sidebarYOffset );
    }
}
