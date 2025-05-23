package com.crepsman.hextechmod.enchantment;

import com.crepsman.hextechmod.HextechMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEnchantmentEffects {
    /*public static final MapCodec<? extends EnchantmentEntityEffect> DASH =
           registerEntityEffect("dash", (MapCodec<? extends EnchantmentEntityEffect>) EnchantmentEntityEffect.CODEC);*/

    private static MapCodec<? extends EnchantmentEntityEffect> registerEntityEffect(String name, MapCodec<? extends EnchantmentEntityEffect> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(HextechMod.MOD_ID, name), codec);
    }

    public static void registerEnchantmentEffects() {
        HextechMod.LOGGER.info("Registering Mod Enchantment Effects for " + HextechMod.MOD_ID);
    }
}