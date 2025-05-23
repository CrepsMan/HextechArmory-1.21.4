package com.crepsman.hextechmod.util;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;

public class DamadgeSourceUtils {

    /**
     * Checks if a damage source is from a projectile
     * @param source The damage source to check
     * @return true if the damage is from a projectile, false otherwise
     */
    public static boolean isProjectile(DamageSource source) {
        // Check the damage source name for projectile indicators
        String name = source.getName();
        return name.contains("arrow") ||
                name.contains("trident") ||
                name.contains("fireball") ||
                name.contains("thrown") ||
                name.contains("shoot") ||
                name.contains("bullet") ||
                name.contains("projectile");
    }

    /**
     * Checks if a damage source can be blocked
     * @param source The damage source to check
     * @return true if the damage can be blocked, false otherwise
     */
    public static boolean isBlockable(DamageSource source) {
        // These damage types typically bypass armor/blocking
        String name = source.getName();
        return !name.contains("magic") &&
                !name.contains("starve") &&
                !name.contains("drown") &&
                !name.contains("fall") &&
                !name.contains("void") &&
                !name.contains("wither") &&
                !name.contains("cramming") &&
                !name.contains("generic");
    }

    /**
     * Gets the damage type for a damage source
     * @param source The damage source to check
     * @return The damage type registry entry
     */
    public static RegistryEntry<DamageType> getDamageType(DamageSource source) {
        return source.getTypeRegistryEntry();
    }
}