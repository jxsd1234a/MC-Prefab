package com.wuest.prefab;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Objects;

import static com.wuest.prefab.ExampleMod.*;

public class ModRegistry {
    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static Block EXAMPLE_BLOCK;

    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static BlockItem EXAMPLE_BLOCK_ITEM;

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static Item EXAMPLE_ITEM;

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(EXAMPLE_BLOCK_ITEM);
            }).build());

    public void register(RegisterEvent event) {
        // This event is called once for each type of registry.
        if (event.getRegistryKey() == Registries.BLOCK) {
            EXAMPLE_BLOCK = new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
            Registry.register(BuiltInRegistries.BLOCK, Objects.requireNonNull(ResourceLocation.tryBuild(MODID, "block_compressed_stone")), EXAMPLE_BLOCK);
        } else if (event.getRegistryKey() == Registries.ITEM) {
            EXAMPLE_BLOCK_ITEM =  new BlockItem(EXAMPLE_BLOCK, new Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, Objects.requireNonNull(ResourceLocation.tryBuild(MODID, "block_compressed_stone")), EXAMPLE_BLOCK_ITEM);

            EXAMPLE_ITEM = new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEdible().nutrition(1).saturationModifier(2f).build()));
            Registry.register(BuiltInRegistries.ITEM, Objects.requireNonNull(ResourceLocation.tryBuild(MODID, "example_item")), EXAMPLE_ITEM);
        }
    }
}
