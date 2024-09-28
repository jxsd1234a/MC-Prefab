package com.wuest.prefab.blocks;

import com.prefab.ModRegistryBase;
import com.prefab.Utils;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlockGlassStairs extends com.prefab.blocks.BlockGlassStairs {
    public BlockGlassStairs(BlockState state, Block.Properties properties) {
        super(state, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        boolean foundBlock = Utils.doesBlockStateHaveTag(adjacentBlockState,  ResourceLocation.parse("c:glass_blocks"));
        Block adjacentBlock = adjacentBlockState.getBlock();

        return foundBlock || adjacentBlock == this
                || (adjacentBlock == ModRegistryBase.GlassSlab
                && adjacentBlockState.getValue(SlabBlock.TYPE) == SlabType.DOUBLE);
    }
}
