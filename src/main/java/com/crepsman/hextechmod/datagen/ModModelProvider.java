package com.crepsman.hextechmod.datagen;

import com.crepsman.hextechmod.HextechMod;
import com.crepsman.hextechmod.block.ModBlocks;
import com.crepsman.hextechmod.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TITANIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_TITANIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TITANIUM_ORE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TITANIUM_DEEPSLATE_ORE_BLOCK);


    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.TITANIUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_TITANIUM, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_HEXTECH_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNREFINED_HEXTECH_CRYSTAL_SHARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.HEXTECH_CRYSTAL, Models.GENERATED);
        itemModelGenerator.register(ModItems.HEXTECH_HANDLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.MERCURY_HAMMER_HEAD, Models.GENERATED);



        itemModelGenerator.registerArmor(ModItems.HEXTECH_HELMET, RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), Identifier.of(HextechMod.MOD_ID, "hextech")),
                "helmet", false);
        itemModelGenerator.registerArmor(ModItems.HEXTECH_CHESTPLATE, RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), Identifier.of(HextechMod.MOD_ID, "hextech")),
                "chestplate", false);
        itemModelGenerator.registerArmor(ModItems.HEXTECH_LEGGINGS, RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), Identifier.of(HextechMod.MOD_ID, "hextech")),
                "leggings", false);
        itemModelGenerator.registerArmor(ModItems.HEXTECH_BOOTS, RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), Identifier.of(HextechMod.MOD_ID, "hextech")),
                "boots", false);
    }
}
