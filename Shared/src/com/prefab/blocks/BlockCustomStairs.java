package com.prefab.blocks;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class just extends the vanilla stairs block for easier implementation.
 */
public class BlockCustomStairs extends StairBlock {
    public BlockCustomStairs(BlockState baseBlockState, Properties settings) {
        super(baseBlockState, settings);
    }
}
