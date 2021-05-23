package com.wuest.prefab.Structures.Config.Enums;

import net.minecraft.util.Direction;

public class GrassyPlainOptions extends BaseOption {
    public static GrassyPlainOptions Default = new GrassyPlainOptions(
            "item.prefab.grassy_plain",
            "assets/prefab/structures/grassy_plain.zip",
            "textures/gui/grassy_plain_topdown.png",
            160,
            160,
            Direction.SOUTH,
            4,
            15,
            15,
            1,
            8,
            -1);

    protected GrassyPlainOptions(String translationString,
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
