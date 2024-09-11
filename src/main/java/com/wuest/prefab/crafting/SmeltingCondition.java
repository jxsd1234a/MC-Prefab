package com.wuest.prefab.crafting;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.wuest.prefab.Prefab;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

/**
 * @author WuestMan
 */
public class SmeltingCondition implements ICondition {
    public static final ResourceLocation NAME = new ResourceLocation(Prefab.MODID, "smelting_recipe");
    public String recipeKeyName;

    /**
     * Initializes a new instance of the smelting condition class.
     */
    public SmeltingCondition(String identifier) {
        this.recipeKeyName = identifier;
    }

    /**
     * Determines if the recipe is active.
     *
     * @return True if the recipe is active, otherwise false.
     */
    public boolean determineActiveRecipe() {
        boolean result = false;

        if (this.recipeKeyName != null) {
            if (Prefab.proxy.getServerConfiguration().recipeConfiguration.containsKey(this.recipeKeyName)) {
                result = Prefab.proxy.getServerConfiguration().recipeConfiguration.get(this.recipeKeyName);
            }
        }

        return result;
    }

    @Override
    public boolean test(IContext iContext, DynamicOps<?> dynamicOps) {
        return this.determineActiveRecipe();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        // TODO: Create conditioned recipe!!
        // May just have to make it like the Fabric ones....
        return null;
    }
}