package com.wuest.prefab.gui;

import com.mojang.serialization.Codec;
import com.wuest.prefab.Quadruple;
import com.wuest.prefab.Tuple;
import com.wuest.prefab.Utils;
import com.wuest.prefab.config.ConfigCategory;
import com.wuest.prefab.config.ConfigOption;
import com.wuest.prefab.config.ModConfiguration;
import com.wuest.prefab.gui.controls.CustomOptionsList;
import com.wuest.prefab.gui.controls.ExtendedButton;
import com.wuest.prefab.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuiPrefab extends GuiBase {
    /**
     * Distance from top of the screen to this GUI's title
     */
    private static final int TITLE_HEIGHT = 8;

    /**
     * Distance from top of the screen to the options row list's top
     */
    private static final int OPTIONS_LIST_TOP_HEIGHT = 55;

    /**
     * Height of each item in the options row list
     */
    private static final int OPTIONS_LIST_ITEM_HEIGHT = 25;

    /**
     * Distance from bottom of the screen to the "Done" button's top
     */
    private static final int DONE_BUTTON_TOP_OFFSET = 26;

    private static final int BOTTOM_SECTION_HEIGHT = 32;

    private final Screen parentScreen;
    private ExtendedButton doneButton;
    private ExtendedButton resetToDefaultsButton;
    private ExtendedButton generalGroupButton;

    private ConfigCategory currentOption = ConfigCategory.General;

    private CustomOptionsList currentRowList;
    private ArrayList<Quadruple<ConfigCategory, CustomOptionsList, CustomOptionsList, ConfigCategory>> optionCollection;
    private CustomOptionsList optionsRowList;
    private CustomOptionsList chestOptionsRowList;
    private CustomOptionsList recipeOptionsRowList;
    private CustomOptionsList starterHouseOptionsRowList;

    public GuiPrefab(Minecraft minecraft, Screen parent) {
        super("Prefab Configuration");
        this.parentScreen = parent;
        super.minecraft = minecraft;
    }

    @Nullable
    public static List<FormattedCharSequence> tooltipAt(CustomOptionsList optionsRowList, int mouseX, int mouseY) {
        if (optionsRowList.isMouseOver(mouseX, mouseY)) {
            Optional<GuiEventListener> optional = optionsRowList.getMouseOver(mouseX, mouseY);

            if (optional.isPresent()) {
                // TODO: Make tooltip accessible as it is not.
                //optional.get().too
                //return ((TooltipAccessor) optional.get()).getTooltip();
            }
        }

        return null;
    }

    @Override
    protected void Initialize() {
        // Get the upper left hand corner of the GUI box.
        Tuple<Integer, Integer> adjustedXYValue = this.getAdjustedXYValue();
        int x = adjustedXYValue.getFirst();

        this.optionCollection = new ArrayList<>();

        this.resetToDefaultsButton = this.createAndAddButton(60, this.height - DONE_BUTTON_TOP_OFFSET, 100, 20, "Reset", false);
        this.doneButton = this.createAndAddButton(this.width - 160, this.height - DONE_BUTTON_TOP_OFFSET, 100, 20, "Done", false);

        this.generalGroupButton = this.createAndAddButton(this.width / 2, 30, 120, 20, "General", false);

        int calculatedListHeight = this.height - OPTIONS_LIST_TOP_HEIGHT - BOTTOM_SECTION_HEIGHT;

        for (ConfigCategory category : ConfigCategory.values()) {
            CustomOptionsList nextOptions = new CustomOptionsList(
                    this.getMinecraft(),
                    this.width,
                    calculatedListHeight,
                    OPTIONS_LIST_TOP_HEIGHT,
                    OPTIONS_LIST_ITEM_HEIGHT,
                    this
            );

            CustomOptionsList currentOptions = null;
            int currentLocation = category.ordinal();

            if (currentLocation == 0) {
                currentOptions = new CustomOptionsList(
                        this.getMinecraft(),
                        this.width,
                        calculatedListHeight,
                        OPTIONS_LIST_TOP_HEIGHT,
                        OPTIONS_LIST_ITEM_HEIGHT,
                        this
                );
            } else {
                int currentOptionsIndex = currentLocation - 1;

                if (currentLocation == ConfigCategory.values().length - 1) {
                    // This is the last one; make sure to use the first record as the "next" option row list.
                    nextOptions = this.optionCollection.get(0).getSecond();
                }

                currentOptions = this.optionCollection.get(currentOptionsIndex).getThird();
            }

            ConfigCategory nextCategory = ConfigCategory.getNextCategory(category);

            this.optionCollection.add(new Quadruple<>(category, currentOptions, nextOptions, nextCategory));
        }

        for (ConfigOption<?> configOption : CommonProxy.proxyConfiguration.configOptions) {
            Quadruple<ConfigCategory, CustomOptionsList, CustomOptionsList, ConfigCategory> rowList = this.getOptionsRowList(configOption.getCategory());

            if (rowList != null) {
                switch (configOption.getConfigType()) {
                    case "Boolean": {
                        this.addBooleanOption(rowList.getSecond(), configOption);
                        break;
                    }

                    case "String": {
                        this.addStringOption(rowList.getSecond(), configOption);
                        break;
                    }

                    case "Integer": {
                        this.addIntegerOption(rowList.getSecond(), configOption);
                        break;
                    }
                }
            }
        }

        // Only add the general OptionsList when starting the screen.
        // This list will be removed and re-added depending on the group of options chosen.
        this.addWidget(this.optionCollection.get(0).getSecond());
    }

    @Override
    public void buttonClicked(AbstractButton button) {
        if (button == this.doneButton) {
            ModConfiguration.UpdateServerConfig();
            this.getMinecraft().setScreen(this.parentScreen);
        } else if (button == this.generalGroupButton) {
            Quadruple<ConfigCategory, CustomOptionsList, CustomOptionsList, ConfigCategory> option = this.getOptionsRowList(this.currentOption);

            if (option != null) {
                this.removeWidget(option.getSecond());
                this.addWidget(option.getThird());
                GuiUtils.setButtonText(generalGroupButton, option.getFourth().getName());
                this.currentOption = option.getFourth();
            }
        } else if (button == this.resetToDefaultsButton) {
            for (ConfigOption<?> configOption : CommonProxy.proxyConfiguration.configOptions) {
                configOption.resetToDefault();
            }

            this.clearWidgets();
            this.currentOption = ConfigCategory.General;
            this.Initialize();
        }
    }

    @Override
    protected Tuple<Integer, Integer> getAdjustedXYValue() {
        return new Tuple<>(0, 0);
    }

    @Override
    protected void preButtonRender(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics, x, y, 0);

        // Only render the appropriate options row list based on the currently selected option.
        Quadruple<ConfigCategory, CustomOptionsList, CustomOptionsList, ConfigCategory> rowList = this.getOptionsRowList(this.currentOption);

        if (rowList != null) {
            rowList.getSecond().render(guiGraphics, x, y, partialTicks);

            List<FormattedCharSequence> list = GuiPrefab.tooltipAt(rowList.getSecond(), mouseX, mouseY);

            if (list != null) {
                this.setTooltipForNextRenderPass(list);
            }
        }

        // Draw the title
        guiGraphics.drawCenteredString(
                this.font,
                this.title.getString(),
                this.width / 2,
                TITLE_HEIGHT,
                0xFFFFFF);

        guiGraphics.drawCenteredString(
                this.font,
                "Category",
                (this.width / 2) - 50,
                35,
                0xFFFFFF);
    }

    @Override
    protected void postButtonRender(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, float partialTicks) {
    }

    private void addBooleanOption(CustomOptionsList rowList, ConfigOption<?> configOption) {
        OptionInstance<Boolean> abstractOption = OptionInstance.createBoolean(
                configOption.getName(),
                !configOption.getHoverText().isEmpty()
                        ? (supplierValue) -> Tooltip.create(configOption.getHoverTextComponent())
                        : OptionInstance.noTooltip(),
                configOption.getConfigValueAsBoolean().get(),
                (newValue) -> configOption.getConfigValueAsBoolean().set(newValue)
        );

        rowList.addBig(abstractOption);
    }

    private void addIntegerOption(CustomOptionsList rowList, ConfigOption<?> configOption) {
        OptionInstance<Integer> abstractOption = new OptionInstance<>(
                configOption.getName(),
                OptionInstance.noTooltip(),
                (component, value) -> Utils.createTextComponent(
                        // Use I18n.get(String) to get a translation key's value
                        configOption.getName()
                                + ": "
                                + value
                ),
                (new OptionInstance.IntRange(configOption.getMinRange(), configOption.getMaxRange())).xmap(
                        (toSliderValue) ->
                        {
                            return toSliderValue;
                        },
                        (fromSliderValue) -> {
                            return fromSliderValue;
                        }),
                // CODEC
                Codec.intRange(configOption.getMinRange(), configOption.getMaxRange()),

                // INITIAL VALUE
                configOption.getConfigValueAsInt().get(),

                // ON VALUE UPDATE
                (newValue) ->
                {
                    configOption.getConfigValueAsInt().set(newValue);
                });

        rowList.addBig(abstractOption);
    }

    private void addStringOption(CustomOptionsList rowList, ConfigOption<?> configOption) {
        OptionInstance<String> abstractOption = new OptionInstance<>(
                configOption.getName(),
                // Tooltip Supplier
                !configOption.getHoverText().isEmpty()
                        ? (supplierValue) -> Tooltip.create(configOption.getHoverTextComponent())
                        : OptionInstance.noTooltip(),
                // Caption Based To String
                (component, value) ->
                        Utils.createTextComponent(
                                configOption.getName()
                                        + ": "
                                        + value),

                // Value Set Function
                new OptionInstance.Enum<>(
                        configOption.getValidValues(),
                        Codec.STRING.xmap(
                                value2 -> String.valueOf(configOption.getValidValues().size()),
                                value1 -> String.valueOf(0))),

                // Initial Value
                configOption.getConfigValueAsString().get(),

                // On Value Update
                (newValue) -> {
                    // 'newValue' is always 1.
                    int nextIndex = configOption.getValidValues().indexOf(newValue);

                    if (nextIndex == -1) {
                        nextIndex = 0;
                    }

                    configOption.getConfigValueAsString().set(configOption.getValidValues().get(nextIndex));
                }
        );

        rowList.addBig(abstractOption);
    }

    private Quadruple<ConfigCategory, CustomOptionsList, CustomOptionsList, ConfigCategory> getOptionsRowList(ConfigCategory listName) {
        for (Quadruple<ConfigCategory, CustomOptionsList, CustomOptionsList, ConfigCategory> option : this.optionCollection) {
            if (option.getFirst() == listName) {
                return option;
            }
        }

        return null;
    }
}
