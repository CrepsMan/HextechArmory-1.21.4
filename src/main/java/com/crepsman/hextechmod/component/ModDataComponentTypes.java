package com.crepsman.hextechmod.component;

import com.crepsman.hextechmod.HextechMod;
import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<Integer> HEXTECH_POWER = register("hextech_power", builder -> builder.codec(Codec.INT));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(HextechMod.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {
        HextechMod.LOGGER.info("Registering Mod Data Component Types for " + HextechMod.MOD_ID);
    }
}
