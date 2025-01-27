package com.prefab.structures.config.enums;

public class WatchTowerOptions extends BaseOption {
    public static WatchTowerOptions Default = new WatchTowerOptions(
            "item.prefab.item_watch_tower",
            "assets/prefab/structures/watch_tower.gz",
            "textures/gui/watch_tower.png",
            true,
            false);

    public static WatchTowerOptions Copper = new WatchTowerOptions(
            "item.prefab.item_watch_tower_2",
            "assets/prefab/structures/watch_tower_2.gz",
            "textures/gui/watch_tower_2.png",
            true,
            true);

    public static WatchTowerOptions Dark = new WatchTowerOptions(
            "item.prefab.item_watch_tower_dark",
            "assets/prefab/structures/watch_tower_dark.gz",
            "textures/gui/watch_tower_dark.png",
            true,
            true);

    protected WatchTowerOptions(String translationString,
                                String assetLocation,
                                String pictureLocation,
                                boolean hasBedColor,
                                boolean hasGlassColor) {
        super(
                translationString,
                assetLocation,
                pictureLocation,
                hasBedColor,
                hasGlassColor);
    }
}
