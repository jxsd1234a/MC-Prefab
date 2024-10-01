package com.prefab.neoforge.network;

import com.prefab.ClientModRegistryBase;
import com.prefab.PrefabBase;
import com.prefab.config.EntityPlayerConfiguration;
import com.prefab.network.payloads.ConfigSyncPayload;
import com.prefab.network.payloads.PlayerConfigPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class ClientPayloadHandler {
    public static void PlayerConfigHandler(PlayerConfigPayload payload, IPayloadContext context) {
        Minecraft.getInstance().execute(() -> {
            // This is now on the "main" client thread and things can be done in the world!
            UUID playerUUID = Minecraft.getInstance().player.getUUID();

            EntityPlayerConfiguration playerConfiguration = EntityPlayerConfiguration.loadFromTag(playerUUID, payload.tagMessage().getMessageTag());
            ClientModRegistryBase.playerConfig.builtStarterHouse = playerConfiguration.builtStarterHouse;
            ClientModRegistryBase.playerConfig.givenHouseBuilder = playerConfiguration.givenHouseBuilder;
        });
    }

    public static void ModConfigHandler(ConfigSyncPayload payload, IPayloadContext context) {
        Minecraft.getInstance().execute(() -> {
            // This is now on the "main" client thread and things can be done in the world!
            PrefabBase.serverConfiguration.readFromTag(payload.tagMessage().getMessageTag());
        });
    }
}
