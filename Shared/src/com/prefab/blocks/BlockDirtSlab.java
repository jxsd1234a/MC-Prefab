package com.prefab.blocks;

import com.prefab.ModRegistryBase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class BlockDirtSlab extends SlabBlock implements IGrassSpreadable {
    public BlockDirtSlab() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).mapColor(MapColor.DIRT).sound(SoundType.GRAVEL)
                .strength(0.5f, 0.5f));
    }

    /**
     * Returns whether or not this block is of a type that needs random ticking.
     * Called for ref-counting purposes by ExtendedBlockStorage in order to broadly
     * cull a chunk from the random chunk update list for efficiency's sake.
     */
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        this.DetermineGrassSpread(state, worldIn, pos, random);
    }

    @Override
    public BlockState getGrassBlockState(BlockState originalState) {
        return ModRegistryBase.GrassSlab.defaultBlockState().setValue(SlabBlock.TYPE,
                originalState.getValue(SlabBlock.TYPE));
    }
}
