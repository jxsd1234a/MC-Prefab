package com.wuest.prefab.items;

import com.prefab.gui.GuiLangKeys;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * This is a condensed chest used in the construction of the warehouse.
 *
 * @author WuestMan
 */
public class ItemCompressedChest extends com.prefab.items.ItemCompressedChest {
    /**
     * Initializes a new instance of the ItemCompressedChest class.
     */
    public ItemCompressedChest() {
        super();

    }

    /**
     * allows items to add custom lines of information to the mouse-over description
     */
    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(GuiLangKeys.translateToComponent(GuiLangKeys.COMPRESSED_CHEST));
    }
}