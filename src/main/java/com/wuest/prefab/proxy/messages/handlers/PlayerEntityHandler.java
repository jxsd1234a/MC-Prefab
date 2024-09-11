package com.wuest.prefab.proxy.messages.handlers;

import com.wuest.prefab.config.EntityPlayerConfiguration;
import com.wuest.prefab.events.ClientEventHandler;
import com.wuest.prefab.proxy.messages.PlayerEntityTagMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * @author WuestMan
 */
public class PlayerEntityHandler {
    /**
     * Initializes a new instance of the StructureHandler class.
     */
    public PlayerEntityHandler() {
    }

    public static void handle(final PlayerEntityTagMessage message, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            // This is client side.
            CompoundTag newPlayerTag = Minecraft.getInstance().player.getPersistentData();
            newPlayerTag.put(EntityPlayerConfiguration.PLAYER_ENTITY_TAG, message.getMessageTag());
            ClientEventHandler.playerConfig.loadFromNBTTagCompound(message.getMessageTag());
        });

        context.setPacketHandled(true);
    }
}
