package com.prefab.structures.config;

import com.prefab.ModRegistryBase;
import com.prefab.PrefabBase;
import com.prefab.gui.GuiLangKeys;
import com.prefab.structures.predefined.StructureHouseImproved;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;

/**
 * This class is used for the moderate houses in the mod.
 *
 * @author WuestMan
 */
public class HouseImprovedConfiguration extends StructureConfiguration {
    public static String tagKey = "houseConfig";
    private static String houseStyleTag = "houseStyle";
    private static String addChestTag = "addChests";
    private static String addChestContentsTag = "addChestContents";
    private static String addMineshaftTag = "addMineshaft";
    private static String bedColorTag = "bedColor";

    /**
     * The house style.
     */
    public HouseStyle houseStyle;

    /**
     * Determines if the chests are included in the structure generation.
     */
    public boolean addChests;

    /**
     * Determines if the chest items are included in the structure generation.
     */
    public boolean addChestContents;

    /**
     * Determines if the mineshaft is generated in the structure generation.
     */
    public boolean addMineshaft;

    public DyeColor bedColor;

    /**
     * Initializes a new instance of the {@link HouseImprovedConfiguration} class.
     */
    public HouseImprovedConfiguration() {
        super();

        this.addChests = true;
        this.addChestContents = true;
        this.addMineshaft = true;
        this.houseStyle = HouseStyle.SPRUCE_HOME;
        this.bedColor = DyeColor.RED;
    }

    @Override
    public void Initialize() {
        super.Initialize();
        this.houseStyle = HouseStyle.SPRUCE_HOME;
        this.bedColor = DyeColor.RED;
    }

    @Override
    protected CompoundTag CustomWriteToCompoundTag(CompoundTag tag) {
        tag.putInt(HouseImprovedConfiguration.houseStyleTag, this.houseStyle.value);
        tag.putBoolean(HouseImprovedConfiguration.addChestTag, this.addChests);
        tag.putBoolean(HouseImprovedConfiguration.addChestContentsTag, this.addChestContents);
        tag.putBoolean(HouseImprovedConfiguration.addMineshaftTag, this.addMineshaft);
        tag.putInt(HouseImprovedConfiguration.bedColorTag, this.bedColor.getId());

        return tag;
    }

    @Override
    protected void CustomReadFromNBTTag(CompoundTag messageTag, StructureConfiguration config) {
        HouseImprovedConfiguration houseConfiguration = ((HouseImprovedConfiguration) config);

        if (messageTag.contains(HouseImprovedConfiguration.houseStyleTag)) {
            houseConfiguration.houseStyle = HouseStyle.ValueOf(messageTag.getInt(HouseImprovedConfiguration.houseStyleTag));
        }

        if (messageTag.contains(HouseImprovedConfiguration.addChestTag)) {
            houseConfiguration.addChests = messageTag.getBoolean(HouseImprovedConfiguration.addChestTag);
        }

        if (messageTag.contains(HouseImprovedConfiguration.addChestContentsTag)) {
            houseConfiguration.addChestContents = messageTag.getBoolean(HouseImprovedConfiguration.addChestContentsTag);
        }

        if (messageTag.contains(HouseImprovedConfiguration.addMineshaftTag)) {
            houseConfiguration.addMineshaft = messageTag.getBoolean(HouseImprovedConfiguration.addMineshaftTag);
        }

        if (messageTag.contains(HouseImprovedConfiguration.bedColorTag)) {
            houseConfiguration.bedColor = DyeColor.byId(messageTag.getInt(HouseImprovedConfiguration.bedColorTag));
        }
    }

    /**
     * Custom method to read the CompoundNBT message.
     *
     * @param messageTag The message to create the configuration from.
     * @return An new configuration object with the values derived from the CompoundNBT.
     */
    @Override
    public HouseImprovedConfiguration ReadFromCompoundTag(CompoundTag messageTag) {
        HouseImprovedConfiguration config = new HouseImprovedConfiguration();

        return (HouseImprovedConfiguration) super.ReadFromCompoundTag(messageTag, config);
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
        StructureHouseImproved structure = StructureHouseImproved.CreateInstance(this.houseStyle.getStructureLocation(), StructureHouseImproved.class);

        if (structure.BuildStructure(this, world, hitBlockPos, player)) {
            this.RemoveStructureItemFromPlayer(player, ModRegistryBase.HouseImproved);
        }
    }

    /**
     * This house style is used to determine what type of houses are available to the user.
     *
     * @author WuestMan
     */
    public enum HouseStyle {
        SPRUCE_HOME(0, GuiLangKeys.IMPROVED_HOUSE_SPRUCE, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_spruce.png"),
                "assets/prefab/structures/house_improved_spruce.gz"),
        ACACIA_HOME(1, GuiLangKeys.IMPROVED_HOUSE_ACACIA, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_acacia.png"),
                "assets/prefab/structures/house_improved_acacia.gz"),
        EARTHEN_HOME(2, GuiLangKeys.IMPROVED_EARTHEN_HOME, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_earthen.png"),
                "assets/prefab/structures/house_improved_earthen.gz"),
        JUNGLE_TREE_HOME(3, GuiLangKeys.IMPROVED_JUNGLE_HOME, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_jungle.png"),
                "assets/prefab/structures/house_improved_jungle.gz"),
        NETHER_HOME(4, GuiLangKeys.IMPROVED_NETHER_HOME, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_nether.png"),
                "assets/prefab/structures/house_improved_nether.gz"),
        MOUNTAIN_HOME(5, GuiLangKeys.IMPROVED_MOUNTAIN_HOME, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_mountain.png"),
                "assets/prefab/structures/house_improved_mountain.gz"),
        ACACIA_HOME2(6, GuiLangKeys.IMPROVED_HOUSE_ACACIA_2, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_acacia_2.png"),
                "assets/prefab/structures/house_improved_acacia_2.gz"),
        MODERN_HOME(7, GuiLangKeys.IMPROVED_HOUSE_MODERN, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_modern.png"),
                "assets/prefab/structures/house_improved_modern.gz"),
        CRIMSON_HOME(8, GuiLangKeys.IMPROVED_HOUSE_CRIMSON, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_crimson.png"),
                "assets/prefab/structures/house_improved_crimson.gz"),
        TOWER_HOME(9, GuiLangKeys.IMPROVED_HOUSE_TOWER, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_tower.png"),
                "assets/prefab/structures/house_improved_tower.gz"),
        HOBBIT_HOME(10, GuiLangKeys.IMPROVED_HOUSE_HOBBIT, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_hobbit.png"),
                "assets/prefab/structures/house_improved_hobbit.gz"),
        COTTAGE_HOME(11, GuiLangKeys.IMPROVED_HOUSE_COTTAGE, ResourceLocation.tryBuild(PrefabBase.MODID, "textures/gui/house_improved_cottage.png"),
                "assets/prefab/structures/house_improved_cottage.gz");

        private final int value;
        private final String displayName;
        private final ResourceLocation housePicture;
        private final String structureLocation;

        HouseStyle(int newValue, String displayName, ResourceLocation housePicture, String structureLocation) {
            this.value = newValue;
            this.displayName = displayName;
            this.housePicture = housePicture;
            this.structureLocation = structureLocation;
        }

        public static HouseStyle ValueOf(int value) {
            for (HouseStyle currentValue : HouseStyle.values()) {
                if (currentValue.value == value) {
                    return currentValue;
                }
            }

            return HouseStyle.SPRUCE_HOME;
        }

        public int getValue() {
            return value;
        }

        public String getDisplayName() {
            return GuiLangKeys.translateString(this.displayName);
        }

        public String getTranslationString() {
            return this.displayName;
        }

        public ResourceLocation getHousePicture() {
            return this.housePicture;
        }

        public String getStructureLocation() {
            return this.structureLocation;
        }
    }
}