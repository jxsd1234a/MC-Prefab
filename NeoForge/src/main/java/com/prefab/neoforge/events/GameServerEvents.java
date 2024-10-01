package com.prefab.neoforge.events;

import com.prefab.ModRegistryBase;
import com.prefab.PrefabBase;
import com.prefab.config.EntityPlayerConfiguration;
import com.prefab.config.ModConfiguration;
import com.prefab.items.ItemSickle;
import com.prefab.network.ServerToClientTypes;
import com.prefab.network.message.TagMessage;
import com.prefab.registries.ModRegistries;
import com.prefab.structures.base.StructureGenerator;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Objects;

public class GameServerEvents {
    /**
     * Determines the affected blocks by redstone power.
     */
    public static ArrayList<BlockPos> RedstoneAffectedBlockPositions = new ArrayList<>();

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event)
    {
        ModRegistryBase.serverModRegistries = new ModRegistries();

        // Do this when the server starts so that all appropriate tags are used.
        ItemSickle.setEffectiveBlocks();
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        // Get the server configuration.
        // This will be pushed to the player when they join the world.
        PrefabBase.serverConfiguration = AutoConfig.getConfigHolder(ModConfiguration.class).getConfig();
    }

    @SubscribeEvent
    public void playerJoinedServer(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            TagMessage message = new TagMessage(PrefabBase.serverConfiguration.writeCompoundTag());
            PrefabBase.networkWrapper.sendToClient(ServerToClientTypes.MOD_CONFIG_SYNC, (ServerPlayer) event.getEntity(), message);

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
    }

    /**
     * This occurs when a player dies and is used to make sure that a player does not get a duplicate starting house.
     *
     * @param event The player clone event.
     */
    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer) {
            // TODO: See if this is needed.
            // We had this event in forge to copy the data tag from the old entity to the new entity
            // So the player did not get the starting house again
            // This doesn't seem to be an issue in Fabric (or at least hasn't been reported)
            // Will need to test this out!
        }
    }

    /**
     * This event is primarily used to build 100 blocks for any queued structures for all players.
     *
     * @param event The event object.
     */
    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Pre event) {
        // This doesn't ACTUALLY generate a structure right away, it checks to see if one needs to be generated.
        StructureGenerator.CheckForStructureToBuildAndBuildIt();
    }
}
