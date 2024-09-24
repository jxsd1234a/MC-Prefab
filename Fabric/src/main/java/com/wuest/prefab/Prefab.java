package com.wuest.prefab;

import com.prefab.PrefabBase;
import com.prefab.config.ModConfiguration;
import com.wuest.prefab.events.ServerEvents;
import com.wuest.prefab.network.NetworkWrapper;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class Prefab implements ModInitializer {

    /**
     * Determines if structure items will scan their defined space or show the build gui. Default is false.
     * Note: this should only be set to true during debug mode.
     */
    public static boolean useScanningMode = false;

    @Override
    public void onInitialize() {
        PrefabBase.logger.info("Registering Mod Components");
        ModRegistry.registerModComponents();

        PrefabBase.networkWrapper = new NetworkWrapper();
        PrefabBase.eventCaller = new EventCaller();

        AutoConfig.register(ModConfiguration.class, GsonConfigSerializer::new);

        PrefabBase.serverConfiguration = new ModConfiguration();
        PrefabBase.configuration = AutoConfig.getConfigHolder(ModConfiguration.class).getConfig();

        ServerEvents.registerServerEvents();
    }
}
