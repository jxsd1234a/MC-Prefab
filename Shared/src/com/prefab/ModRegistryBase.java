package com.prefab;

import com.prefab.blocks.*;
import com.prefab.blocks.BlockBoundary;
import com.prefab.blocks.BlockGlassSlab;
import com.prefab.blocks.BlockGlassStairs;
import com.prefab.blocks.BlockPaperLantern;
import com.prefab.items.ItemCompressedChest;
import com.prefab.items.ItemSwiftBlade;
import com.prefab.registries.ModRegistries;
import com.prefab.structures.items.*;
import com.prefab.blocks.entities.LightSwitchBlockEntity;
import com.prefab.blocks.entities.StructureScannerBlockEntity;
import com.prefab.items.ItemBlockWoodenCrate;
import com.prefab.items.ItemSickle;
import com.prefab.items.ItemWoodenCrate;
import com.prefab.recipe.ConditionedShapedRecipe;
import com.prefab.recipe.ConditionedShapelessRecipe;
import com.prefab.recipe.ConditionedSmeltingRecipe;
import com.prefab.structures.config.BasicStructureConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.item.Tiers.WOOD;

public class ModRegistryBase {
    public static ModRegistries serverModRegistries;
    public static final ArrayList<Consumer<Object>> guiRegistrations = new ArrayList<>();

    /* *********************************** Blocks *********************************** */
    public static BlockCompressedStone CompressedStone = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_STONE);
    public static BlockCompressedStone DoubleCompressedStone = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_STONE);
    public static BlockCompressedStone TripleCompressedStone = new BlockCompressedStone(BlockCompressedStone.EnumType.TRIPLE_COMPRESSED_STONE);
    public static BlockCompressedStone CompressedDirt = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_DIRT);
    public static BlockCompressedStone DoubleCompressedDirt = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_DIRT);
    public static BlockCompressedStone CompressedGlowstone = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_GLOWSTONE);
    public static BlockCompressedStone DoubleCompressedGlowstone = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_GLOWSTONE);
    public static BlockCompressedStone CompressedQuartzCrete = new BlockCompressedStone(BlockCompressedStone.EnumType.COMPRESSED_QUARTZCRETE);
    public static BlockCompressedStone DoubleCompressedQuartzCrete = new BlockCompressedStone(BlockCompressedStone.EnumType.DOUBLE_COMPRESSED_QUARTZCRETE);

    public static BlockCompressedObsidian CompressedObsidian = new BlockCompressedObsidian(BlockCompressedObsidian.EnumType.COMPRESSED_OBSIDIAN);
    public static BlockCompressedObsidian DoubleCompressedObsidian = new BlockCompressedObsidian(BlockCompressedObsidian.EnumType.DOUBLE_COMPRESSED_OBSIDIAN);
    public static BlockGlassSlab GlassSlab;
    public static BlockGlassStairs GlassStairs;
    public static BlockPaperLantern PaperLantern;
    public static BlockPhasic Phasic;
    public static BlockBoundary Boundary;
    public static BlockGrassSlab GrassSlab = new BlockGrassSlab();
    public static BlockGrassStairs GrassStairs = new BlockGrassStairs();
    public static BlockCustomWall GrassWall = new BlockCustomWall(Blocks.GRASS_BLOCK, BlockCustomWall.EnumType.GRASS);
    public static BlockCustomWall DirtWall = new BlockCustomWall(Blocks.DIRT, BlockCustomWall.EnumType.DIRT);
    public static BlockDirtStairs DirtStairs = new BlockDirtStairs();
    public static BlockDirtSlab DirtSlab = new BlockDirtSlab();
    public static BlockStructureScanner StructureScanner = null;
    public static BlockLightSwitch LightSwitch = new BlockLightSwitch();
    public static BlockDarkLamp DarkLamp = new BlockDarkLamp();
    public static BlockRotatableHorizontalShaped PileOfBricks = new BlockRotatableHorizontalShaped(BlockShaped.BlockShape.PileOfBricks, Block.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.COLOR_RED).noOcclusion().isViewBlocking(ModRegistryBase::never));
    public static BlockRotatableHorizontalShaped PalletOfBricks = new BlockRotatableHorizontalShaped(BlockShaped.BlockShape.PalletOfBricks, Block.Properties.ofFullCopy(Blocks.BRICKS).mapColor(MapColor.COLOR_RED).noOcclusion().isViewBlocking(ModRegistryBase::never));
    public static BlockRotatableHorizontalShaped BundleOfTimber = new BlockRotatableHorizontalShaped(BlockShaped.BlockShape.BundleOfTimber, Block.Properties.ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noOcclusion().isViewBlocking(ModRegistryBase::never));
    public static BlockRotatableHorizontalShaped HeapOfTimber = new BlockRotatableHorizontalShaped(BlockShaped.BlockShape.HeapOfTimber, Block.Properties.ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noOcclusion().isViewBlocking(ModRegistryBase::never));
    public static BlockRotatableHorizontalShaped TonOfTimber = new BlockRotatableHorizontalShaped(BlockShaped.BlockShape.TonOfTimber, Block.Properties.ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_BROWN).sound(SoundType.WOOD).noOcclusion().isViewBlocking(ModRegistryBase::never));
    public static BlockRotatable EmptyCrate = new BlockRotatable(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.WOOD));
    public static BlockRotatable CartonOfEggs = new BlockRotatable(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.WOOD));
    public static BlockRotatable CrateOfPotatoes = new BlockRotatable(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.WOOD));
    public static BlockRotatable CrateOfCarrots = new BlockRotatable(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.WOOD));
    public static BlockRotatable CrateOfBeets = new BlockRotatable(Block.Properties.ofFullCopy(Blocks.OAK_WOOD).sound(SoundType.WOOD));
    public static Block QuartzCrete = new Block(Block.Properties.ofFullCopy(Blocks.QUARTZ_BLOCK));
    public static WallBlock QuartzCreteWall = new WallBlock(Block.Properties.ofFullCopy(ModRegistryBase.QuartzCrete));
    public static Block QuartzCreteBricks = new Block(Block.Properties.ofFullCopy(ModRegistryBase.QuartzCrete));
    public static Block ChiseledQuartzCrete = new Block(Block.Properties.ofFullCopy(Blocks.CHISELED_QUARTZ_BLOCK));
    public static RotatedPillarBlock QuartzCretePillar = new RotatedPillarBlock(Block.Properties.ofFullCopy(Blocks.QUARTZ_PILLAR));
    public static BlockCustomStairs QuartzCreteStairs = new BlockCustomStairs(ModRegistryBase.QuartzCrete.defaultBlockState(), Block.Properties.ofFullCopy(ModRegistryBase.QuartzCrete));
    public static SlabBlock QuartzCreteSlab = new SlabBlock(Block.Properties.ofFullCopy(ModRegistryBase.QuartzCrete));
    public static Block SmoothQuartzCrete = new Block(Block.Properties.ofFullCopy(ModRegistryBase.QuartzCrete));
    public static WallBlock SmoothQuartzCreteWall = new WallBlock(Block.Properties.ofFullCopy(ModRegistryBase.SmoothQuartzCrete));
    public static BlockCustomStairs SmoothQuartzCreteStairs = new BlockCustomStairs(ModRegistryBase.SmoothQuartzCrete.defaultBlockState(), Block.Properties.ofFullCopy(ModRegistryBase.SmoothQuartzCrete));
    public static SlabBlock SmoothQuartzCreteSlab = new SlabBlock(Block.Properties.ofFullCopy(SmoothQuartzCrete));

    /* *********************************** Messages *********************************** */
    public static Item LogoItem = new Item(new Item.Properties());

    /* *********************************** Item Blocks *********************************** */
    public static BlockItem CompressedStoneItem = new BlockItem(ModRegistryBase.CompressedStone, new Item.Properties());
    public static BlockItem DoubleCompressedStoneItem = new BlockItem(ModRegistryBase.DoubleCompressedStone, new Item.Properties());
    public static BlockItem TripleCompressedStoneItem = new BlockItem(ModRegistryBase.TripleCompressedStone, new Item.Properties());
    public static BlockItem CompressedDirtItem = new BlockItem(ModRegistryBase.CompressedDirt, new Item.Properties());
    public static BlockItem DoubleCompressedDirtItem = new BlockItem(ModRegistryBase.DoubleCompressedDirt, new Item.Properties());
    public static BlockItem CompressedGlowstoneItem = new BlockItem(ModRegistryBase.CompressedGlowstone, new Item.Properties());
    public static BlockItem DoubleCompressedGlowstoneItem = new BlockItem(ModRegistryBase.DoubleCompressedGlowstone, new Item.Properties());
    public static BlockItem CompressedQuartzCreteItem = new BlockItem(ModRegistryBase.CompressedQuartzCrete, new Item.Properties());
    public static BlockItem DoubleCompressedQuartzCreteItem = new BlockItem(ModRegistryBase.DoubleCompressedQuartzCrete, new Item.Properties());

    public static BlockItem CompressedObsidianItem = new BlockItem(ModRegistryBase.CompressedObsidian, new Item.Properties());
    public static BlockItem DoubleCompressedObsidianItem = new BlockItem(ModRegistryBase.DoubleCompressedObsidian, new Item.Properties());
    public static BlockItem GlassSlabItem;
    public static BlockItem GlassStairsItem;
    public static BlockItem PaperLanternItem;
    public static BlockItem PhasicItem;
    public static BlockItem BoundaryItem;
    public static BlockItem GrassSlabItem = new BlockItem(ModRegistryBase.GrassSlab, new Item.Properties());
    public static BlockItem GrassStairsItem = new BlockItem(ModRegistryBase.GrassStairs, new Item.Properties());
    public static BlockItem GrassWallItem = new BlockItem(ModRegistryBase.GrassWall, new Item.Properties());
    public static BlockItem DirtWallItem = new BlockItem(ModRegistryBase.DirtWall, new Item.Properties());
    public static BlockItem DirtStairsItem = new BlockItem(ModRegistryBase.DirtStairs, new Item.Properties());
    public static BlockItem DirtSlabItem = new BlockItem(ModRegistryBase.DirtSlab, new Item.Properties());
    public static BlockItem StructureScannerItem = null;
    public static BlockItem LightSwitchItem = new BlockItem(ModRegistryBase.LightSwitch, new Item.Properties());
    public static BlockItem DarkLampItem = new BlockItem(ModRegistryBase.DarkLamp, new Item.Properties());
    public static BlockItem QuartzCreteItem = new BlockItem(ModRegistryBase.QuartzCrete, new Item.Properties());
    public static BlockItem QuartzCreteWallItem = new BlockItem(ModRegistryBase.QuartzCreteWall, new Item.Properties());
    public static BlockItem QuartzCreteBricksItem = new BlockItem(ModRegistryBase.QuartzCreteBricks, new Item.Properties());
    public static BlockItem ChiseledQuartzCreteItem = new BlockItem(ModRegistryBase.ChiseledQuartzCrete, new Item.Properties());
    public static BlockItem QuartzCretePillarItem = new BlockItem(ModRegistryBase.QuartzCretePillar, new Item.Properties());
    public static BlockItem QuartzCreteStairsItem = new BlockItem(ModRegistryBase.QuartzCreteStairs, new Item.Properties());
    public static BlockItem QuartzCreteSlabItem = new BlockItem(ModRegistryBase.QuartzCreteSlab, new Item.Properties());
    public static BlockItem SmoothQuartzCreteItem = new BlockItem(ModRegistryBase.SmoothQuartzCrete, new Item.Properties());
    public static BlockItem SmoothQuartzCreteWallItem = new BlockItem(ModRegistryBase.SmoothQuartzCreteWall, new Item.Properties());
    public static BlockItem SmoothQuartzCreteStairsItem = new BlockItem(ModRegistryBase.SmoothQuartzCreteStairs, new Item.Properties());
    public static BlockItem SmoothQuartzCreteSlabItem = new BlockItem(ModRegistryBase.SmoothQuartzCreteSlab, new Item.Properties());

    /* *********************************** Items *********************************** */
    public static ItemCompressedChest CompressedChest;
    public static Item ItemPileOfBricks = new BlockItem(ModRegistryBase.PileOfBricks, new Item.Properties());
    public static Item ItemPalletOfBricks = new BlockItem(ModRegistryBase.PalletOfBricks, new Item.Properties());
    public static Item ItemBundleOfTimber = new BlockItem(ModRegistryBase.BundleOfTimber, new Item.Properties());
    public static Item ItemHeapOfTimber = new BlockItem(ModRegistryBase.HeapOfTimber, new Item.Properties());
    public static Item ItemTonOfTimber = new BlockItem(ModRegistryBase.TonOfTimber, new Item.Properties());
    public static Item StringOfLanterns = new Item(new Item.Properties());
    public static Item CoilOfLanterns = new Item(new Item.Properties());
    public static Item Upgrade = new Item(new Item.Properties());
    public static Item SwiftBladeWood = new ItemSwiftBlade(WOOD, 2, .5f);
    public static Item SwiftBladeStone = new ItemSwiftBlade(Tiers.STONE, 2, .5f);
    public static Item SwiftBladeIron = new ItemSwiftBlade(Tiers.IRON, 2, .5f);
    public static Item SwiftBladeDiamond = new ItemSwiftBlade(Tiers.DIAMOND, 2, .5f);
    public static Item SwiftBladeGold = new ItemSwiftBlade(Tiers.GOLD, 2, .5f);
    public static Item SwiftBladeCopper = new ItemSwiftBlade(ModRegistryBase.CustomItemTier.COPPER, 2, .5f);
    public static Item SwiftBladeOsmium = new ItemSwiftBlade(ModRegistryBase.CustomItemTier.OSMIUM, 2, .5f);
    public static Item SwiftBladeBronze = new ItemSwiftBlade(ModRegistryBase.CustomItemTier.BRONZE, 2, .5f);
    public static Item SwiftBladeSteel = new ItemSwiftBlade(ModRegistryBase.CustomItemTier.STEEL, 2, .5f);
    public static Item SwiftBladeObsidian = new ItemSwiftBlade(ModRegistryBase.CustomItemTier.OBSIDIAN, 2, .5f);
    public static Item SwiftBladeNetherite = new ItemSwiftBlade(Tiers.NETHERITE, 2, .5f);

    // These will be overridden in mod-loader files.
    public static ItemSickle SickleWood;
    public static ItemSickle SickleStone;
    public static ItemSickle SickleGold;
    public static ItemSickle SickleIron;
    public static ItemSickle SickleDiamond;
    public static ItemSickle SickleNetherite;

    // Note: Empty crate must be created registered first to avoid null-pointer errors with the rest of the ItemWoodenCrate items.
    public static ItemBlockWoodenCrate ItemEmptyCrate = new ItemBlockWoodenCrate(ModRegistryBase.EmptyCrate, ItemWoodenCrate.CrateType.Empty);
    public static ItemWoodenCrate ClutchOfEggs = new ItemWoodenCrate(ItemWoodenCrate.CrateType.Clutch_Of_Eggs);
    public static ItemBlockWoodenCrate ItemCartonOfEggs = new ItemBlockWoodenCrate(ModRegistryBase.CartonOfEggs, ItemWoodenCrate.CrateType.Carton_Of_Eggs);
    public static ItemWoodenCrate BunchOfPotatoes = new ItemWoodenCrate(ItemWoodenCrate.CrateType.Bunch_Of_Potatoes);
    public static ItemBlockWoodenCrate ItemCrateOfPotatoes = new ItemBlockWoodenCrate(ModRegistryBase.CrateOfPotatoes, ItemWoodenCrate.CrateType.Crate_Of_Potatoes);
    public static ItemWoodenCrate BunchOfCarrots = new ItemWoodenCrate(ItemWoodenCrate.CrateType.Bunch_Of_Carrots);
    public static ItemBlockWoodenCrate ItemCrateOfCarrots = new ItemBlockWoodenCrate(ModRegistryBase.CrateOfCarrots, ItemWoodenCrate.CrateType.Crate_Of_Carrots);
    public static ItemWoodenCrate BunchOfBeets = new ItemWoodenCrate(ItemWoodenCrate.CrateType.Bunch_Of_Beets);
    public static ItemBlockWoodenCrate ItemCrateOfBeets = new ItemBlockWoodenCrate(ModRegistryBase.CrateOfBeets, ItemWoodenCrate.CrateType.Crate_Of_Beets);

    /* *********************************** Blueprint Items *********************************** */
    public static ItemInstantBridge InstantBridge = new ItemInstantBridge();
    public static ItemHouse House = new ItemHouse();
    public static ItemHouseImproved HouseImproved = new ItemHouseImproved();
    public static ItemHouseAdvanced HouseAdvanced = new ItemHouseAdvanced();
    public static ItemBulldozer Bulldozer;
    public static ItemBulldozer CreativeBulldozer;
    public static ItemBasicStructure MachineryTower = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.MachineryTower);
    public static ItemBasicStructure DefenseBunker = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.DefenseBunker);
    public static ItemBasicStructure MineshaftEntrance = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.MineshaftEntrance);
    public static ItemBasicStructure EnderGateway = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.EnderGateway);
    public static ItemBasicStructure AquaBase = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.AquaBase);
    public static ItemBasicStructure GrassyPlain = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.GrassyPlain);
    public static ItemBasicStructure MagicTemple = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.MagicTemple);
    public static ItemBasicStructure WatchTower = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WatchTower);
    public static ItemBasicStructure WelcomeCenter = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WelcomeCenter);
    public static ItemBasicStructure Jail = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Jail);
    public static ItemBasicStructure Saloon = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Saloon);
    public static ItemBasicStructure SkiLodge = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.SkiLodge);
    public static ItemBasicStructure WindMill = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WindMill);
    public static ItemBasicStructure TownHall = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.TownHall);
    public static ItemBasicStructure NetherGate = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.NetherGate);
    public static ItemBasicStructure AquaBaseImproved = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.AquaBaseImproved);
    public static ItemBasicStructure Warehouse = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Warehouse);
    public static ItemBasicStructure WareHouseImproved = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.WarehouseImproved);
    public static ItemBasicStructure VillagerHouses = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.VillagerHouses, 10);
    public static ItemBasicStructure ModernBuildings = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildings);
    public static ItemBasicStructure ModernBuildingsImproved = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildingsImproved);
    public static ItemBasicStructure ModernBuildingsAdvanced = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.ModernBuildingsAdvanced);
    public static ItemBasicStructure Farm = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.Farm);
    public static ItemBasicStructure FarmImproved = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.FarmImproved);
    public static ItemBasicStructure FarmAdvanced = new ItemBasicStructure(BasicStructureConfiguration.EnumBasicStructureName.FarmAdvanced);

    /* *********************************** Recipe Serializers *********************************** */
    public static RecipeSerializer<ConditionedShapedRecipe> ConditionedShapedRecipeSeriaizer = new ConditionedShapedRecipe.Serializer();
    public static RecipeSerializer<ConditionedShapelessRecipe> ConditionedShapelessRecipeSeriaizer = new ConditionedShapelessRecipe.Serializer();
    public static RecipeSerializer<ConditionedSmeltingRecipe> ConditionedSmeltingRecipeSeriaizer = new ConditionedSmeltingRecipe.Serializer();

    /* *********************************** Sounds *********************************** */
    public static SoundEvent BuildingBlueprint = SoundEvent.createVariableRangeEvent(ResourceLocation.tryBuild(PrefabBase.MODID, "building_blueprint"));

    /* *********************************** Block Entities Types *********************************** */
    public static BlockEntityType<StructureScannerBlockEntity> StructureScannerEntityType;
    public static BlockEntityType<LightSwitchBlockEntity> LightSwitchEntityType;

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
