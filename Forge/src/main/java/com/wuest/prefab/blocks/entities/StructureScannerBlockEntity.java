package com.wuest.prefab.blocks.entities;

import com.wuest.prefab.ModRegistry;
import com.wuest.prefab.base.TileEntityBase;
import com.wuest.prefab.config.StructureScannerConfig;
import com.wuest.prefab.structures.base.BuildClear;
import com.wuest.prefab.structures.base.BuildShape;
import com.wuest.prefab.structures.base.Structure;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class StructureScannerBlockEntity extends TileEntityBase<StructureScannerConfig> {
    public StructureScannerBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.StructureScannerEntityType, pos, state);

        this.config = new StructureScannerConfig();
    }

    public static void ScanShape(StructureScannerConfig config, ServerPlayer playerEntity, ServerLevel serverWorld) {
        // Make sure to update the tile entity data.
        StructureScannerBlockEntity entity = (StructureScannerBlockEntity) serverWorld.getBlockEntity(config.blockPos);

        if (entity == null) {
            return;
        }

        entity.config = config;
        entity.setChanged();

        BuildClear clearedSpace = new BuildClear();
        clearedSpace.getShape().setDirection(config.direction);
        clearedSpace.getShape().setHeight(config.blocksTall);
        clearedSpace.getShape().setWidth(config.blocksWide);
        clearedSpace.getShape().setLength(config.blocksLong);

        BuildShape buildShape = clearedSpace.getShape().Clone();

        Direction playerFacing = config.direction;

        // Scanning the structure doesn't contain the starting corner block but the clear does.
        buildShape.setWidth(buildShape.getWidth() - 1);
        buildShape.setLength(buildShape.getLength() - 1);

        clearedSpace.getShape().setWidth(clearedSpace.getShape().getWidth());
        clearedSpace.getShape().setLength(clearedSpace.getShape().getLength());

        int downOffset = Math.max(config.blocksDown, 0);

        // Down is inverse on the GUI so make sure that it's negative when saving to the file.
        clearedSpace.getStartingPosition().setHeightOffset(-downOffset);
        clearedSpace.getStartingPosition().setHorizontalOffset(playerFacing, config.blocksParallel);
        clearedSpace.getStartingPosition().setHorizontalOffset(playerFacing.getCounterClockWise(), config.blocksToTheLeft);

        BlockPos cornerPos = config.blockPos
                .relative(playerFacing.getCounterClockWise(), config.blocksToTheLeft)
                .relative(playerFacing, config.blocksParallel)
                .below(downOffset);

        BlockPos otherCorner = cornerPos
                .relative(playerFacing, buildShape.getLength())
                .relative(playerFacing.getClockWise(), buildShape.getWidth())
                .above(buildShape.getHeight());

        Structure.ScanStructure(
                serverWorld,
                config.blockPos,
                cornerPos,
                otherCorner,
                "..\\src\\main\\resources\\assets\\prefab\\structures\\" + config.structureZipName + ".zip",
                clearedSpace,
                playerFacing,
                false,
                false);

        BlockEntity blockEntity = serverWorld.getBlockEntity(config.blockPos);

        if (blockEntity != null && blockEntity.getClass() == StructureScannerBlockEntity.class) {
            StructureScannerBlockEntity structureScannerBlockEntity = (StructureScannerBlockEntity) blockEntity;
            structureScannerBlockEntity.setConfig(config);
        }
    }
}
