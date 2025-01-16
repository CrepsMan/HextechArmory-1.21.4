package com.crepsman.hextechmod.datagen;

import com.mojang.datafixers.types.templates.Tag;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import com.crepsman.hextechmod.item.ModItems;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;


import javax.swing.text.html.HTML;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup>compleatebleFuture) {
        super(output, compleatebleFuture);
    }

    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
    }
}
