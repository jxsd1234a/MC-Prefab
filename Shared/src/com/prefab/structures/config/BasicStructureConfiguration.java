package com.prefab.structures.config;

import com.prefab.ModRegistryBase;
import com.prefab.PrefabBase;
import com.prefab.blocks.FullDyeColor;
import com.prefab.structures.config.enums.*;
import com.prefab.structures.predefined.StructureBasic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

/**
 * This class is used for the basic structures in the mod.
 *
 * @author WuestMan
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class BasicStructureConfiguration extends StructureConfiguration {
    private static final String structureEnumNameTag = "structureEnumName";
    private static final String structureDisplayNameTag = "structureDisplayName";
    private static final String bedColorTag = "bedColor";
    private static final String glassColorTag = "glassColor";
    private static final String chosenOptionTag = "chosenOption";

    /**
     * This field is used to contain the {@link EnumBasicStructureName} used by this instance.
     */
    public EnumBasicStructureName basicStructureName;

    /**
     * This field is used to contain the display name for the structure.
     */
    public String structureDisplayName;

    public DyeColor bedColor;

    public FullDyeColor glassColor;

    public BaseOption chosenOption;

    /**
     * Initializes a new instance of the BasicStructureConfiguration class.
     */
    public BasicStructureConfiguration() {
        super();
    }

    /**
     * Gets the display name for this structure.
     *
     * @return The unlocalized display name for this structure
     */
    public String getDisplayName() {
        if (this.basicStructureName == EnumBasicStructureName.Custom) {
            return this.structureDisplayName;
        } else {
            return this.basicStructureName.getItemTranslationString();
        }
    }

    /**
     * Determines if this is a custom structure.
     *
     * @return A value indicating whether this is a custom structure.
     */
    public boolean IsCustomStructure() {
        return this.basicStructureName == EnumBasicStructureName.Custom;
    }

    @Override
    public void Initialize() {
        super.Initialize();
        this.houseFacing = Direction.NORTH;
        this.basicStructureName = EnumBasicStructureName.AquaBase;
        this.bedColor = DyeColor.RED;
        this.glassColor = FullDyeColor.CLEAR;
        this.chosenOption = this.basicStructureName.baseOption.getSpecificOptions().get(0);
    }

    @Override
    protected void CustomReadFromNBTTag(CompoundTag messageTag, StructureConfiguration config) {
        BasicStructureConfiguration basicConfig = (BasicStructureConfiguration) config;

        if (messageTag.contains(BasicStructureConfiguration.structureEnumNameTag)) {
            basicConfig.basicStructureName = EnumBasicStructureName.valueOf(messageTag.getString(BasicStructureConfiguration.structureEnumNameTag));
        }

        if (messageTag.contains(BasicStructureConfiguration.structureDisplayNameTag)) {
            basicConfig.structureDisplayName = messageTag.getString(BasicStructureConfiguration.structureDisplayNameTag);
        }

        if (messageTag.contains(BasicStructureConfiguration.bedColorTag)) {
            basicConfig.bedColor = DyeColor.byId(messageTag.getInt(BasicStructureConfiguration.bedColorTag));
        }

        if (messageTag.contains(BasicStructureConfiguration.glassColorTag)) {
            basicConfig.glassColor = FullDyeColor.byId(messageTag.getInt(BasicStructureConfiguration.glassColorTag));
        }

        if (messageTag.contains(BasicStructureConfiguration.chosenOptionTag)) {
            basicConfig.chosenOption = BaseOption.getOptionByHash(messageTag.getInt(BasicStructureConfiguration.chosenOptionTag));
        }
    }

    @Override
    protected CompoundTag CustomWriteToCompoundTag(CompoundTag tag) {
        tag.putString(BasicStructureConfiguration.structureEnumNameTag, this.basicStructureName.name());

        if (this.structureDisplayName != null) {
            tag.putString(BasicStructureConfiguration.structureDisplayNameTag, this.structureDisplayName);
        }

        tag.putInt(BasicStructureConfiguration.bedColorTag, this.bedColor.getId());
        tag.putInt(BasicStructureConfiguration.glassColorTag, this.glassColor.getId());
        tag.putInt(BasicStructureConfiguration.chosenOptionTag, this.chosenOption.hashCode());

        return tag;
    }

    /**
     * Reads information from an NBTTagCompound.
     *
     * @param messageTag The tag to read the data from.
     * @return An instance of {@link BasicStructureConfiguration} with vaules pulled from the NBTTagCompound.
     */
    @Override
    public BasicStructureConfiguration ReadFromCompoundTag(CompoundTag messageTag) {
        BasicStructureConfiguration config = new BasicStructureConfiguration();

        return (BasicStructureConfiguration) super.ReadFromCompoundTag(messageTag, config);
    }

    /**
     * This is used to actually build the structure as it creates the structure instance and calls build structure.
     *
     * @param player      The player which requested the build.
     * @param world       The world instance where the build will occur.
     * @param hitBlockPos This hit block position.
     */
    @Override
    protected void ConfigurationSpecificBuildStructure(Player player, ServerLevel world, BlockPos hitBlockPos) {
        String assetLocation = "";

        if (!this.IsCustomStructure()) {
            assetLocation = this.chosenOption.getAssetLocation();
        }

        StructureBasic structure = StructureBasic.CreateInstance(assetLocation, StructureBasic.class);

        if (structure.BuildStructure(this, world, hitBlockPos, player)) {
            ItemStack stack = BasicStructureConfiguration.getBasicStructureItemInHand(player);

            if (!stack.isDamageableItem()) {
                if (stack.getCount() == 1) {
                    player.getInventory().removeItem(stack);
                } else {
                    stack.setCount(stack.getCount() - 1);
                }

                player.containerMenu.broadcastChanges();
            } else {
                // The item has durability; damage it since the building was constructed.
                this.DamageHeldItem(player, stack.getItem());
            }
        }
    }

    public static ItemStack getBasicStructureItemInHand(Player player) {
        ItemStack stack = player.getOffhandItem();

        // Get off-hand first since that is the right-click hand if there is
        // something in there.
        if (!(stack.getItem().getClass() == ModRegistryBase.MachineryTower.getClass())) {
            if (player.getMainHandItem().getItem().getClass() == ModRegistryBase.MachineryTower.getClass()) {
                stack = player.getMainHandItem();
            } else {
                stack = null;
            }
        }

        return stack;
    }

    /**
     * This enum is used to list the names of the basic structures and provide other information necessary.
     *
     * @author WuestMan
     */
    @SuppressWarnings("SpellCheckingInspection")
    public enum EnumBasicStructureName {
        Custom("custom", null, null, null, false),
        MachineryTower("machinery_tower", "item.prefab.item_machinery_tower", "item_machinery_tower", MachineryTowerOptions.Default, true),
        DefenseBunker("defense_bunker", "item.prefab.item_defense_bunker", "item_defense_bunker", DefenseBunkerOptions.Default, false),
        MineshaftEntrance("mineshaft_entrance", "item.prefab.item_mineshaft_entrance", "item_mineshaft_entrance", MineshaftEntranceOptions.Default, true),
        EnderGateway("ender_gateway", "item.prefab.item_ender_gateway", "item_ender_gateway", EnderGatewayOptions.Default, false),
        AquaBase("aqua_base", "item.prefab.item_aqua_base", "item_aqua_base", AquaBaseOptions.Default, false),
        GrassyPlain("grassy_plain", "item.prefab.item_grassy_plain", "item_grassy_plain", GrassyPlainOptions.Default, false),
        MagicTemple("magic_temple", "item.prefab.item_magic_temple", "item_magic_temple", MagicTempleOptions.Default, false),
        WatchTower("watch_tower", "item.prefab.item_watch_tower", "item_watch_tower", WatchTowerOptions.Default, true),
        WelcomeCenter("welcome_center", "item.prefab.item_welcome_center", "item_welcome_center", WelcomeCenterOptions.Default, true),
        Jail("jail", "item.prefab.item_jail", "item_jail", JailOptions.Default, false),
        Saloon("saloon", "item.prefab.item_saloon", "item_saloon", SaloonOptions.Default, false),
        SkiLodge("ski_lodge", "item.prefab.item_ski_lodge", "item_ski_lodge", SkiLodgeOptions.Default, false),
        WindMill("wind_mill", "item.prefab.item_wind_mill", "item_wind_mill", WindMillOptions.Default, false),
        TownHall("town_hall", "item.prefab.item_town_hall", "item_town_hall", TownHallOptions.Default, false),
        NetherGate("nether_gate", "item.prefab.item_nether_gate", "item_nether_gate", NetherGateOptions.AncientSkull, false),
        AquaBaseImproved("aqua_base_improved", "item.prefab.item_aqua_base_improved", "item_aqua_base_improved", AquaBaseImprovedOptions.Default, false),
        VillagerHouses("villager_houses", "item.prefab.item_villager_houses", "item_villager_houses", VillagerHouseOptions.FLAT_ROOF, true),
        WarehouseImproved("warehouse_improved", "item.prefab.item_warehouse_improved", "item_warehouse_improved", WarehouseImprovedOptions.Default, true),
        Warehouse("warehouse", "item.prefab.item_warehouse", "item_warehouse", WarehouseOptions.Default, true),
        ModernBuildings("modern_buildings", "item.prefab.item_modern_buildings", "item_modern_buildings", ModernBuildingsOptions.HipsterFruitStand, true),
        Farm("farm", "item.prefab.item_farm", "item_farm", FarmOptions.ElevatedFarm, true),
        FarmImproved("farm_improved", "item.prefab.item_farm_improved", "item_farm_improved", FarmImprovedOptions.AutomatedFarm, true),
        FarmAdvanced("farm_advanced", "item.prefab.item_farm_advanced", "item_farm_advanced", FarmAdvancedOptions.AutomatedBambooFarm, true),
        ModernBuildingsImproved("modern_buildings_improved", "item.prefab.item_modern_buildings_improved", "item_modern_buildings_improved", ModernBuildingsImprovedOptions.ConstructionSite, true),
        ModernBuildingsAdvanced("modern_buildings_advanced", "item.prefab.item_modern_buildings_advanced", "item_modern_buildings_advanced", ModernBuildingsAdvancedOptions.TreeHouse, true);

        private final String name;
        private final String itemTranslationString;
        private final BaseOption baseOption;
        private ResourceLocation itemTextureLocation;
        private final boolean guiShowConfigurationOptions;

        /**
         * This is a basic structure which doesn't have any (or limited) custom processing.
         *
         * @param name                  - This is the name for this structure. This is used for comparative purposes in
         *                              item stacks.
         * @param itemTranslationString - This is the localization key to determine the displayed name to the user.
         * @param itemTextureLocation   - This is the resource location for the item's texture when it's in the players
         *                              and or in inventories/the world.
         */
        EnumBasicStructureName(
                String name,
                String itemTranslationString,
                String itemTextureLocation,
                BaseOption baseOption,
                boolean guiShowConfigurationOptions) {
            this.name = name;
            this.itemTranslationString = itemTranslationString;

            if (itemTextureLocation != null) {
                this.itemTextureLocation = ResourceLocation.tryBuild(PrefabBase.MODID, itemTextureLocation);
            }

            this.baseOption = baseOption;
            this.guiShowConfigurationOptions = guiShowConfigurationOptions;
        }

        /**
         * The enum name.
         *
         * @return The enum name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * Should show configuration options in the GUI.
         *
         * @return Should show configuration options in the GUI.
         * */
        public boolean shouldShowConfigurationOptions() {
            return this.guiShowConfigurationOptions;
        }

        /**
         * The unlocalized name.
         *
         * @return The unlocalized name for this structure.
         */
        public String getItemTranslationString() {
            return this.itemTranslationString;
        }

        /**
         * This is the resource location for the item's texture when it's in the players and or in inventories/the
         * world.
         *
         * @return The resource location for the item texture.
         */
        public ResourceLocation getItemTextureLocation() {
            return this.itemTextureLocation;
        }

        public BaseOption getBaseOption() {
            return this.baseOption;
        }
    }
}
