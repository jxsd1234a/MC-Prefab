package com.wuest.prefab.crafting;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.wuest.prefab.Prefab;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

/**
 * @author WuestMan
 */
public class RecipeCondition implements ICondition {
    public static final ResourceLocation NAME = new ResourceLocation(Prefab.MODID, "config_recipe");
    public String recipeKey;

    /**
     * Initializes a new instance of the recipe condition class.
     */
    public RecipeCondition(String recipeKey) {
        this.recipeKey = recipeKey;
    }

    /**
     * Determines if the recipe is active.
     *
     * @return True if the recipe is active, otherwise false.
     */
    private boolean determineActiveRecipe() {
        boolean result = false;

        if (this.recipeKey != null) {
            if (Prefab.proxy.getServerConfiguration().recipeConfiguration.containsKey(this.recipeKey)) {
                result = Prefab.proxy.getServerConfiguration().recipeConfiguration.get(this.recipeKey);
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
