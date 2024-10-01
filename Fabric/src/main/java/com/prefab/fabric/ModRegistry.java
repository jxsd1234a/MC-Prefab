package com.prefab.fabric;

import com.prefab.ModRegistryBase;
import com.prefab.PrefabBase;
import com.prefab.Utils;
import com.prefab.blocks.*;
import com.prefab.blocks.entities.LightSwitchBlockEntity;
import com.prefab.blocks.entities.StructureScannerBlockEntity;
import com.prefab.fabric.blocks.BlockBoundary;
import com.prefab.fabric.blocks.BlockGlassSlab;
import com.prefab.fabric.blocks.BlockGlassStairs;
import com.prefab.fabric.blocks.BlockPaperLantern;
import com.prefab.fabric.blocks.BlockPhasic;
import com.prefab.fabric.items.ItemBulldozer;
import com.prefab.fabric.items.ItemCompressedChest;
import com.prefab.fabric.items.ItemSickle;
import com.prefab.network.payloads.ConfigSyncPayload;
import com.prefab.network.payloads.PlayerConfigPayload;
import com.prefab.network.payloads.ScanShapePayload;
import com.prefab.network.payloads.ScannerConfigPayload;
import com.prefab.structures.config.BasicStructureConfiguration;
import com.prefab.network.payloads.StructurePayload;
import com.prefab.fabric.network.ServerPayloadHandler;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;

/**
 * This is the mod registry so there is a way to get to all instances of the blocks/items created by this mod.
 *
 * @author WuestMan
 */
public class ModRegistry extends ModRegistryBase {
    private static final ArrayList<Item> ModItems = new ArrayList<>();

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

    @Override
    public void initializeModLoaderBlocks() {
        // Always make sure to fully qualify WHICH block we are creating...
        ModRegistryBase.Boundary = new BlockBoundary();
        ModRegistryBase.GlassSlab = new BlockGlassSlab(Block.Properties.ofFullCopy(Blocks.GLASS));
        ModRegistryBase.GlassStairs = new BlockGlassStairs(Blocks.GLASS.defaultBlockState(), Block.Properties.ofFullCopy(Blocks.GLASS));
        ModRegistryBase.PaperLantern = new BlockPaperLantern();
        ModRegistryBase.Phasic = new BlockPhasic();
    }

    @Override
    public void initializeModLoaderBlockItems() {
        // Always make sure do re-do the block item when replacing a block.
        ModRegistryBase.BoundaryItem = new BlockItem(ModRegistryBase.Boundary, new Item.Properties());
        ModRegistryBase.GlassSlabItem = new BlockItem(ModRegistryBase.GlassSlab, new Item.Properties());
        ModRegistryBase.GlassStairsItem = new BlockItem(ModRegistryBase.GlassStairs, new Item.Properties());
        ModRegistryBase.PaperLanternItem = new BlockItem(ModRegistryBase.PaperLantern, new Item.Properties());
        ModRegistryBase.PhasicItem = new BlockItem(ModRegistryBase.Phasic, new Item.Properties());
    }

    @Override
    public void initializeModLoaderItems() {
        ModRegistryBase.CompressedChest = new ItemCompressedChest();
        ModRegistryBase.SickleDiamond = new ItemSickle(Tiers.DIAMOND);
        ModRegistryBase.SickleGold = new ItemSickle(Tiers.GOLD);
        ModRegistryBase.SickleNetherite = new ItemSickle(Tiers.NETHERITE);
        ModRegistryBase.SickleIron = new ItemSickle(Tiers.IRON);
        ModRegistryBase.SickleStone = new ItemSickle(Tiers.STONE);
        ModRegistryBase.SickleWood = new ItemSickle(Tiers.WOOD);
    }

    @Override
    public void initializeModLoaderBluePrintItems() {
        // Always make sure to fully qualify WHICH item we are creating...
        ModRegistryBase.Bulldozer = new ItemBulldozer();
        ModRegistryBase.CreativeBulldozer = new ItemBulldozer(true);
    }

    public void registerModComponents() {
        this.registerSounds();

        this.registerBlocks();

        this.registerBlockEntities();

        this.registerItems();

        this.registerBluePrints();

        this.registerItemBlocks();

        this.registerPayloadTypes();

        this.RegisterClientToServerMessageHandlers();

        this.RegisterRecipeSerializers();
    }

    private void registerSounds() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, ResourceLocation.tryBuild(PrefabBase.MODID, "building_blueprint"), ModRegistryBase.BuildingBlueprint);
    }

    private void registerBlockEntities() {
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

    private void registerBlocks() {
        this.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.CompressedStone);
        this.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedStone);
        this.registerBlock(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.TripleCompressedStone);
        this.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_DIRT.getUnlocalizedName(), ModRegistryBase.CompressedDirt);
        this.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT.getUnlocalizedName(), ModRegistryBase.DoubleCompressedDirt);
        this.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistryBase.CompressedGlowstone);
        this.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedGlowstone);
        this.registerBlock(BlockCompressedStone.EnumType.COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistryBase.CompressedQuartzCrete);
        this.registerBlock(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedQuartzCrete);
        this.registerBlock(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.toString(), ModRegistryBase.CompressedObsidian);
        this.registerBlock(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.toString(), ModRegistryBase.DoubleCompressedObsidian);
        this.registerBlock("block_glass_slab", ModRegistryBase.GlassSlab);
        this.registerBlock("block_glass_stairs", ModRegistryBase.GlassStairs);
        this.registerBlock("block_paper_lantern", ModRegistryBase.PaperLantern);
        this.registerBlock("block_phasic", ModRegistryBase.Phasic);
        this.registerBlock("block_boundary", ModRegistryBase.Boundary);
        this.registerBlock("block_grass_slab", ModRegistryBase.GrassSlab);
        this.registerBlock("block_grass_stairs", ModRegistryBase.GrassStairs);
        this.registerBlock(BlockCustomWall.EnumType.GRASS.getUnlocalizedName(), ModRegistryBase.GrassWall);
        this.registerBlock(BlockCustomWall.EnumType.DIRT.getUnlocalizedName(), ModRegistryBase.DirtWall);
        this.registerBlock("block_dirt_stairs", ModRegistryBase.DirtStairs);
        this.registerBlock("block_dirt_slab", ModRegistryBase.DirtSlab);

        this.registerBlock("item_pile_of_bricks", ModRegistryBase.PileOfBricks);
        this.registerBlock("item_pallet_of_bricks", ModRegistryBase.PalletOfBricks);
        this.registerBlock("item_bundle_of_timber", ModRegistryBase.BundleOfTimber);
        this.registerBlock("item_heap_of_timber", ModRegistryBase.HeapOfTimber);
        this.registerBlock("item_ton_of_timber", ModRegistryBase.TonOfTimber);

        this.registerBlock("item_wooden_crate", ModRegistryBase.EmptyCrate);
        this.registerBlock("item_carton_of_eggs", ModRegistryBase.CartonOfEggs);
        this.registerBlock("item_crate_of_potatoes", ModRegistryBase.CrateOfPotatoes);
        this.registerBlock("item_crate_of_carrots", ModRegistryBase.CrateOfCarrots);
        this.registerBlock("item_crate_of_beets", ModRegistryBase.CrateOfBeets);

        if (PrefabBase.isDebug) {
            ModRegistryBase.StructureScanner = new BlockStructureScanner();
            this.registerBlock("block_structure_scanner", ModRegistryBase.StructureScanner);
        }

        this.registerBlock("block_light_switch", ModRegistryBase.LightSwitch);
        this.registerBlock("block_dark_lamp", ModRegistryBase.DarkLamp);

        this.registerBlock("block_quartz_crete", ModRegistryBase.QuartzCrete);
        this.registerBlock("block_quartz_crete_wall", ModRegistryBase.QuartzCreteWall);
        this.registerBlock("block_quartz_crete_bricks", ModRegistryBase.QuartzCreteBricks);
        this.registerBlock("block_quartz_crete_chiseled", ModRegistryBase.ChiseledQuartzCrete);
        this.registerBlock("block_quartz_crete_pillar", ModRegistryBase.QuartzCretePillar);
        this.registerBlock("block_quartz_crete_stairs", ModRegistryBase.QuartzCreteStairs);
        this.registerBlock("block_quartz_crete_slab", ModRegistryBase.QuartzCreteSlab);
        this.registerBlock("block_quartz_crete_smooth", ModRegistryBase.SmoothQuartzCrete);
        this.registerBlock("block_quartz_crete_smooth_wall", ModRegistryBase.SmoothQuartzCreteWall);
        this.registerBlock("block_quartz_crete_smooth_stairs", ModRegistryBase.SmoothQuartzCreteStairs);
        this.registerBlock("block_quartz_crete_smooth_slab", ModRegistryBase.SmoothQuartzCreteSlab);
    }

    private void registerItems() {
        this.registerItem("item_logo", ModRegistryBase.LogoItem);
        this.registerItem("item_pile_of_bricks", ModRegistryBase.ItemPileOfBricks);
        this.registerItem("item_pallet_of_bricks", ModRegistryBase.ItemPalletOfBricks);
        this.registerItem("item_bundle_of_timber", ModRegistryBase.ItemBundleOfTimber);
        this.registerItem("item_heap_of_timber", ModRegistryBase.ItemHeapOfTimber);
        this.registerItem("item_ton_of_timber", ModRegistryBase.ItemTonOfTimber);
        this.registerItem("item_string_of_lanterns", ModRegistryBase.StringOfLanterns);
        this.registerItem("item_coil_of_lanterns", ModRegistryBase.CoilOfLanterns);
        this.registerItem("item_compressed_chest", ModRegistryBase.CompressedChest);
        this.registerItem("item_upgrade", ModRegistryBase.Upgrade);

        this.registerItem("item_swift_blade_wood", ModRegistryBase.SwiftBladeWood);
        this.registerItem("item_swift_blade_stone", ModRegistryBase.SwiftBladeStone);
        this.registerItem("item_swift_blade_iron", ModRegistryBase.SwiftBladeIron);
        this.registerItem("item_swift_blade_diamond", ModRegistryBase.SwiftBladeDiamond);
        this.registerItem("item_swift_blade_gold", ModRegistryBase.SwiftBladeGold);
        this.registerItem("item_swift_blade_copper", ModRegistryBase.SwiftBladeCopper);
        this.registerItem("item_swift_blade_osmium", ModRegistryBase.SwiftBladeOsmium);
        this.registerItem("item_swift_blade_bronze", ModRegistryBase.SwiftBladeBronze);
        this.registerItem("item_swift_blade_steel", ModRegistryBase.SwiftBladeSteel);
        this.registerItem("item_swift_blade_obsidian", ModRegistryBase.SwiftBladeObsidian);
        this.registerItem("item_swift_blade_netherite", ModRegistryBase.SwiftBladeNetherite);

        this.registerItem("item_sickle_wood", ModRegistryBase.SickleWood);
        this.registerItem("item_sickle_stone", ModRegistryBase.SickleStone);
        this.registerItem("item_sickle_gold", ModRegistryBase.SickleGold);
        this.registerItem("item_sickle_iron", ModRegistryBase.SickleIron);
        this.registerItem("item_sickle_diamond", ModRegistryBase.SickleDiamond);
        this.registerItem("item_sickle_netherite", ModRegistryBase.SickleNetherite);

        this.registerItem("item_wooden_crate", ModRegistryBase.ItemEmptyCrate);
        this.registerItem("item_clutch_of_eggs", ModRegistryBase.ClutchOfEggs);
        this.registerItem("item_carton_of_eggs", ModRegistryBase.ItemCartonOfEggs);
        this.registerItem("item_bunch_of_potatoes", ModRegistryBase.BunchOfPotatoes);
        this.registerItem("item_crate_of_potatoes", ModRegistryBase.ItemCrateOfPotatoes);
        this.registerItem("item_bunch_of_carrots", ModRegistryBase.BunchOfCarrots);
        this.registerItem("item_crate_of_carrots", ModRegistryBase.ItemCrateOfCarrots);
        this.registerItem("item_bunch_of_beets", ModRegistryBase.BunchOfBeets);
        this.registerItem("item_crate_of_beets", ModRegistryBase.ItemCrateOfBeets);
    }

    private void registerBluePrints() {
        this.registerItem("item_house", ModRegistryBase.House);
        this.registerItem("item_instant_bridge", ModRegistryBase.InstantBridge);
        this.registerItem("item_house_improved", ModRegistryBase.HouseImproved);
        this.registerItem("item_house_advanced", ModRegistryBase.HouseAdvanced);
        this.registerItem("item_bulldozer", ModRegistryBase.Bulldozer);
        this.registerItem("item_creative_bulldozer", ModRegistryBase.CreativeBulldozer);

        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MachineryTower.getItemTextureLocation().getPath(), ModRegistryBase.MachineryTower);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.DefenseBunker.getItemTextureLocation().getPath(), ModRegistryBase.DefenseBunker);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MineshaftEntrance.getItemTextureLocation().getPath(), ModRegistryBase.MineshaftEntrance);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.EnderGateway.getItemTextureLocation().getPath(), ModRegistryBase.EnderGateway);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.AquaBase.getItemTextureLocation().getPath(), ModRegistryBase.AquaBase);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.GrassyPlain.getItemTextureLocation().getPath(), ModRegistryBase.GrassyPlain);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.MagicTemple.getItemTextureLocation().getPath(), ModRegistryBase.MagicTemple);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WatchTower.getItemTextureLocation().getPath(), ModRegistryBase.WatchTower);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WelcomeCenter.getItemTextureLocation().getPath(), ModRegistryBase.WelcomeCenter);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Jail.getItemTextureLocation().getPath(), ModRegistryBase.Jail);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Saloon.getItemTextureLocation().getPath(), ModRegistryBase.Saloon);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.SkiLodge.getItemTextureLocation().getPath(), ModRegistryBase.SkiLodge);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WindMill.getItemTextureLocation().getPath(), ModRegistryBase.WindMill);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.TownHall.getItemTextureLocation().getPath(), ModRegistryBase.TownHall);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.NetherGate.getItemTextureLocation().getPath(), ModRegistryBase.NetherGate);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.AquaBaseImproved.getItemTextureLocation().getPath(), ModRegistryBase.AquaBaseImproved);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Warehouse.getItemTextureLocation().getPath(), ModRegistryBase.Warehouse);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.WarehouseImproved.getItemTextureLocation().getPath(), ModRegistryBase.WareHouseImproved);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.VillagerHouses.getItemTextureLocation().getPath(), ModRegistryBase.VillagerHouses);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildings.getItemTextureLocation().getPath(), ModRegistryBase.ModernBuildings);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildingsImproved.getItemTextureLocation().getPath(), ModRegistryBase.ModernBuildingsImproved);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildingsAdvanced.getItemTextureLocation().getPath(), ModRegistryBase.ModernBuildingsAdvanced);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.Farm.getItemTextureLocation().getPath(), ModRegistryBase.Farm);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.FarmImproved.getItemTextureLocation().getPath(), ModRegistryBase.FarmImproved);
        this.registerItem(BasicStructureConfiguration.EnumBasicStructureName.FarmAdvanced.getItemTextureLocation().getPath(), ModRegistryBase.FarmAdvanced);
    }

    private void registerItemBlocks() {
        this.registerItem(BlockCompressedStone.EnumType.COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.CompressedStoneItem);
        this.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedStoneItem);
        this.registerItem(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE.getUnlocalizedName(), ModRegistryBase.TripleCompressedStoneItem);
        this.registerItem(BlockCompressedStone.EnumType.COMPRESSED_DIRT.getUnlocalizedName(), ModRegistryBase.CompressedDirtItem);
        this.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT.getUnlocalizedName(), ModRegistryBase.DoubleCompressedDirtItem);
        this.registerItem(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistryBase.CompressedGlowstoneItem);
        this.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedGlowstoneItem);
        this.registerItem(BlockCompressedStone.EnumType.COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistryBase.CompressedQuartzCreteItem);
        this.registerItem(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_QUARTZCRETE.getUnlocalizedName(), ModRegistryBase.DoubleCompressedQuartzCreteItem);
        this.registerItem(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN.toString(), ModRegistryBase.CompressedObsidianItem);
        this.registerItem(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN.toString(), ModRegistryBase.DoubleCompressedObsidianItem);
        this.registerItem("block_glass_slab", ModRegistryBase.GlassSlabItem);
        this.registerItem("block_glass_stairs", ModRegistryBase.GlassStairsItem);
        this.registerItem("block_paper_lantern", ModRegistryBase.PaperLanternItem);
        this.registerItem("block_phasic", ModRegistryBase.PhasicItem);
        this.registerItem("block_boundary", ModRegistryBase.BoundaryItem);
        this.registerItem("block_grass_slab", ModRegistryBase.GrassSlabItem);
        this.registerItem("block_grass_stairs", ModRegistryBase.GrassStairsItem);
        this.registerItem(BlockCustomWall.EnumType.GRASS.getUnlocalizedName(), ModRegistryBase.GrassWallItem);
        this.registerItem(BlockCustomWall.EnumType.DIRT.getUnlocalizedName(), ModRegistryBase.DirtWallItem);
        this.registerItem("block_dirt_stairs", ModRegistryBase.DirtStairsItem);
        this.registerItem("block_dirt_slab", ModRegistryBase.DirtSlabItem);

        if (PrefabBase.isDebug) {
            ModRegistryBase.StructureScannerItem = new BlockItem(ModRegistryBase.StructureScanner, new Item.Properties());
            this.registerItem("block_structure_scanner", ModRegistryBase.StructureScannerItem);
        }

        this.registerItem("block_light_switch", ModRegistryBase.LightSwitchItem);
        this.registerItem("block_dark_lamp", ModRegistryBase.DarkLampItem);

        this.registerItem("block_quartz_crete", ModRegistryBase.QuartzCreteItem);
        this.registerItem("block_quartz_crete_wall", ModRegistryBase.QuartzCreteWallItem);
        this.registerItem("block_quartz_crete_bricks", ModRegistryBase.QuartzCreteBricksItem);
        this.registerItem("block_quartz_crete_chiseled", ModRegistryBase.ChiseledQuartzCreteItem);
        this.registerItem("block_quartz_crete_pillar", ModRegistryBase.QuartzCretePillarItem);
        this.registerItem("block_quartz_crete_stairs", ModRegistryBase.QuartzCreteStairsItem);
        this.registerItem("block_quartz_crete_slab", ModRegistryBase.QuartzCreteSlabItem);
        this.registerItem("block_quartz_crete_smooth", ModRegistryBase.SmoothQuartzCreteItem);
        this.registerItem("block_quartz_crete_smooth_wall", ModRegistryBase.SmoothQuartzCreteWallItem);
        this.registerItem("block_quartz_crete_smooth_stairs", ModRegistryBase.SmoothQuartzCreteStairsItem);
        this.registerItem("block_quartz_crete_smooth_slab", ModRegistryBase.SmoothQuartzCreteSlabItem);
    }

    /**
     * This is where the mod messages are registered.
     */
    private void RegisterClientToServerMessageHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(StructurePayload.PACKET_TYPE, ServerPayloadHandler::structureBuilderHandler);
        ServerPlayNetworking.registerGlobalReceiver(ScannerConfigPayload.PACKET_TYPE, ServerPayloadHandler::scannerConfigHandler);
        ServerPlayNetworking.registerGlobalReceiver(ScanShapePayload.PACKET_TYPE, ServerPayloadHandler::scannerScanHandler);
    }

    private void RegisterRecipeSerializers() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.tryBuild(PrefabBase.MODID, "condition_crafting_shaped"), ModRegistryBase.ConditionedShapedRecipeSeriaizer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.tryBuild(PrefabBase.MODID, "condition_crafting_shapeless"), ModRegistryBase.ConditionedShapelessRecipeSeriaizer);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.tryBuild(PrefabBase.MODID, "condition_smelting"), ModRegistryBase.ConditionedSmeltingRecipeSeriaizer);
    }

    private void registerBlock(String registryName, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.tryBuild(PrefabBase.MODID, registryName), block);
    }

    private void registerItem(String registryName, Item item) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.tryBuild(PrefabBase.MODID, registryName), item);
        ModRegistry.ModItems.add(item);
    }

    private void registerPayloadTypes() {
        PayloadTypeRegistry.playC2S().register(StructurePayload.PACKET_TYPE, StructurePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ScannerConfigPayload.PACKET_TYPE, ScannerConfigPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ScanShapePayload.PACKET_TYPE, ScanShapePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ConfigSyncPayload.PACKET_TYPE, ConfigSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerConfigPayload.PACKET_TYPE, PlayerConfigPayload.STREAM_CODEC);
    }
}