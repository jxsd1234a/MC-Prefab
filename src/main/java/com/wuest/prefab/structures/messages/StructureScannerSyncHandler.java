package com.wuest.prefab.structures.messages;

import com.wuest.prefab.blocks.entities.StructureScannerBlockEntity;
import com.wuest.prefab.config.StructureScannerConfig;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class StructureScannerSyncHandler {
    public static void handle(final StructureScannerSyncMessage message, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            StructureScannerConfig config = (new StructureScannerConfig()).ReadFromCompoundTag(message.getMessageTag());

            BlockEntity blockEntity = context.getSender().serverLevel().getBlockEntity(config.blockPos);

            if (blockEntity instanceof StructureScannerBlockEntity) {
                StructureScannerBlockEntity actualEntity = (StructureScannerBlockEntity) blockEntity;
                actualEntity.setConfig(config);
            }
        });

        context.setPacketHandled(true);
    }
}
