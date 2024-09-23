package com.wuest.prefab.structures.items;

import com.wuest.prefab.ClientModRegistry;
import com.wuest.prefab.structures.gui.GuiStructure;
import java.lang.reflect.InvocationTargetException;

/**
 * @author WuestMan
 */
public class StructureItem extends com.prefab.structures.items.StructureItem {

    public StructureItem(Properties properties) {
        super(properties);
    }

    public StructureItem() {
        super();
    }

    /**
     * This method is used to register a GUI for a structure item.
     *
     * Note: This registration functionality is specific to Fabric.
     *
     * @param classToRegister
     */
    protected void RegisterGui(Class<?> classToRegister) {
        try {
            // TODO: There is probably a better way to do this.
            GuiStructure userInterface = (GuiStructure) classToRegister.getDeclaredConstructor().newInstance();
            ClientModRegistry.ModGuis.put(this, userInterface);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
