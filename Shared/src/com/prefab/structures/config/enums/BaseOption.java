package com.prefab.structures.config.enums;

import com.prefab.PrefabBase;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BaseOption {

    private static final HashMap<String, ArrayList<BaseOption>> classOptions = new HashMap<>();
    private final String translationString;
    private final String assetLocation;
    private final ResourceLocation pictureLocation;
    private final boolean hasBedColor;
    private final boolean hasGlassColor;

    protected BaseOption(
            String translationString,
            String assetLocation,
            String pictureLocation,
            boolean hasBedColor,
            boolean hasGlassColor) {
        this.translationString = translationString;
        this.assetLocation = assetLocation;
        this.pictureLocation = ResourceLocation.tryBuild(PrefabBase.MODID, pictureLocation);
        this.hasBedColor = hasBedColor;
        this.hasGlassColor = hasGlassColor;

        BaseOption.addOption(this);
    }

    /**
     * @param hashCode The hashcode to use to find the option.
     * @return The option found.
     */
    public static BaseOption getOptionByHash(int hashCode) {
        for (Map.Entry<String, ArrayList<BaseOption>> mapping : BaseOption.classOptions.entrySet()) {
            for (BaseOption storedOption : mapping.getValue()) {
                if (storedOption.hashCode() == hashCode) {
                    return storedOption;
                }
            }
        }

        return null;
    }

    public static void addOption(BaseOption option) {
        String className = option.getClass().getName();

        ArrayList<BaseOption> options;

        if (!BaseOption.classOptions.containsKey(className)) {
            options = new ArrayList<>();
            BaseOption.classOptions.put(className, options);
        } else {
            options = BaseOption.classOptions.get(className);
        }

        options.add(option);
    }

    /**
     * @return Gets the translation string for the button when choosing this option.
     */
    public String getTranslationString() {
        return this.translationString;
    }

    /**
     * @return Gets the asset location to use to build the structure.
     */
    public String getAssetLocation() {
        return this.assetLocation;
    }

    /**
     * @return Gets the picture location to show when this option is chosen.
     */
    public ResourceLocation getPictureLocation() {
        return this.pictureLocation;
    }

    /**
     * @return A value indicating whether the current option has bed color options.
     */
    public boolean getHasBedColor() {
        return this.hasBedColor;
    }

    /**
     * @return A value indicating whether the current option has glass color options.
     */
    public boolean getHasGlassColor() {
        return this.hasGlassColor;
    }

    public ArrayList<BaseOption> getSpecificOptions() {
        String className = this.getClass().getName();

        for (Map.Entry<String, ArrayList<BaseOption>> mapping : BaseOption.classOptions.entrySet()) {
            if (mapping.getKey().equals(className)) {
                return mapping.getValue();
            }
        }

        return null;
    }

    /**
     * Filters the supplied options to remove any options not available based on configuration.
     *
     * @param originalOptions The original options to filter.
     * @return A modified array list which potentially has some items removed.
     */
    public ArrayList<BaseOption> filterOptions(ArrayList<BaseOption> originalOptions) {
        return originalOptions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.translationString, this.assetLocation);
    }
}
