package com.wuest.prefab.network;

import com.prefab.network.ClientToServerTypes;
import com.prefab.network.INetworkWrapper;
import com.prefab.network.ServerToClientTypes;
import net.minecraft.server.level.ServerPlayer;

public class NetworkWrapper implements INetworkWrapper {
    @Override
    public <T> void sendToServer(ClientToServerTypes messageType, T message) {
        // TODO: Implement this
    }

    @Override
    public <T> void sendToClient(ServerToClientTypes messageType, ServerPlayer targetEntity, T message) {
        // TODO: Implement this
    }
}
