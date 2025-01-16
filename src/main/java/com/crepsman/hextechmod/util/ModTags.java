package com.crepsman.hextechmod.util;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.item.Item;

public class ModTags {
    public static final TagKey<Item> SMELT_TO_TITANIUM_INGOT = TagKey.of(RegistryKeys.ITEM, Identifier.of("hextechmod", "smelt_to_titanium_ingot"));
}