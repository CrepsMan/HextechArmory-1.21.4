package com.crepsman.hextechmod.item;

import com.crepsman.hextechmod.util.ModTags;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;

public interface ModToolMaterial {
    // Define a specialized material for Hextech items
    ToolMaterial HEXTECH = new ToolMaterial(
            BlockTags.NEEDS_DIAMOND_TOOL,  // Can mine diamond-level blocks
            2500,                         // High durability
            10.0F,                        // Fast mining speed
            5.0F,                         // High attack damage
            18,                           // Good enchantability
            ModTags.HEXTECH_TOOL_MATERIALS  // Repair with hextech materials
    );

    // Method for gauntlet-specific settings - use public methods only
    static Item.Settings applyGauntletSettings(ToolMaterial material, Item.Settings settings, float attackDamage, float attackSpeed) {
        return material.applySwordSettings(settings, attackDamage, attackSpeed);
    }

    // Method for hammer-specific settings - use public methods only
    static Item.Settings applyHammerSettings(ToolMaterial material, Item.Settings settings, float attackDamage, float attackSpeed) {
        return material.applySwordSettings(settings, attackDamage, attackSpeed);
    }
}