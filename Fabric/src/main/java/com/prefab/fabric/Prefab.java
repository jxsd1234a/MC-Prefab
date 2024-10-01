package com.prefab.fabric;

import com.prefab.PrefabBase;
import com.prefab.config.ModConfiguration;
import com.prefab.fabric.events.ServerEvents;
import com.prefab.fabric.network.NetworkWrapper;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class Prefab implements ModInitializer {
    @Override
    public void onInitialize() {
        PrefabBase.logger.info("Registering Mod Components");

        // While we do create an instance of the mod registry to initialize and register the mod components.
        // We throw this away as all of the mod items/blocks/etc are saved off in the static instance.
        ModRegistry registry = new ModRegistry();
        registry.initializeEverything();

        registry.registerModComponents();

        PrefabBase.networkWrapper = new NetworkWrapper();
        PrefabBase.eventCaller = new EventCaller();

        AutoConfig.register(ModConfiguration.class, GsonConfigSerializer::new);

        PrefabBase.serverConfiguration = new ModConfiguration();
        PrefabBase.configuration = AutoConfig.getConfigHolder(ModConfiguration.class).getConfig();

        ServerEvents.registerServerEvents();
    }
}
