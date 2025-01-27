package com.prefab.structures.gui;

import com.prefab.Tuple;
import com.prefab.gui.GuiBase;
import com.prefab.gui.GuiLangKeys;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;

public class GuiNoOptions extends GuiBase {
    protected Button btnCancel;

    public GuiNoOptions() {
        super("No Options");
    }

    @Override
    protected void Initialize() {
        this.modifiedInitialXAxis = 140;
        this.modifiedInitialYAxis = 77;
        this.imagePanelWidth = 285;
        this.imagePanelHeight = 200;

        // Get the upper left-hand corner of the GUI box.
        Tuple<Integer, Integer> adjustedCorner = this.getAdjustedXYValue();
        int grayBoxX = adjustedCorner.getFirst();
        int grayBoxY = adjustedCorner.getSecond();

        // Create the done and cancel buttons.
        this.btnCancel = this.createAndAddButton(grayBoxX + 97, grayBoxY + 102, 90, 20, GuiLangKeys.GUI_BUTTON_CANCEL);
    }

    @Override
    protected void preButtonRender(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        this.drawStandardControlBoxAndImage(guiGraphics, null, x, y, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void postButtonRender(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        this.drawSplitString(guiGraphics, GuiLangKeys.translateString(GuiLangKeys.NO_OPTIONS_PART_1), x + 8, y + 15, 260, this.textColor);
        this.drawSplitString(guiGraphics, GuiLangKeys.translateString(GuiLangKeys.NO_OPTIONS_PART_2), x + 8, y + 50, 260, this.textColor);
    }

    @Override
    public void buttonClicked(AbstractButton button) {
        if (button == this.btnCancel) {
            this.closeScreen();
        }
    }
}
