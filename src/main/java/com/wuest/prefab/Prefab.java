package com.wuest.prefab;

import com.mojang.blaze3d.platform.InputConstants;
import com.wuest.prefab.events.ClientEventHandler;
import com.wuest.prefab.items.ItemSickle;
import com.wuest.prefab.proxy.ClientProxy;
import com.wuest.prefab.proxy.CommonProxy;
import com.wuest.prefab.registries.ModRegistries;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;

/**
 * The starting point to load all of the blocks, items and other objects associated with this mod.
 *
 * @author WuestMan
 */
@Mod(Prefab.MODID)
public class Prefab {
    /**
     * Simulates an air block that blocks movement and cannot be moved.
     * Basically a GLASS block, but NOT glass.
     */
    public static final Supplier<BlockBehaviour.Properties> SeeThroughImmovable = ()-> BlockBehaviour.Properties.of()
            .noOcclusion()
            .isViewBlocking(Prefab::never)
            .pushReaction(PushReaction.IGNORE)
            .sound(SoundType.STONE);

    public static boolean always(BlockState p_50775_, BlockGetter p_50776_, BlockPos p_50777_) {
        return true;
    }

    public static boolean never(BlockState p_50806_, BlockGetter p_50807_, BlockPos p_50808_) {
        return false;
    }

    /**
     * This is the ModID
     */
    public static final String MODID = "prefab";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static final int PROTOCOL_VERSION = 1;

    /**
     * This is used to determine if the mod is currently being debugged.
     */
    public static boolean isDebug = false;

    /**
     * Determines if structure items will scan their defined space or show the build gui. Default is false.
     * Note: this should only be set to true during debug mode.
     */
    public static boolean useScanningMode = false;

    /**
     * Says where the client and server 'proxy' code is loaded.
     */
    public static CommonProxy proxy;

    /**
     * The network class used to send messages.
     */
    public static SimpleChannel network;

    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(Prefab.MODID, "prefab"));

    static {
        Prefab.isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
    }

    public Prefab() {
        // Register the blocks and items for this mod.
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.BLOCKS.register(bus);
        ModRegistry.ITEMS.register(bus);
        ModRegistry.TILE_ENTITIES.register(bus);
        ModRegistry.SOUNDS.register(bus);
        ModRegistry.RECIPES.register(bus);

        // Register the setup method for mod-loading
        bus.addListener(this::setup);

        bus.addListener(CommonProxy::creativeModeTabRegister);

        MinecraftForge.EVENT_BUS.addListener(this::serverStart);

        Prefab.proxy = new CommonProxy();
    }

    private void setup(final FMLCommonSetupEvent event) {
        Prefab.proxy.preInit(event);
        Prefab.proxy.init(event);
        Prefab.proxy.postinit(event);
    }

    // The method that gets called when a server starts up(Singleplayer and multiplayer are both affected)
    public void serverStart(ServerAboutToStartEvent event) {
        // Get's the current server instance.
        MinecraftServer server = event.getServer();

        // Get's the Command manager for the server.
        // This is used to register available commands for the server.
        Commands command = server.getCommands();

        ItemSickle.setEffectiveBlocks();

        ModRegistry.serverModRegistries = new ModRegistries();
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            Prefab.proxy = new ClientProxy();
            Prefab.proxy.preInit(event);
            Prefab.proxy.init(event);
            Prefab.proxy.postinit(event);

            Prefab.proxy.RegisterEventHandler();

            Prefab.proxy.clientSetup(event);
        }

        @SubscribeEvent
        public static void KeyBindRegistrationEvent(RegisterKeyMappingsEvent event) {
            KeyMapping binding = new KeyMapping("Build Current Structure",
                    KeyConflictContext.IN_GAME, KeyModifier.ALT,
                    InputConstants.Type.KEYSYM, GLFW_KEY_B, "Prefab - Structure Preview");

            event.register(binding);

            ClientEventHandler.keyBindings.add(binding);
        }
    }
}
