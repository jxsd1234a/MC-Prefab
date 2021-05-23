package com.wuest.prefab.Structures.Config.Enums;

import net.minecraft.util.Direction;

public class MineshaftEntranceOptions extends BaseOption {
    public static MineshaftEntranceOptions Default = new MineshaftEntranceOptions(
            "item.prefab.mineshaft.entrance",
            "assets/prefab/structures/mineshaft_entrance.zip",
            "textures/gui/mineshaft_entrance_topdown.png",
            159,
            135,
            Direction.SOUTH,
            6,
            7,
            7,
            1,
            3,
            0);

    protected MineshaftEntranceOptions(String translationString,
                                       String assetLocation,
                                       String pictureLocation,
                                       int imageWidth,
                                       int imageHeight,
                                       Direction direction,
                                       int height,
                                       int width,
                                       int length,
                                       int offsetParallelToPlayer,
                                       int offsetToLeftOfPlayer,
                                       int heightOffset) {
        super(translationString, assetLocation, pictureLocation, imageWidth, imageHeight, direction, height, width, length, offsetParallelToPlayer, offsetToLeftOfPlayer, heightOffset);
    }

}
