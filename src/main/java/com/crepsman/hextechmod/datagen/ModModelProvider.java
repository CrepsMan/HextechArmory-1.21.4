package com.crepsman.hextechmod.datagen;

import com.crepsman.hextechmod.block.ModBlocks;
import com.crepsman.hextechmod.item.ModItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.equipment.EquipmentType;

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

        itemModelGenerator.registerArmor(ModItems.HEXTECH_HELMET, ModItems.HEXTECH_ARMOR_PATH,
                EquipmentModel.builder().addHumanoidLayers(ModItems.HEXTECH_ARMOR_PATH).build(), EquipmentSlot.HEAD);

        itemModelGenerator.registerArmor(ModItems.HEXTECH_CHESTPLATE, ModItems.HEXTECH_ARMOR_PATH,
                EquipmentModel.builder().addHumanoidLayers(ModItems.HEXTECH_ARMOR_PATH).build(), EquipmentSlot.CHEST);
        itemModelGenerator.registerArmor(ModItems.HEXTECH_LEGGINGS, ModItems.HEXTECH_ARMOR_PATH,
                EquipmentModel.builder().addHumanoidLayers(ModItems.HEXTECH_ARMOR_PATH).build(), EquipmentSlot.LEGS);
        itemModelGenerator.registerArmor(ModItems.HEXTECH_BOOTS, ModItems.HEXTECH_ARMOR_PATH,
                EquipmentModel.builder().addHumanoidLayers(ModItems.HEXTECH_ARMOR_PATH).build(), EquipmentSlot.FEET);

    }
}
