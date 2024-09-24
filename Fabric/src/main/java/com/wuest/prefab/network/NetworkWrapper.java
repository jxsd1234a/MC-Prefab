package com.wuest.prefab.network;

import com.prefab.network.ClientToServerTypes;
import com.prefab.network.INetworkWrapper;
import com.prefab.network.ServerToClientTypes;
import com.prefab.network.message.ScannerInfo;
import com.prefab.network.message.TagMessage;
import com.wuest.prefab.network.message.*;
import com.wuest.prefab.structures.messages.StructurePayload;
import com.prefab.structures.messages.StructureTagMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class NetworkWrapper implements INetworkWrapper {

    @Override
    public <T> void sendToServer(ClientToServerTypes messageType, T message) {
        switch (messageType) {
            case ClientToServerTypes.STRUCTURE_BUILD -> {
                this.sendStructureTagMessage((StructureTagMessage)message);
                break;
            }

            case ClientToServerTypes.SCAN_SHAPE -> {
                this.sendScannerConfigMessage((ScannerInfo) message);
                break;
            }

            case ClientToServerTypes.SCANNER_CONFIG_UPDATE -> {
                this.sendScannerScanMessage((ScannerInfo) message);
                break;
            }
        }
    }

    @Override
    public <T> void sendToClient(ServerToClientTypes messageType, ServerPlayer targetEntity, T message) {
        switch (messageType) {
            case ServerToClientTypes.MOD_CONFIG_SYNC -> {
                this.sendModConfigSyncMessage((TagMessage) message, targetEntity);
                break;
            }

            case ServerToClientTypes.PLAYER_CONFIG_SYNC -> {
                this.sendPlayerConfigSyncMessage((TagMessage) message, targetEntity);
                break;
            }
        }
    }

    private void sendStructureTagMessage(StructureTagMessage message) {
        StructurePayload payload = new StructurePayload(message);

        ClientPlayNetworking.send(payload);
    }

    private void sendScannerConfigMessage(ScannerInfo message) {
        ScannerConfigPayload scannerConfigPayload = new ScannerConfigPayload(message);

        ClientPlayNetworking.send(scannerConfigPayload);
    }

    private void sendScannerScanMessage(ScannerInfo message) {
        ScanShapePayload scanShapePayload = new ScanShapePayload(message);

        ClientPlayNetworking.send(scanShapePayload);
    }

    private void sendModConfigSyncMessage(TagMessage message, ServerPlayer player) {
        ConfigSyncPayload configSyncPayload = new ConfigSyncPayload(message);
        ServerPlayNetworking.send(player, configSyncPayload);
    }

    private void sendPlayerConfigSyncMessage(TagMessage message, ServerPlayer player) {
        PlayerConfigPayload playerConfigPayload = new PlayerConfigPayload(message);
        ServerPlayNetworking.send(player, playerConfigPayload);
    }
}