package com.prefab.fabric.events;

import com.prefab.ModRegistryBase;
import com.prefab.PrefabBase;
import com.prefab.network.ServerToClientTypes;
import com.prefab.Tuple;
import com.prefab.config.EntityPlayerConfiguration;
import com.prefab.config.ModConfiguration;
import com.prefab.network.message.TagMessage;
import com.prefab.structures.base.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * This is the structure event handler.
 *
 * @author WuestMan
 */
public final class StructureEventHandler {
    public static ArrayList<Tuple<Structure, BuildEntity>> entitiesToGenerate = new ArrayList<>();

    public static int ticksSinceLastEntitiesGenerated = 0;

    public static void registerStructureServerSideEvents() {
        StructureEventHandler.playerJoinedServer();

        StructureEventHandler.serverStarted();

        StructureEventHandler.serverStopped();

        StructureEventHandler.serverTick();
    }

    private static void playerJoinedServer() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity instanceof ServerPlayer) {
                StructureEventHandler.playerLoggedIn((ServerPlayer) entity, serverWorld);
            }
        });
    }

    private static void serverTick() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            StructureEventHandler.onServerTick();
        });
    }

    private static void serverStarted() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            EntityPlayerConfiguration.playerTagData.clear();
        });
    }

    private static void serverStopped() {
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {
            EntityPlayerConfiguration.playerTagData.clear();
        });
    }

    /**
     * This event is used to determine if the player should be given the starting house item when they log in.
     */
    public static void playerLoggedIn(ServerPlayer player, ServerLevel serverWorld) {
        EntityPlayerConfiguration playerConfig = EntityPlayerConfiguration.loadFromEntity(player);

        ModConfiguration.StartingItemOptions startingItem = PrefabBase.serverConfiguration.startingItem;

        if (!playerConfig.givenHouseBuilder && startingItem != null
            && PrefabBase.serverConfiguration.newPlayersGetStartingItem) {
            ItemStack stack = ItemStack.EMPTY;

            switch (startingItem) {
                case StartingHouse: {
                    stack = new ItemStack(ModRegistryBase.House);
                    break;
                }

                case ModerateHouse: {
                    stack = new ItemStack(ModRegistryBase.HouseImproved);
                    break;
                }
            }

            if (!stack.isEmpty()) {
                PrefabBase.logger.info("{} joined the game for the first time. Giving them starting item.", Objects.requireNonNull(player.getDisplayName()).getString());

                player.getInventory().add(stack);
                player.containerMenu.broadcastChanges();

                // Make sure to set the tag for this player; so they don't get the item again.
                playerConfig.givenHouseBuilder = true;
            }
        }

        // Send the tag to the client.
        TagMessage tagMessage = new TagMessage();
        tagMessage.setMessageTag(playerConfig.createPlayerTag());

        PrefabBase.networkWrapper.sendToClient(ServerToClientTypes.PLAYER_CONFIG_SYNC, player, tagMessage);
    }

    /**
     * This event is primarily used to build 100 blocks for any queued structures for all players.
     */
    public static void onServerTick() {
        // This doesn't ACTUALLY generate a structure right away, it checks to see if one needs to be generated.
        StructureGenerator.CheckForStructureToBuildAndBuildIt();
    }

}
