package com.prefab.structures.gui;

import com.prefab.ClientModRegistryBase;
import com.prefab.Tuple;
import com.prefab.gui.GuiLangKeys;
import com.prefab.structures.config.BulldozerConfiguration;
import com.prefab.structures.messages.StructureTagMessage;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;

/**
 * @author WuestMan
 */
public class GuiBulldozer extends GuiStructure {

    protected BulldozerConfiguration specificConfiguration;

    /**
     * Initializes a new instance of the {@link GuiBulldozer} class.
     */
    public GuiBulldozer() {
        super("Bulldozer");

        this.structureConfiguration = StructureTagMessage.EnumStructureConfiguration.Bulldozer;
    }

    @Override
    public Component getNarrationMessage() {
        return Component.translatable(GuiLangKeys.translateString(GuiLangKeys.TITLE_BULLDOZER));
    }

    @Override
    protected void Initialize() {
        this.modifiedInitialXAxis = 125;
        this.modifiedInitialYAxis = 83;
        this.imagePanelWidth = 256;
        this.imagePanelHeight = 256;

        this.configuration = this.specificConfiguration = ClientModRegistryBase.playerConfig.getClientConfig("Bulldozer", BulldozerConfiguration.class);
        this.specificConfiguration.pos = this.pos;

        // Get the upper left-hand corner of the GUI box.
        Tuple<Integer, Integer> adjustedCorner = this.getAdjustedXYValue();
        int grayBoxX = adjustedCorner.getFirst();
        int grayBoxY = adjustedCorner.getSecond();

        // Create the done and cancel buttons.
        this.btnBuild = this.createAndAddButton(grayBoxX + 10, grayBoxY + 136, 90, 20, GuiLangKeys.GUI_BUTTON_BUILD);
        this.btnCancel = this.createAndAddButton(grayBoxX + 147, grayBoxY + 136, 90, 20, GuiLangKeys.GUI_BUTTON_CANCEL);
    }

    @Override
    protected void postButtonRender(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        String strToDraw = GuiLangKeys.translateString(GuiLangKeys.GUI_BULLDOZER_DESCRIPTION) + "\n \n" + GuiLangKeys.translateString(GuiLangKeys.GUI_CLEARED_AREA);
        this.drawSplitString(guiGraphics, strToDraw, x + 10, y + 10, 230, this.textColor);
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Override
    public void buttonClicked(AbstractButton button) {
        this.configuration.houseFacing = this.getMinecraft().player.getDirection().getOpposite();
        this.performCancelOrBuildOrHouseFacing(button);
    }
}
