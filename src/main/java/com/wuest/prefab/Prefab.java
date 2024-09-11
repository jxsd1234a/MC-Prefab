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
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTabs;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.SimpleChannel;
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

    static {
        Prefab.isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
    }

    public Prefab() {
        // Register the blocks and items for this mod.
        ModRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModRegistry.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModRegistry.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register the setup method for mod-loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

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

        // TODO: Need to register the creative tab in the client-side mod registry.
        @SubscribeEvent
        public static void onCreativeModeTabRegister(BuildCreativeModeTabContentsEvent event) {

            // Add to ingredients tab
            if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
                event.accept(ModRegistry.CompressedStoneItem.get());
                event.accept(ModRegistry.DoubleCompressedStoneItem.get());
                event.accept(ModRegistry.TripleCompressedStoneItem.get());
                event.accept(ModRegistry.CompressedDirtItem.get());
                event.accept(ModRegistry.DoubleCompressedDirtItem.get());
                event.accept(ModRegistry.CompressedGlowStoneItem.get());
                event.accept(ModRegistry.DoubleCompressedGlowStoneItem.get());
                event.accept(ModRegistry.CompressedQuartzCreteItem.get());
                event.accept(ModRegistry.DoubleCompressedQuartzCreteItem.get());
                event.accept(ModRegistry.CompressedObsidianItem.get());
                event.accept(ModRegistry.DoubleCompressedObsidianItem.get());
                event.accept(ModRegistry.GlassSlabItem.get());
                event.accept(ModRegistry.GlassStairsItem.get());
                event.accept(ModRegistry.PaperLanternItem.get());
                event.accept(ModRegistry.BlockPhasingItem.get());
                event.accept(ModRegistry.BlockBoundaryItem.get());
                event.accept(ModRegistry.GrassSlabItem.get());
                event.accept(ModRegistry.GrassStairsItem.get());
                event.accept(ModRegistry.GrassWallItem.get());
                event.accept(ModRegistry.DirtWallItem.get());
                event.accept(ModRegistry.DirtStairsItem.get());
                event.accept(ModRegistry.DirtSlabItem.get());
                event.accept(ModRegistry.LightSwitchItem.get());
                event.accept(ModRegistry.DarkLampItem.get());
                event.accept(ModRegistry.QuartzCreteItem.get());
                event.accept(ModRegistry.QuartzCreteWallItem.get());
                event.accept(ModRegistry.QuartzCreteBricksItem.get());
                event.accept(ModRegistry.ChiseledQuartzCreteItem.get());
                event.accept(ModRegistry.QuartzCretePillarItem.get());
                event.accept(ModRegistry.QuartzCreteStairsItem.get());
                event.accept(ModRegistry.QuartzCreteSlabItem.get());
                event.accept(ModRegistry.SmoothQuartzCreteItem.get());
                event.accept(ModRegistry.SmoothQuartzCreteWallItem.get());
                event.accept(ModRegistry.SmoothQuartzCreteStairsItem.get());
                event.accept(ModRegistry.SmoothQuartzCreteSlabItem.get());

                event.accept(ModRegistry.ItemCompressedChest.get());
                event.accept(ModRegistry.ItemPileOfBricks.get());
                event.accept(ModRegistry.ItemPalletOfBricks.get());
                event.accept(ModRegistry.ItemBundleOfTimber.get());
                event.accept(ModRegistry.ItemHeapOfTimber.get());
                event.accept(ModRegistry.ItemTonOfTimber.get());
                event.accept(ModRegistry.ItemStringOfLanterns.get());
                event.accept(ModRegistry.ItemCoilOfLanterns.get());
                event.accept(ModRegistry.WarehouseUpgrade.get());
                event.accept(ModRegistry.SwiftBladeWood.get());
                event.accept(ModRegistry.SwiftBladeStone.get());
                event.accept(ModRegistry.SwiftBladeIron.get());
                event.accept(ModRegistry.SwiftBladeDiamond.get());
                event.accept(ModRegistry.SwiftBladeGold.get());
                event.accept(ModRegistry.SwiftBladeCopper.get());
                event.accept(ModRegistry.SwiftBladeOsmium.get());
                event.accept(ModRegistry.SwiftBladeBronze.get());
                event.accept(ModRegistry.SwiftBladeSteel.get());
                event.accept(ModRegistry.SwiftBladeObsidian.get());
                event.accept(ModRegistry.SwiftBladeNetherite.get());
                event.accept(ModRegistry.SickleWood.get());
                event.accept(ModRegistry.SickleStone.get());
                event.accept(ModRegistry.SickleGold.get());
                event.accept(ModRegistry.SickleIron.get());
                event.accept(ModRegistry.SickleDiamond.get());
                event.accept(ModRegistry.SickleNetherite.get());
                event.accept(ModRegistry.ItemEmptyCrate.get());
                event.accept(ModRegistry.ClutchOfEggs.get());
                event.accept(ModRegistry.ItemCartonOfEggs.get());
                event.accept(ModRegistry.BunchOfPotatoes.get());
                event.accept(ModRegistry.ItemCrateOfPotatoes.get());
                event.accept(ModRegistry.BunchOfCarrots.get());
                event.accept(ModRegistry.ItemCrateOfCarrots.get());
                event.accept(ModRegistry.BunchOfBeets.get());
                event.accept(ModRegistry.ItemCrateOfBeets.get());

                event.accept(ModRegistry.InstantBridge.get());
                event.accept(ModRegistry.StartHouse.get());
                event.accept(ModRegistry.ModerateHouse.get());
                event.accept(ModRegistry.AdvancedHouse.get());
                event.accept(ModRegistry.Bulldozer.get());
                event.accept(ModRegistry.Creative_Bulldozer.get());
                event.accept(ModRegistry.MachineryTower.get());
                event.accept(ModRegistry.DefenseBunker.get());
                event.accept(ModRegistry.MineshaftEntrance.get());
                event.accept(ModRegistry.EnderGateway.get());
                event.accept(ModRegistry.GrassyPlain.get());
                event.accept(ModRegistry.MagicTemple.get());
                event.accept(ModRegistry.WatchTower.get());
                event.accept(ModRegistry.WelcomeCenter.get());
                event.accept(ModRegistry.Jail.get());
                event.accept(ModRegistry.Saloon.get());
                event.accept(ModRegistry.SkiLodge.get());
                event.accept(ModRegistry.WindMill.get());
                event.accept(ModRegistry.TownHall.get());
                event.accept(ModRegistry.NetherGate.get());
                event.accept(ModRegistry.AquaBase.get());
                event.accept(ModRegistry.AdvancedAquaBase.get());
                event.accept(ModRegistry.WareHouse.get());
                event.accept(ModRegistry.AdvancedWareHouse.get());
                event.accept(ModRegistry.VillagerHouses.get());
                event.accept(ModRegistry.ModernBuilding.get());
                event.accept(ModRegistry.ModerateModernBuildings.get());
                event.accept(ModRegistry.AdvancedModernBuildings.get());
                event.accept(ModRegistry.StarterFarm.get());
                event.accept(ModRegistry.ModerateFarm.get());
                event.accept(ModRegistry.AdvancedFarm.get());

                if (Prefab.isDebug) {
                    event.accept(ModRegistry.StructureScannerItem.get());
                }
            }

            /*ModRegistry.PREFAB_GROUP = event.registerCreativeModeTab(new ResourceLocation(Prefab.MODID, "logo"),
                    builder -> builder.icon(() -> new ItemStack(ModRegistry.ItemLogo.get()))
                    .title(Component.translatable("itemGroup.prefab.logo"))
                    .withLabelColor(0x00FF00)
                    .displayItems((context, entries) -> {
                    }));*/
        }
    }
}
