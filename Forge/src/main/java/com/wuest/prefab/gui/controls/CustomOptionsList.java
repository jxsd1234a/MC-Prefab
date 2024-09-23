package com.wuest.prefab.gui.controls;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public class CustomOptionsList extends ContainerObjectSelectionList<CustomOptionsList.Entry> {
    private static final int BIG_BUTTON_WIDTH = 310;
    private static final int DEFAULT_ITEM_HEIGHT = 25;
    private final Screen parentScreen;

    public CustomOptionsList(Minecraft minecraft, int width, int height, int headerHeight, int listItemHeight, Screen parentScreen) {
        super(minecraft, width, height, headerHeight, Math.max(listItemHeight, DEFAULT_ITEM_HEIGHT));
        this.centerListVertically = false;
        this.parentScreen = parentScreen;
    }

    public void addBig(OptionInstance<?> optionInstance) {
        this.addEntry(CustomOptionsList.OptionEntry.big(this.minecraft.options, optionInstance, this.parentScreen));
    }

    public void addSmall(OptionInstance<?>... optionInstances) {
        for (int i = 0; i < optionInstances.length; i += 2) {
            OptionInstance<?> optioninstance = i < optionInstances.length - 1 ? optionInstances[i + 1] : null;
            this.addEntry(CustomOptionsList.OptionEntry.small(this.minecraft.options, optionInstances[i], optioninstance, this.parentScreen));
        }
    }

    public void addSmall(List<AbstractWidget> widgets) {
        for (int i = 0; i < widgets.size(); i += 2) {
            this.addSmall(widgets.get(i), i < widgets.size() - 1 ? widgets.get(i + 1) : null);
        }
    }

    public void addSmall(AbstractWidget widget, @Nullable AbstractWidget otherWidget) {
        this.addEntry(CustomOptionsList.Entry.small(widget, otherWidget, this.parentScreen));
    }

    @Override
    public int getRowWidth() {
        return 310;
    }

    @Nullable
    public AbstractWidget findOption(OptionInstance<?> optionInstance) {
        for (CustomOptionsList.Entry optionslist$entry : this.children()) {
            if (optionslist$entry instanceof CustomOptionsList.OptionEntry optionslist$optionentry) {
                AbstractWidget abstractwidget = optionslist$optionentry.options.get(optionInstance);
                if (abstractwidget != null) {
                    return abstractwidget;
                }
            }
        }

        return null;
    }

    public void applyUnsavedChanges() {
        for (CustomOptionsList.Entry optionslist$entry : this.children()) {
            if (optionslist$entry instanceof CustomOptionsList.OptionEntry) {
                CustomOptionsList.OptionEntry optionslist$optionentry = (CustomOptionsList.OptionEntry)optionslist$entry;

                for (AbstractWidget abstractwidget : optionslist$optionentry.options.values()) {
                    if (abstractwidget instanceof OptionInstance.OptionInstanceSliderButton<?> optioninstancesliderbutton) {
                        optioninstancesliderbutton.applyUnsavedValue();
                    }
                }
            }
        }
    }

    public Optional<GuiEventListener> getMouseOver(double mouseX, double mouseY) {
        for (CustomOptionsList.Entry optionslist$entry : this.children()) {
            for (GuiEventListener guieventlistener : optionslist$entry.children()) {
                if (guieventlistener.isMouseOver(mouseX, mouseY)) {
                    return Optional.of(guieventlistener);
                }
            }
        }

        return Optional.empty();
    }

    @OnlyIn(Dist.CLIENT)
    protected static class Entry extends ContainerObjectSelectionList.Entry<CustomOptionsList.Entry> {
        private final List<AbstractWidget> children;
        private final Screen screen;
        private static final int X_OFFSET = 160;

        Entry(List<AbstractWidget> widgets, Screen parentScreen) {
            this.children = ImmutableList.copyOf(widgets);
            this.screen = parentScreen;
        }

        public static CustomOptionsList.Entry big(List<AbstractWidget> widgets, Screen parentScreen) {
            return new CustomOptionsList.Entry(widgets, parentScreen);
        }

        public static CustomOptionsList.Entry small(AbstractWidget widget, @Nullable AbstractWidget otherWidget, Screen parentScreen) {
            return otherWidget == null
                    ? new CustomOptionsList.Entry(ImmutableList.of(widget), parentScreen)
                    : new CustomOptionsList.Entry(ImmutableList.of(widget, otherWidget), parentScreen);
        }

        @Override
        public void render(
                GuiGraphics guiGraphics,
                int p_94497_,
                int p_94498_,
                int p_94499_,
                int p_94500_,
                int p_94501_,
                int p_94502_,
                int p_94503_,
                boolean p_94504_,
                float p_94505_
        ) {
            int i = 0;
            int j = this.screen.width / 2 - 155;

            for (AbstractWidget abstractwidget : this.children) {
                abstractwidget.setPosition(j + i, p_94498_);
                abstractwidget.render(guiGraphics, p_94502_, p_94503_, p_94505_);
                i += 160;
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static class OptionEntry extends CustomOptionsList.Entry {
        final Map<OptionInstance<?>, AbstractWidget> options;

        private OptionEntry(Map<OptionInstance<?>, AbstractWidget> abstractWidgetMap, Screen parentScreen) {
            super(ImmutableList.copyOf(abstractWidgetMap.values()), parentScreen);
            this.options = abstractWidgetMap;
        }

        public static CustomOptionsList.OptionEntry big(Options options, OptionInstance<?> optionInstance, Screen parentScreen) {
            return new CustomOptionsList.OptionEntry(ImmutableMap.of(optionInstance, optionInstance.createButton(options, 0, 0, 310)), parentScreen);
        }

        public static CustomOptionsList.OptionEntry small(Options options, OptionInstance<?> optionInstance, @Nullable OptionInstance<?> newOptionsInstance, Screen parentScreen) {
            AbstractWidget widget = optionInstance.createButton(options);
            return parentScreen == null ?
                    new CustomOptionsList.OptionEntry(ImmutableMap.of(optionInstance, widget), parentScreen) :
                    new CustomOptionsList.OptionEntry(ImmutableMap.of(optionInstance, widget, newOptionsInstance, newOptionsInstance.createButton(options)), parentScreen);
        }
    }
}
