package com.prefab.blocks;

import com.prefab.ModRegistryBase;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface IGrassSpreadable {

    /**
     * Determines if grass should spread to this block.
     *
     * @param state   The state of the current block.
     * @param worldIn The server world the block resides in.
     * @param pos     The position of the block.
     * @param random  The random value used for checking.
     */
    default void DetermineGrassSpread(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        if (!worldIn.isClientSide) {
            // This is equivalent to light level 9.
            if (worldIn.getBrightness(LightLayer.SKY, pos.above()) >= 0.2727273) {
                for (int i = 0; i < 4; ++i) {
                    BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                    if (blockpos.getY() >= 0 && blockpos.getY() < 256 && !worldIn.isLoaded(blockpos)) {
                        return;
                    }

                    BlockState iblockstate1 = worldIn.getBlockState(blockpos);

                    if ((iblockstate1.getBlock() == Blocks.GRASS_BLOCK
                            || iblockstate1.getBlock() == ModRegistryBase.GrassStairs
                            || iblockstate1.getBlock() == ModRegistryBase.GrassWall
                            || iblockstate1.getBlock() == ModRegistryBase.GrassSlab)
                            // This equivalent to light level 4.
                            && worldIn.getBrightness(LightLayer.SKY, blockpos.above()) >= 0.08333334) {

                        BlockState grassState = this.getGrassBlockState(state);

                        worldIn.setBlock(pos, grassState, 3);
                    }
                }
            }
        }
    }

    /**
     * Gets the block state of the associated grass block.
     *
     * @param originalState The original non-grass block
     * @return A block state for the new grass.
     */
    BlockState getGrassBlockState(BlockState originalState);
}
