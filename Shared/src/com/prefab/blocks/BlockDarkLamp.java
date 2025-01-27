package com.prefab.blocks;

import com.prefab.ModRegistryBase;
import com.prefab.PrefabBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class BlockDarkLamp extends Block {
    public static final BooleanProperty LIT;

    static {
        LIT = BlockStateProperties.LIT;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int i) {
        return (blockState) -> (Boolean)blockState.getValue(BlockDarkLamp.LIT) ? i : 0;
    }

    private static Boolean always(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType<?> entityType) {
        return true;
    }

    public BlockDarkLamp() {
        super(PrefabBase.SeeThroughImmovable.get()
                .lightLevel(BlockDarkLamp.litBlockEmission(8))
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(BlockDarkLamp::always));

        this.registerDefaultState(this.defaultBlockState().setValue(BlockDarkLamp.LIT, false));
    }

    @Override
    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return 1.0F;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        if (!blockPlaceContext.getLevel().isClientSide) {
            boolean isLit = ModRegistryBase.serverModRegistries.getLightSwitchRegistry().checkForNearbyOnSwitch(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos());
            return this.defaultBlockState().setValue(BlockDarkLamp.LIT, isLit);
        } else {
            return this.defaultBlockState().setValue(BlockDarkLamp.LIT, false);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockDarkLamp.LIT);
    }
}
