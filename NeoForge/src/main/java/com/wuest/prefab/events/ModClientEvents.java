package com.wuest.prefab.events;

import com.mojang.blaze3d.platform.InputConstants;
import com.prefab.ClientModRegistryBase;
import com.prefab.PrefabBase;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;

// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = PrefabBase.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        ClientModRegistryBase.RegisterGuis();
    }

    /**
     * Contains the keybindings registered.
     */
    public static ArrayList<KeyMapping> keyBindings = new ArrayList<KeyMapping>();

    @SubscribeEvent
    public static void KeyBindRegistrationEvent(RegisterKeyMappingsEvent event) {
        KeyMapping binding = new KeyMapping("Build Current Structure",
                KeyConflictContext.IN_GAME, KeyModifier.ALT,
                InputConstants.Type.KEYSYM, GLFW_KEY_B, "Prefab - Structure Preview");

        event.register(binding);

        ModClientEvents.keyBindings.add(binding);
    }

}
