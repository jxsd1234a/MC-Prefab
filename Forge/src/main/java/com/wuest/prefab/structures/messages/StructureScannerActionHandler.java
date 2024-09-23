package com.wuest.prefab.structures.messages;

import com.wuest.prefab.blocks.entities.StructureScannerBlockEntity;
import com.wuest.prefab.config.StructureScannerConfig;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class StructureScannerActionHandler {
    public static void handle(final StructureScannerActionMessage message, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            StructureScannerConfig config = (new StructureScannerConfig()).ReadFromCompoundTag(message.getMessageTag());

            StructureScannerBlockEntity.ScanShape(config, context.getSender(), context.getSender().serverLevel());
        });

        context.setPacketHandled(true);
    }
}
