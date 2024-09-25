package com.prefab.network;

import net.minecraft.server.level.ServerPlayer;

/**
 * This is a simple wrapper interface for the mod.
 * \n
 * This is to allow for separate mod-loader implementations to manager their own sending logic for how server <--> client communication is managed
 * while maintaining separation for the majority of the code.
 */
public interface INetworkWrapper {
    /**
     * Sends a message from the client to the server.
     *
     * @param messageType The message payload type.
     * @param message The message payload.
     * @param <T> The message payload type.
     */
    <T> void sendToServer (ClientToServerTypes messageType, T message);

    /**
     * Sends a message from the server to the client (typically a player).
     *
     * @param messageType The message payload type.
     * @param targetEntity The target entity
     * @param message The message payload.
     * @param <T> The message payload type.
     */
    <T> void sendToClient (ServerToClientTypes messageType, ServerPlayer targetEntity, T message);
}
