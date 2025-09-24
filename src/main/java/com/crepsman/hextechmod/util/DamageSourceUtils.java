package com.crepsman.hextechmod.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Set;

public class DamageSourceUtils {

    private static final Set<String> UNBLOCKABLE_DAMAGE_TYPES = Set.of(
            "magic", "starve", "drown", "fall", "void", "wither", "cramming", "generic"
    );

    /**
     * Checks if a damage source was caused by a projectile.
     * Supports both vanilla and modded sources.
     *
     * @param source The damage source to check
     * @return true if the damage is from a projectile, false otherwise
     */
    public static boolean isProjectile(DamageSource source) {
        Entity src = source.getSource();
        Entity attacker = source.getAttacker();

        if (src instanceof ProjectileEntity || attacker instanceof ProjectileEntity) {
            return true;
        }

        RegistryKey<DamageType> damageTypeKey = source.getTypeRegistryEntry().getKey().orElse(null);
        if (damageTypeKey != null) {
            Identifier id = damageTypeKey.getValue();
            String path = id.getPath().toLowerCase(Locale.ROOT);

            // Check vanilla and common mod projectile keywords
            if (path.matches(".*(projectile|arrow|trident|fireball|thrown|shot|bolt).*")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether a damage source can be blocked.
     * Excludes common unblockable types like magic or void.
     *
     * @param source The damage source to check
     * @return true if the damage can be blocked, false otherwise
     */
    public static boolean isBlockable(DamageSource source) {
        String name = source.getName().toLowerCase(Locale.ROOT);

        for (String type : UNBLOCKABLE_DAMAGE_TYPES) {
            if (name.contains(type)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Retrieves the DamageType registry entry from the given damage source.
     *
     * @param source The damage source
     * @return The registry entry of its damage type
     */
    public static RegistryEntry<DamageType> getDamageType(DamageSource source) {
        return source.getTypeRegistryEntry();
    }

    /**
     * Checks if the damage source represents an explosion.
     *
     * @param source The damage source
     * @return true if it's explosive, false otherwise
     */
    public static boolean isExplosive(DamageSource source) {
        String name = source.getName().toLowerCase(Locale.ROOT);
        return name.contains("explosion") || name.contains("blast");
    }
}
