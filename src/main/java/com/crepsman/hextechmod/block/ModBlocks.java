package com.crepsman.hextechmod.block;

import com.crepsman.hextechmod.HextechMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.function.Function;

public class ModBlocks {
    public static final Block TITANIUM_ORE_BLOCK = registerBlockWithItem("titanium_ore_block", Block::new, AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK)).getLeft();
    public static final Block TITANIUM_BLOCK = registerBlockWithItem("titanium_block", Block::new, AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK)).getLeft();
    public static final Block RAW_TITANIUM_BLOCK = registerBlockWithItem("raw_titanium_block", Block::new, AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK)).getLeft();
    public static final Block TITANIUM_DEEPSLATE_ORE_BLOCK = registerBlockWithItem("titanium_deepslate_ore_block", Block::new, AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK)).getLeft();

    public static void registerModBlock() {



        HextechMod.LOGGER.info("Registering Mod Blocks for " + HextechMod.MOD_ID);
    }

    public static <T extends Item> T registerItem(String name, Function<Item.Settings, T> factory) {
        Identifier id = Identifier.of(HextechMod.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        return Registry.register(
                Registries.ITEM,
                key,
                factory.apply(new Item.Settings().registryKey(key))
        );
    }

    public static <T extends Block> T registerBlock(String name, Function<AbstractBlock.Settings, T> factory) {
        return registerBlock(name, factory, AbstractBlock.Settings.create());
    }

    public static <T extends Block> T registerBlock(String name, Function<AbstractBlock.Settings, T> factory, AbstractBlock.Settings base) {
        Identifier id = Identifier.of(HextechMod.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        return Registry.register(
                Registries.BLOCK,
                key,
                factory.apply(base.registryKey(key))
        );
    }

    public static <T extends Block> Pair<T, BlockItem> registerBlockWithItem(String name, Function<AbstractBlock.Settings, T> factory, AbstractBlock.Settings base) {
        T block = registerBlock(name, factory, base);
        return new Pair<>(
                block,
                registerItem(name, settings -> new BlockItem(block, settings))
        );
    }
}