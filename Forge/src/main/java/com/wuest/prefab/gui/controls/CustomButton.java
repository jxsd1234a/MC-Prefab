package com.wuest.prefab.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wuest.prefab.Utils;
import com.wuest.prefab.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CustomButton extends ExtendedButton {
    private final ResourceLocation buttonTexture = new ResourceLocation("prefab", "textures/gui/prefab_button.png");
    private final ResourceLocation buttonTexturePressed = new ResourceLocation("prefab", "textures/gui/prefab_button_pressed.png");
    private final ResourceLocation buttonTextureHover = new ResourceLocation("prefab", "textures/gui/prefab_button_highlight.png");

    public CustomButton(int xPos, int yPos, Component displayString, Button.OnPress handler) {
        super(xPos, yPos, 200, 90, displayString, handler, null);
    }

    public CustomButton(int xPos, int yPos, int width, int height, Component displayString, Button.OnPress handler) {
        super(xPos, yPos, width, height, displayString, handler, null);
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            Minecraft mc = Minecraft.getInstance();
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            ResourceLocation buttonTexture = this.isHovered ? this.buttonTextureHover : this.buttonTexture;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, buttonTexture);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

            GuiUtils.bindAndDrawScaledTexture(buttonTexture, guiGraphics, this.getX(), this.getY(), this.width, this.height, 90, 20, 90, 20);
            int color = 14737632;

            Component buttonText = this.getMessage();
            int strWidth = mc.font.width(buttonText);
            int ellipsisWidth = mc.font.width("...");

            if (strWidth > width - 6 && strWidth > ellipsisWidth)
                buttonText = Utils.createTextComponent(mc.font.substrByWidth(buttonText, width - 6 - ellipsisWidth).getString() + "...");

            guiGraphics.drawCenteredString(mc.font, buttonText, this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, color);
            guiGraphics.flush();
        }
    }
}
