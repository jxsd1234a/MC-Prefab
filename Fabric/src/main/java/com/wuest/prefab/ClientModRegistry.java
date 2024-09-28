package com.wuest.prefab;

import com.mojang.blaze3d.platform.InputConstants;
import com.prefab.ClientModRegistryBase;
import com.prefab.ModRegistryBase;
import com.prefab.network.payloads.PlayerConfigPayload;
import com.prefab.network.payloads.ConfigSyncPayload;
import com.wuest.prefab.network.ClientPayloadHandler;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.glfw.GLFW;

public class ClientModRegistry {
    public static KeyMapping keyBinding;

    public static void registerModComponents() {
        ClientModRegistry.registerKeyBindings();

        ClientModRegistry.registerBlockLayers();

        ClientModRegistry.registerServerToClientMessageHandlers();

        ClientModRegistryBase.RegisterGuis();
    }

    private static void registerServerToClientMessageHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.PACKET_TYPE, ClientPayloadHandler::ModConfigHandler);

        ClientPlayNetworking.registerGlobalReceiver(PlayerConfigPayload.PACKET_TYPE, ClientPayloadHandler::PlayerConfigHandler);
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

    public static void registerKeyBindings() {
        // TODO: Create translation keys.
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "Build Current Structure", // The translation key of the keybinding's name
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "Prefab - Structure Preview" // The translation key of the keybinding's category.
        ));
    }
}
