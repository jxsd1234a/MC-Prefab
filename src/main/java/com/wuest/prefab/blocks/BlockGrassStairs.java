package com.wuest.prefab.blocks;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolType;

/**
 * This class defines a set of grass stairs.
 *
 * @author WuestMan
 */
public class BlockGrassStairs extends StairBlock {

    public BlockGrassStairs() {
        super(Blocks.GRASS_BLOCK.defaultBlockState(), BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK)
                .harvestTool(ToolType.SHOVEL)
                .harvestLevel(0));
    }
}
