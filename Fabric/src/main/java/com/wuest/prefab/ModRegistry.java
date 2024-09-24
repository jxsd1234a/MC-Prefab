package com.wuest.prefab;

import com.prefab.ModRegistryBase;
import com.prefab.PrefabBase;
import com.prefab.Utils;
import com.prefab.blocks.*;
import com.prefab.blocks.entities.LightSwitchBlockEntity;
import com.prefab.blocks.entities.StructureScannerBlockEntity;
import com.prefab.config.StructureScannerConfig;
import com.wuest.prefab.blocks.BlockBoundary;
import com.wuest.prefab.items.ItemCompressedChest;
import com.wuest.prefab.items.ItemSickle;
import com.wuest.prefab.network.message.ConfigSyncPayload;
import com.wuest.prefab.network.message.PlayerConfigPayload;
import com.wuest.prefab.network.message.ScanShapePayload;
import com.wuest.prefab.network.message.ScannerConfigPayload;
import com.prefab.structures.config.BasicStructureConfiguration;
import com.prefab.structures.config.StructureConfiguration;
import com.wuest.prefab.structures.messages.StructurePayload;
import com.prefab.structures.messages.StructureTagMessage;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This is the mod registry so there is a way to get to all instances of the blocks/items created by this mod.
 *
 * @author WuestMan
 */
public class ModRegistry {
    private static final ArrayList<Item> ModItems = new ArrayList<>();
    public static final ArrayList<Consumer<Object>> guiRegistrations = new ArrayList<>();

    /* *********************************** Item Group *********************************** */
    private static final CreativeModeTab ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModRegistryBase.LogoItem))
            .displayItems((context, entries) -> {
                for (Item item : ModRegistry.ModItems) {
                    if (item == ModRegistryBase.StructureScannerItem && !PrefabBase.isDebug) {
                        continue;
                    } else if (item == ModRegistryBase.LogoItem) {
                        continue;
                    }

                    entries.accept(item);
                }
            })
            .title(Utils.createTextComponent("Prefab"))
            .build();

    // This variable may not be used, but the registration is still needed.
    public static final CreativeModeTab creativeModeTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.tryBuild(PrefabBase.MODID, "logo"), ITEM_GROUP);

    public static void registerModComponents() {
        ModRegistry.doFabricSpecificReplacements();

        ModRegistry.registerSounds();

        ModRegistry.registerBlocks();

        ModRegistry.registerBlockEntities();

        ModRegistry.registerItems();

        ModRegistry.registerBluePrints();

        ModRegistry.registerItemBlocks();

        ModRegistry.registerPayloadTypes();

        ModRegistry.RegisterClientToServerMessageHandlers();

        ModRegistry.RegisterRecipeSerializers();
    }

    /**
     * This method is to do Fabric specific replacements of ModRegistryBase objects.
     * This is generally due to environment sided interactions which have specific attributes for different mod-loaders
     * or different events fired for the various mod-loaders
     */
    private static void doFabricSpecificReplacements() {
        ModRegistry.doFabricBlockEntityReplacements();

        ModRegistry.doFabricBlockReplacements();

        ModRegistry.doFabricItemReplacements();

        ModRegistry.doFabricBluePrintReplacements();

        ModRegistry.doFabricItemBlockReplacements();
    }

    private static void doFabricBlockEntityReplacements() {

    }

    private static void doFabricBlockReplacements() {
        // Always make sure do re-do the block item when replacing a block.
        ModRegistryBase.Boundary = new BlockBoundary();
        ModRegistryBase.GlassSlab = new BlockGlassSlab(Block.Properties.ofFullCopy(Blocks.GLASS));
        ModRegistryBase.GlassStairs = new BlockGlassStairs(Blocks.GLASS.defaultBlockState(), Block.Properties.ofFullCopy(Blocks.GLASS));
        ModRegistryBase.PaperLantern = new BlockPaperLantern();
        ModRegistryBase.Phasic = new BlockPhasic();
    }

    private static void doFabricItemReplacements() {
        ModRegistryBase.CompressedChest = new ItemCompressedChest();
        ModRegistryBase.SickleDiamond = new ItemSickle(Tiers.DIAMOND);
        ModRegistryBase.SickleGold = new ItemSickle(Tiers.GOLD);
        ModRegistryBase.SickleNetherite = new ItemSickle(Tiers.NETHERITE);
        ModRegistryBase.SickleIron = new ItemSickle(Tiers.IRON);
        ModRegistryBase.SickleStone = new ItemSickle(Tiers.STONE);
        ModRegistryBase.SickleWood = new ItemSickle(Tiers.WOOD);
    }

    private static void doFabricBluePrintReplacements() {

    }

    private static void doFabricItemBlockReplacements() {
        // Always make sure do re-do the block item when replacing a block.
        ModRegistryBase.BoundaryItem = new BlockItem(ModRegistryBase.Boundary, new Item.Properties());
        ModRegistryBase.GlassSlabItem = new BlockItem(ModRegistryBase.GlassSlab, new Item.Properties());
        ModRegistryBase.GlassStairsItem = new BlockItem(ModRegistryBase.GlassStairs, new Item.Properties());
        ModRegistryBase.PaperLanternItem = new BlockItem(ModRegistryBase.PaperLantern, new Item.Properties());
        ModRegistryBase.PhasicItem = new BlockItem(ModRegistryBase.Phasic, new Item.Properties());
    }

    private static void registerSounds() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, ResourceLocation.tryBuild(PrefabBase.MODID, "building_blueprint"), ModRegistryBase.BuildingBlueprint);
    }

    private static void registerBlockEntities() {
        if (PrefabBase.isDebug) {
            ModRegistryBase.StructureScannerEntityType = Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    "prefab:structure_scanner_entity",
                    BlockEntityType.Builder
                            .of(StructureScannerBlockEntity::new, ModRegistryBase.StructureScanner)
                            .build(null));
        }

        ModRegistryBase.LightSwitchEntityType = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                "prefab:light_switch_entity",
                BlockEntityType.Builder
                        .of(LightSwitchBlockEntity::new, ModRegistryBase.LightSwitch)
                        .build(null));
    }

    private static void registerBlocks() {
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.CompressedStone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedStone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.TripleCompressedStone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_DIRT.getUnlocalizedName(), ModRegistryBase.CompressedDirt);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT.getUnlocalizedName(), ModRegistryBase.DoubleCompressedDirt);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistryBase.CompressedGlowstone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedGlowstone);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistryBase.CompressedQuartzCrete);
        ModRegistry.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedQuartzCrete);
        ModRegistry.registerBlock(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.toString(), ModRegistryBase.CompressedObsidian);
        ModRegistry.registerBlock(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.toString(), ModRegistryBase.DoubleCompressedObsidian);
        ModRegistry.registerBlock("block_glass_slab", ModRegistryBase.GlassSlab);
        ModRegistry.registerBlock("block_glass_stairs", ModRegistryBase.GlassStairs);
        ModRegistry.registerBlock("block_paper_lantern", ModRegistryBase.PaperLantern);
        ModRegistry.registerBlock("block_phasic", ModRegistryBase.Phasic);
        ModRegistry.registerBlock("block_boundary", ModRegistryBase.Boundary);
        ModRegistry.registerBlock("block_grass_slab", ModRegistryBase.GrassSlab);
        ModRegistry.registerBlock("block_grass_stairs", ModRegistryBase.GrassStairs);
        ModRegistry.registerBlock(BlockCustomWall.EnumType.GRASS.getUnlocalizedName(), ModRegistryBase.GrassWall);
        ModRegistry.registerBlock(BlockCustomWall.EnumType.DIRT.getUnlocalizedName(), ModRegistryBase.DirtWall);
        ModRegistry.registerBlock("block_dirt_stairs", ModRegistryBase.DirtStairs);
        ModRegistry.registerBlock("block_dirt_slab", ModRegistryBase.DirtSlab);

        ModRegistry.registerBlock("item_pile_of_bricks", ModRegistryBase.PileOfBricks);
        ModRegistry.registerBlock("item_pallet_of_bricks", ModRegistryBase.PalletOfBricks);
        ModRegistry.registerBlock("item_bundle_of_timber", ModRegistryBase.BundleOfTimber);
        ModRegistry.registerBlock("item_heap_of_timber", ModRegistryBase.HeapOfTimber);
        ModRegistry.registerBlock("item_ton_of_timber", ModRegistryBase.TonOfTimber);

        ModRegistry.registerBlock("item_wooden_crate", ModRegistryBase.EmptyCrate);
        ModRegistry.registerBlock("item_carton_of_eggs", ModRegistryBase.CartonOfEggs);
        ModRegistry.registerBlock("item_crate_of_potatoes", ModRegistryBase.CrateOfPotatoes);
        ModRegistry.registerBlock("item_crate_of_carrots", ModRegistryBase.CrateOfCarrots);
        ModRegistry.registerBlock("item_crate_of_beets", ModRegistryBase.CrateOfBeets);

        if (PrefabBase.isDebug) {
            ModRegistryBase.StructureScanner = new BlockStructureScanner();
            ModRegistry.registerBlock("block_structure_scanner", ModRegistryBase.StructureScanner);
        }

        ModRegistry.registerBlock("block_light_switch", ModRegistryBase.LightSwitch);
        ModRegistry.registerBlock("block_dark_lamp", ModRegistryBase.DarkLamp);

        ModRegistry.registerBlock("block_quartz_crete", ModRegistryBase.QuartzCrete);
        ModRegistry.registerBlock("block_quartz_crete_wall", ModRegistryBase.QuartzCreteWall);
        ModRegistry.registerBlock("block_quartz_crete_bricks", ModRegistryBase.QuartzCreteBricks);
        ModRegistry.registerBlock("block_quartz_crete_chiseled", ModRegistryBase.ChiseledQuartzCrete);
        ModRegistry.registerBlock("block_quartz_crete_pillar", ModRegistryBase.QuartzCretePillar);
        ModRegistry.registerBlock("block_quartz_crete_stairs", ModRegistryBase.QuartzCreteStairs);
        ModRegistry.registerBlock("block_quartz_crete_slab", ModRegistryBase.QuartzCreteSlab);
        ModRegistry.registerBlock("block_quartz_crete_smooth", ModRegistryBase.SmoothQuartzCrete);
        ModRegistry.registerBlock("block_quartz_crete_smooth_wall", ModRegistryBase.SmoothQuartzCreteWall);
        ModRegistry.registerBlock("block_quartz_crete_smooth_stairs", ModRegistryBase.SmoothQuartzCreteStairs);
        ModRegistry.registerBlock("block_quartz_crete_smooth_slab", ModRegistryBase.SmoothQuartzCreteSlab);
    }

    private static void registerItems() {
        ModRegistry.registerItem("item_logo", ModRegistryBase.LogoItem);
        ModRegistry.registerItem("item_pile_of_bricks", ModRegistryBase.ItemPileOfBricks);
        ModRegistry.registerItem("item_pallet_of_bricks", ModRegistryBase.ItemPalletOfBricks);
        ModRegistry.registerItem("item_bundle_of_timber", ModRegistryBase.ItemBundleOfTimber);
        ModRegistry.registerItem("item_heap_of_timber", ModRegistryBase.ItemHeapOfTimber);
        ModRegistry.registerItem("item_ton_of_timber", ModRegistryBase.ItemTonOfTimber);
        ModRegistry.registerItem("item_string_of_lanterns", ModRegistryBase.StringOfLanterns);
        ModRegistry.registerItem("item_coil_of_lanterns", ModRegistryBase.CoilOfLanterns);
        ModRegistry.registerItem("item_compressed_chest", ModRegistryBase.CompressedChest);
        ModRegistry.registerItem("item_upgrade", ModRegistryBase.Upgrade);

        ModRegistry.registerItem("item_swift_blade_wood", ModRegistryBase.SwiftBladeWood);
        ModRegistry.registerItem("item_swift_blade_stone", ModRegistryBase.SwiftBladeStone);
        ModRegistry.registerItem("item_swift_blade_iron", ModRegistryBase.SwiftBladeIron);
        ModRegistry.registerItem("item_swift_blade_diamond", ModRegistryBase.SwiftBladeDiamond);
        ModRegistry.registerItem("item_swift_blade_gold", ModRegistryBase.SwiftBladeGold);
        ModRegistry.registerItem("item_swift_blade_copper", ModRegistryBase.SwiftBladeCopper);
        ModRegistry.registerItem("item_swift_blade_osmium", ModRegistryBase.SwiftBladeOsmium);
        ModRegistry.registerItem("item_swift_blade_bronze", ModRegistryBase.SwiftBladeBronze);
        ModRegistry.registerItem("item_swift_blade_steel", ModRegistryBase.SwiftBladeSteel);
        ModRegistry.registerItem("item_swift_blade_obsidian", ModRegistryBase.SwiftBladeObsidian);
        ModRegistry.registerItem("item_swift_blade_netherite", ModRegistryBase.SwiftBladeNetherite);

        ModRegistry.registerItem("item_sickle_wood", ModRegistryBase.SickleWood);
        ModRegistry.registerItem("item_sickle_stone", ModRegistryBase.SickleStone);
        ModRegistry.registerItem("item_sickle_gold", ModRegistryBase.SickleGold);
        ModRegistry.registerItem("item_sickle_iron", ModRegistryBase.SickleIron);
        ModRegistry.registerItem("item_sickle_diamond", ModRegistryBase.SickleDiamond);
        ModRegistry.registerItem("item_sickle_netherite", ModRegistryBase.SickleNetherite);

        ModRegistry.registerItem("item_wooden_crate", ModRegistryBase.ItemEmptyCrate);
        ModRegistry.registerItem("item_clutch_of_eggs", ModRegistryBase.ClutchOfEggs);
        ModRegistry.registerItem("item_carton_of_eggs", ModRegistryBase.ItemCartonOfEggs);
        ModRegistry.registerItem("item_bunch_of_potatoes", ModRegistryBase.BunchOfPotatoes);
        ModRegistry.registerItem("item_crate_of_potatoes", ModRegistryBase.ItemCrateOfPotatoes);
        ModRegistry.registerItem("item_bunch_of_carrots", ModRegistryBase.BunchOfCarrots);
        ModRegistry.registerItem("item_crate_of_carrots", ModRegistryBase.ItemCrateOfCarrots);
        ModRegistry.registerItem("item_bunch_of_beets", ModRegistryBase.BunchOfBeets);
        ModRegistry.registerItem("item_crate_of_beets", ModRegistryBase.ItemCrateOfBeets);
    }

    private static void registerBluePrints() {
        ModRegistry.registerItem("item_house", ModRegistryBase.House);
        ModRegistry.registerItem("item_instant_bridge", ModRegistryBase.InstantBridge);
        ModRegistry.registerItem("item_house_improved", ModRegistryBase.HouseImproved);
        ModRegistry.registerItem("item_house_advanced", ModRegistryBase.HouseAdvanced);
        ModRegistry.registerItem("item_bulldozer", ModRegistryBase.Bulldozer);
        ModRegistry.registerItem("item_creative_bulldozer", ModRegistryBase.CreativeBulldozer);

        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MachineryTower.getItemTextureLocation().getPath(), ModRegistryBase.MachineryTower);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.DefenseBunker.getItemTextureLocation().getPath(), ModRegistryBase.DefenseBunker);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MineshaftEntrance.getItemTextureLocation().getPath(), ModRegistryBase.MineshaftEntrance);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.EnderGateway.getItemTextureLocation().getPath(), ModRegistryBase.EnderGateway);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.AquaBase.getItemTextureLocation().getPath(), ModRegistryBase.AquaBase);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.GrassyPlain.getItemTextureLocation().getPath(), ModRegistryBase.GrassyPlain);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MagicTemple.getItemTextureLocation().getPath(), ModRegistryBase.MagicTemple);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WatchTower.getItemTextureLocation().getPath(), ModRegistryBase.WatchTower);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WelcomeCenter.getItemTextureLocation().getPath(), ModRegistryBase.WelcomeCenter);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Jail.getItemTextureLocation().getPath(), ModRegistryBase.Jail);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Saloon.getItemTextureLocation().getPath(), ModRegistryBase.Saloon);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.SkiLodge.getItemTextureLocation().getPath(), ModRegistryBase.SkiLodge);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WindMill.getItemTextureLocation().getPath(), ModRegistryBase.WindMill);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.TownHall.getItemTextureLocation().getPath(), ModRegistryBase.TownHall);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.NetherGate.getItemTextureLocation().getPath(), ModRegistryBase.NetherGate);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.AquaBaseImproved.getItemTextureLocation().getPath(), ModRegistryBase.AquaBaseImproved);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Warehouse.getItemTextureLocation().getPath(), ModRegistryBase.Warehouse);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WarehouseImproved.getItemTextureLocation().getPath(), ModRegistryBase.WareHouseImproved);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.VillagerHouses.getItemTextureLocation().getPath(), ModRegistryBase.VillagerHouses);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildings.getItemTextureLocation().getPath(), ModRegistryBase.ModernBuildings);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildingsImproved.getItemTextureLocation().getPath(), ModRegistryBase.ModernBuildingsImproved);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildingsAdvanced.getItemTextureLocation().getPath(), ModRegistryBase.ModernBuildingsAdvanced);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Farm.getItemTextureLocation().getPath(), ModRegistryBase.Farm);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.FarmImproved.getItemTextureLocation().getPath(), ModRegistryBase.FarmImproved);
        ModRegistry.registerItem(BasicStructureConfiguration.EnumBasicStructureName.FarmAdvanced.getItemTextureLocation().getPath(), ModRegistryBase.FarmAdvanced);
    }

    private static void registerItemBlocks() {
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.CompressedStoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedStoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.TripleCompressedStoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_DIRT.getUnlocalizedName(), ModRegistryBase.CompressedDirtItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT.getUnlocalizedName(), ModRegistryBase.DoubleCompressedDirtItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistryBase.CompressedGlowstoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedGlowstoneItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistryBase.CompressedQuartzCreteItem);
        ModRegistry.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedQuartzCreteItem);
        ModRegistry.registerItem(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.toString(), ModRegistryBase.CompressedObsidianItem);
        ModRegistry.registerItem(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.toString(), ModRegistryBase.DoubleCompressedObsidianItem);
        ModRegistry.registerItem("block_glass_slab", ModRegistryBase.GlassSlabItem);
        ModRegistry.registerItem("block_glass_stairs", ModRegistryBase.GlassStairsItem);
        ModRegistry.registerItem("block_paper_lantern", ModRegistryBase.PaperLanternItem);
        ModRegistry.registerItem("block_phasic", ModRegistryBase.PhasicItem);
        ModRegistry.registerItem("block_boundary", ModRegistryBase.BoundaryItem);
        ModRegistry.registerItem("block_grass_slab", ModRegistryBase.GrassSlabItem);
        ModRegistry.registerItem("block_grass_stairs", ModRegistryBase.GrassStairsItem);
        ModRegistry.registerItem(BlockCustomWall.EnumType.GRASS.getUnlocalizedName(), ModRegistryBase.GrassWallItem);
        ModRegistry.registerItem(BlockCustomWall.EnumType.DIRT.getUnlocalizedName(), ModRegistryBase.DirtWallItem);
        ModRegistry.registerItem("block_dirt_stairs", ModRegistryBase.DirtStairsItem);
        ModRegistry.registerItem("block_dirt_slab", ModRegistryBase.DirtSlabItem);

        if (PrefabBase.isDebug) {
            ModRegistryBase.StructureScannerItem = new BlockItem(ModRegistryBase.StructureScanner, new Item.Properties());
            ModRegistry.registerItem("block_structure_scanner", ModRegistryBase.StructureScannerItem);
        }

        ModRegistry.registerItem("block_light_switch", ModRegistryBase.LightSwitchItem);
        ModRegistry.registerItem("block_dark_lamp", ModRegistryBase.DarkLampItem);

        ModRegistry.registerItem("block_quartz_crete", ModRegistryBase.QuartzCreteItem);
        ModRegistry.registerItem("block_quartz_crete_wall", ModRegistryBase.QuartzCreteWallItem);
        ModRegistry.registerItem("block_quartz_crete_bricks", ModRegistryBase.QuartzCreteBricksItem);
        ModRegistry.registerItem("block_quartz_crete_chiseled", ModRegistryBase.ChiseledQuartzCreteItem);
        ModRegistry.registerItem("block_quartz_crete_pillar", ModRegistryBase.QuartzCretePillarItem);
        ModRegistry.registerItem("block_quartz_crete_stairs", ModRegistryBase.QuartzCreteStairsItem);
        ModRegistry.registerItem("block_quartz_crete_slab", ModRegistryBase.QuartzCreteSlabItem);
        ModRegistry.registerItem("block_quartz_crete_smooth", ModRegistryBase.SmoothQuartzCreteItem);
        ModRegistry.registerItem("block_quartz_crete_smooth_wall", ModRegistryBase.SmoothQuartzCreteWallItem);
        ModRegistry.registerItem("block_quartz_crete_smooth_stairs", ModRegistryBase.SmoothQuartzCreteStairsItem);
        ModRegistry.registerItem("block_quartz_crete_smooth_slab", ModRegistryBase.SmoothQuartzCreteSlabItem);
    }

    /**
     * This is where the mod messages are registered.
     */
    private static void RegisterClientToServerMessageHandlers() {

        ModRegistry.registerStructureBuilderMessageHandler();

        ModRegistry.registerStructureScannerMessageHandler();

        ModRegistry.registerStructureScannerActionMessageHandler();
    }

    private static void RegisterRecipeSerializers() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.tryBuild(PrefabBase.MODID, "condition_crafting_shaped"), ModRegistryBase.ConditionedShapedRecipeSeriaizer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.tryBuild(PrefabBase.MODID, "condition_crafting_shapeless"), ModRegistryBase.ConditionedShapelessRecipeSeriaizer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.tryBuild(PrefabBase.MODID, "condition_smelting"), ModRegistryBase.ConditionedSmeltingRecipeSeriaizer);
    }

    private static void registerBlock(String registryName, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.tryBuild(PrefabBase.MODID, registryName), block);
    }

    private static void registerItem(String registryName, Item item) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.tryBuild(PrefabBase.MODID, registryName), item);
        ModRegistry.ModItems.add(item);
    }

    private static void registerPayloadTypes() {
        PayloadTypeRegistry.playC2S().register(StructurePayload.PACKET_TYPE, StructurePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ScannerConfigPayload.PACKET_TYPE, ScannerConfigPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ScanShapePayload.PACKET_TYPE, ScanShapePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.PACKET_TYPE, ConfigSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerConfigPayload.PACKET_TYPE, PlayerConfigPayload.STREAM_CODEC);
    }

    private static void registerStructureBuilderMessageHandler() {
        ServerPlayNetworking.registerGlobalReceiver(StructurePayload.PACKET_TYPE, (payLoad, context) -> {
            // Packet processor, data will already have been de-serialized.
            // Can only access the "attachedData" on the "network thread" which is here.
            StructureTagMessage.EnumStructureConfiguration structureConfig = payLoad.structureTagMessage().getStructureConfig();

            context.player().getServer().execute(() -> {
                // This is now on the "main" server thread and things can be done in the world!
                StructureConfiguration configuration = structureConfig.structureConfig.ReadFromCompoundTag(payLoad.structureTagMessage().getMessageTag());

                configuration.BuildStructure(context.player(), context.player().serverLevel());
            });
        });
    }

    private static void registerStructureScannerMessageHandler() {
        ServerPlayNetworking.registerGlobalReceiver(ScannerConfigPayload.PACKET_TYPE, (payLoad, context) -> {
            // Packet processor, data will already have been de-serialized.
            context.player().getServer().execute(() -> {
                StructureScannerConfig config = payLoad.scannerInfo().ToConfig();

                // The GUI always goes down 1 block for it's processing, so we have to make sure we go UP a block.
                BlockEntity blockEntity = context.player().level().getBlockEntity(config.blockPos.above());

                if (blockEntity instanceof StructureScannerBlockEntity actualEntity) {
                    actualEntity.setConfig(config);
                }
            });
        });
    }

    private static void registerStructureScannerActionMessageHandler() {
        ServerPlayNetworking.registerGlobalReceiver(ScanShapePayload.PACKET_TYPE, (payLoad, context) -> {
            // Packet processor, data will already have been de-serialized.
            context.player().getServer().execute(() -> {
                StructureScannerConfig config = payLoad.scannerInfo().ToConfig();

                StructureScannerBlockEntity.ScanShape(config, context.player(), context.player().serverLevel());
            });
        });
    }

    public static boolean always(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    public static boolean never(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    public enum CustomItemTier implements Tier {
        COPPER("Copper", (int)Tiers.STONE.getAttackDamageBonus(), Tiers.STONE.getUses(), Tiers.STONE.getSpeed(),
                Tiers.STONE.getAttackDamageBonus(), Tiers.STONE.getEnchantmentValue(), () -> {
            return Ingredient
                    .of(Utils.getItemStacksWithTag(ResourceLocation.tryBuild("c", "copper_ingots")).stream());
        }, BlockTags.INCORRECT_FOR_STONE_TOOL),
        OSMIUM("Osmium", (int)Tiers.IRON.getAttackDamageBonus(), 500, Tiers.IRON.getSpeed(),
                Tiers.IRON.getAttackDamageBonus() + .5f, Tiers.IRON.getEnchantmentValue(), () -> {
            return Ingredient
                    .of(Utils.getItemStacksWithTag(ResourceLocation.tryBuild("c", "osmium_ingots")).stream());
        }, BlockTags.INCORRECT_FOR_IRON_TOOL),
        BRONZE("Bronze", (int)Tiers.IRON.getAttackDamageBonus(), Tiers.IRON.getUses(), Tiers.IRON.getSpeed(),
                Tiers.IRON.getAttackDamageBonus(), Tiers.IRON.getEnchantmentValue(), () -> {
            return Ingredient
                    .of(Utils.getItemStacksWithTag(ResourceLocation.tryBuild("c", "bronze_ingots")).stream());
        }, BlockTags.INCORRECT_FOR_IRON_TOOL),
        STEEL("Steel", (int)Tiers.DIAMOND.getAttackDamageBonus(), (int) (Tiers.IRON.getUses() * 1.5),
                Tiers.DIAMOND.getSpeed(), Tiers.DIAMOND.getAttackDamageBonus(),
                Tiers.DIAMOND.getEnchantmentValue(), () -> {
            return Ingredient
                    .of(Utils.getItemStacksWithTag(ResourceLocation.tryBuild("c", "steel_ingots")).stream());
        }, BlockTags.INCORRECT_FOR_DIAMOND_TOOL),
        OBSIDIAN("Obsidian", (int)Tiers.DIAMOND.getAttackDamageBonus(), (int) (Tiers.DIAMOND.getUses() * 1.5),
                Tiers.DIAMOND.getSpeed(), Tiers.DIAMOND.getAttackDamageBonus(),
                Tiers.DIAMOND.getEnchantmentValue(), () -> {
            return Ingredient.of(Item.byBlock(Blocks.OBSIDIAN));
        }, BlockTags.INCORRECT_FOR_DIAMOND_TOOL);

        private final String name;
        private final int harvestLevel;
        private final int maxUses;
        private final float efficiency;
        private final float attackDamage;
        private final int enchantability;
        private final LazyLoadedValue<Ingredient> repairMaterial;
        private final TagKey<Block> incorrectBlocksForDrops;

        CustomItemTier(String name, int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn,
                       int enchantability, Supplier<Ingredient> repairMaterialIn, TagKey<Block> incorrectBlocksForDrops) {
            this.name = name;
            this.harvestLevel = harvestLevelIn;
            this.maxUses = maxUsesIn;
            this.efficiency = efficiencyIn;
            this.attackDamage = attackDamageIn;
            this.enchantability = enchantability;
            this.repairMaterial = new LazyLoadedValue<>(repairMaterialIn);
            this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        }

        public static CustomItemTier getByName(String name) {
            for (CustomItemTier item : CustomItemTier.values()) {
                if (item.getName().equals(name)) {
                    return item;
                }
            }

            return null;
        }

        public String getName() {
            return this.name;
        }

        public int getUses() {
            return this.maxUses;
        }

        public float getSpeed() {
            return this.efficiency;
        }

        public float getAttackDamageBonus() {
            return this.attackDamage;
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return this.incorrectBlocksForDrops;
        }

        public int getLevel() {
            return this.harvestLevel;
        }

        public int getEnchantmentValue() {
            return this.enchantability;
        }

        public @NotNull Ingredient getRepairIngredient() {
            return this.repairMaterial.get();
        }
    }
}