package com.prefab.blocks;

import com.prefab.PrefabBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * This is a phasing block.
 *
 * @author WuestMan
 */
@SuppressWarnings({"SpellCheckingInspection", "NullableProblems"})
public class BlockPhasic extends Block {
    /**
     * The phasing progress property.
     */
    protected static final EnumProperty<EnumPhasingProgress> Phasing_Progress = EnumProperty.create("phasic_progress", EnumPhasingProgress.class);

    /**
     * The phasing out block property.
     */
    protected static final BooleanProperty Phasing_Out = BooleanProperty.create("phasing_out");

    /**
     * The tick rage for this block.
     */
    protected int tickRate = 2;

    /**
     * Initializes a new instance of the BlockPhasing class.
     */
    public BlockPhasic() {
        super(PrefabBase.SeeThroughImmovable.get()
                .sound(SoundType.STONE)
                .strength(0.6f));

        this.registerDefaultState(this.getStateDefinition().any().setValue(Phasing_Out, false).setValue(Phasing_Progress, EnumPhasingProgress.base));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockPhasic.Phasing_Out, BlockPhasic.Phasing_Progress);
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTrace) {
        if (!world.isClientSide()) {
            EnumPhasingProgress progress = state.getValue(Phasing_Progress);

            if (progress == EnumPhasingProgress.base) {
                // Only trigger the phasing when this block is not currently phasing.
                world.scheduleTick(pos, this, this.tickRate);
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        EnumPhasingProgress progress = state.getValue(Phasing_Progress);
        return progress != EnumPhasingProgress.transparent ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        EnumPhasingProgress progress = state.getValue(Phasing_Progress);

        return progress != EnumPhasingProgress.transparent ? Shapes.block() : Shapes.empty();
    }

    @Nullable
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        EnumPhasingProgress progress = state.getValue(Phasing_Progress);

        if (progress == EnumPhasingProgress.transparent) {
            return Shapes.empty();
        }

        return super.getCollisionShape(state, worldIn, pos, context);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        EnumPhasingProgress progress = state.getValue(Phasing_Progress);

        if (progress == EnumPhasingProgress.transparent) {
            return Shapes.empty();
        }

        VoxelShape aabb = super.getInteractionShape(state, worldIn, pos);

        return aabb;
    }

    @Override
    public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        EnumPhasingProgress progress = blockState.getValue(Phasing_Progress);

        if (progress == EnumPhasingProgress.transparent) {
            return 1.0F;
        } else {
            return 0.2F;
        }
    }

    /**
     * Sets the powered status and updates the block's neighbor.
     *
     * @param worldIn           The world where the block resides.
     * @param pos               The position of the block.
     * @param desiredBlockState The current state of the block at the position.
     * @param cascadeCount      The number of times it has cascaded.
     * @param cascadedBlockPos  The list of cascaded block positions, this is used to determine if this block should
     *                          be processed again.
     * @param setCurrentBlock   Determines if the current block should be set.
     */
    protected int findNeighborPhasicBlocks(Level worldIn, BlockPos pos, BlockState desiredBlockState, int cascadeCount,
                                           ArrayList<BlockPos> cascadedBlockPos, boolean setCurrentBlock) {
        cascadeCount++;

        if (cascadeCount > 100) {
            return cascadeCount;
        }

        if (setCurrentBlock) {
            cascadedBlockPos.add(pos);
        }

        for (Direction facing : Direction.values()) {
            Block neighborBlock = worldIn.getBlockState(pos.relative(facing)).getBlock();

            if (neighborBlock instanceof BlockPhasic) {
                BlockState blockState = worldIn.getBlockState(pos.relative(facing));

                // If the block is already in the correct state or was already checked, there is no need to cascade to
                // it's neighbors.
                EnumPhasingProgress progress = blockState.getValue(Phasing_Progress);

                if (cascadedBlockPos.contains(pos.relative(facing)) || progress == desiredBlockState.getValue(Phasing_Progress)) {
                    continue;
                }

                setCurrentBlock = true;
                cascadeCount = this.findNeighborPhasicBlocks(worldIn, pos.relative(facing), desiredBlockState, cascadeCount, cascadedBlockPos, setCurrentBlock);

                if (cascadeCount > 100) {
                    break;
                }
            }
        }

        return cascadeCount;
    }

    /**
     * The enum used to determine the meta data for this block.
     *
     * @author WuestMan
     */
    public enum EnumPhasingProgress implements StringRepresentable {
        base(0, "base"),
        twenty_percent(2, "twenty_percent"),
        forty_percent(4, "forty_percent"),
        sixty_percent(6, "sixty_percent"),
        eighty_percent(8, "eighty_percent"),
        transparent(10, "transparent");

        private int meta;
        private String name;

        EnumPhasingProgress(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        /**
         * Gets a instance based on the meta data.
         *
         * @param meta The meta data value to get the enum value for.
         * @return An instance of the enum.
         */
        public static EnumPhasingProgress ValueOf(int meta) {
            for (EnumPhasingProgress progress : EnumPhasingProgress.values()) {
                if (progress.meta == meta) {
                    return progress;
                }
            }

            return EnumPhasingProgress.base;
        }

        public int getMeta() {
            return this.meta;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
