package com.crepsman.hextechmod.datagen;

import com.crepsman.hextechmod.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import com.crepsman.hextechmod.item.ModItems;
import net.minecraft.registry.tag.ItemTags;


import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {


    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.HEXTECH_ARMOR_MATERIALS)
                .add(ModItems.HEXTECH_HELMET)
                .add(ModItems.HEXTECH_CHESTPLATE)
                .add(ModItems.HEXTECH_LEGGINGS)
                .add(ModItems.HEXTECH_BOOTS);

        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.HEXTECH_HELMET)
                .add(ModItems.HEXTECH_CHESTPLATE)
                .add(ModItems.HEXTECH_LEGGINGS)
                .add(ModItems.HEXTECH_BOOTS);
        getOrCreateTagBuilder(ModTags.MERCURY_HAMMER)
                .add(ModItems.MERCURY_HAMMER);

    }
}
