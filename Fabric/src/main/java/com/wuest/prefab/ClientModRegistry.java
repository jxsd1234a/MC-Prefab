package com.wuest.prefab;

import com.prefab.ClientModRegistryBase;
import com.prefab.ModRegistryBase;
import com.wuest.prefab.config.EntityPlayerConfiguration;
import com.wuest.prefab.network.message.PlayerConfigPayload;
import com.wuest.prefab.network.message.ConfigSyncPayload;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import java.util.UUID;

public class ClientModRegistry {

    public static EntityPlayerConfiguration playerConfig = new EntityPlayerConfiguration();

    public static void registerModComponents() {
        ClientModRegistryBase.registerKeyBindings();

        ClientModRegistry.registerBlockLayers();

        ClientModRegistry.registerServerToClientMessageHandlers();

        ClientModRegistryBase.RegisterGuis();
    }

    private static void registerServerToClientMessageHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.PACKET_TYPE,
                (payload, context) -> {
                    context.client().execute(() -> {
                        // This is now on the "main" client thread and things can be done in the world!
                        Prefab.serverConfiguration.readFromTag(payload.tagMessage().getMessageTag());
                    });
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(PlayerConfigPayload.PACKET_TYPE, (payload, context) -> {
            context.client().execute(() -> {
                // This is now on the "main" client thread and things can be done in the world!
                UUID playerUUID = Minecraft.getInstance().player.getUUID();

                EntityPlayerConfiguration playerConfiguration = EntityPlayerConfiguration.loadFromTag(playerUUID, payload.tagMessage().getMessageTag());
                ClientModRegistry.playerConfig.builtStarterHouse = playerConfiguration.builtStarterHouse;
                ClientModRegistry.playerConfig.givenHouseBuilder = playerConfiguration.givenHouseBuilder;
            });
        });
    }

    private static void registerBlockLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.GlassStairs, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.GlassSlab, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.PaperLantern, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.Boundary, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.Phasic, RenderType.cutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.GrassStairs, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.DirtStairs, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.GrassSlab, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.DirtSlab, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.GrassWall, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.DirtWall, RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.LightSwitch, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModRegistryBase.DarkLamp, RenderType.cutoutMipped());
    }
}
