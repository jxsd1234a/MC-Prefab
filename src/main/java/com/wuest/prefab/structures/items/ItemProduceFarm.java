package com.wuest.prefab.structures.items;

import com.wuest.prefab.Prefab;
import com.wuest.prefab.structures.gui.GuiProduceFarm;
import com.wuest.prefab.structures.predefined.StructureProduceFarm;
import net.minecraft.item.ItemUseContext;

/**
 * @author WuestMan
 */
@SuppressWarnings("ConstantConditions")
public class ItemProduceFarm extends StructureItem {
    public ItemProduceFarm() {
        super();
    }

    /**
     * Initializes common fields/properties for this structure item.
     */
    @Override
    protected void Initialize() {
        if (Prefab.proxy.isClient) {
            this.RegisterGui(GuiProduceFarm.class);
        }
    }

    @Override
    public void scanningMode(ItemUseContext context) {
        StructureProduceFarm.ScanStructure(
                context.getLevel(),
                context.getClickedPos(),
                context.getPlayer().getDirection());
    }
}