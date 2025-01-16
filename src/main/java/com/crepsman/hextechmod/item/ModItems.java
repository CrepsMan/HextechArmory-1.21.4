package com.crepsman.hextechmod.item;


import com.crepsman.hextechmod.HextechMod;
import com.crepsman.hextechmod.item.armor.HextechArmor;
import com.crepsman.hextechmod.item.equipment.ModArmorMaterials;
import com.crepsman.hextechmod.item.materials.ModToolMaterials;
import com.crepsman.hextechmod.item.smithing_templates.HextechHammerCombinerSmithingTemplateItem;
import com.crepsman.hextechmod.item.weapons.HextechGauntlets;
import com.crepsman.hextechmod.item.weapons.HextechHammer;
import com.crepsman.hextechmod.item.weapons.HextechSniperRifle;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModItems {
    // Weapons
    public static final Item HEXTECH_GAUNTLETS = registerItem("hextech_gauntlets", HextechGauntlets::new, new Item.Settings().fireproof());
    public static final Item HEXTECH_HAMMER = registerItem("hextech_hammer", settings -> new HextechHammer(ToolMaterial.NETHERITE, 15.0F, -3.5F, settings), new Item.Settings().fireproof());
    public static final Item HEXTECH_SNIPER_RIFLE = registerItem("hextech_sniper_rifle", HextechSniperRifle::new, new Item.Settings().fireproof());

    // Materials
    public static final Item TITANIUM_INGOT = registerItem("titanium_ingot", Item::new, new Item.Settings().fireproof());
    public static final Item RAW_TITANIUM = registerItem("raw_titanium", Item::new, new Item.Settings().fireproof());

    //Parts
    public static final Item HEXTECH_HANDLE = registerItem("hextech_handle", Item::new, new Item.Settings().fireproof());
    public static final Item HEXTECH_HAMMER_HEAD = registerItem("hextech_hammer_head", Item::new, new Item.Settings().fireproof());

    //Smithing templates
    public static final Item HEXTECH_HAMMER_COMBINER_SMITHING_TEMPLATE =registerItem("hextech_hammer_combiner_smithing_template", HextechHammerCombinerSmithingTemplateItem::createNetheriteUpgrade, (new Item.Settings().fireproof().rarity(Rarity.UNCOMMON)));

    // Armor
    public static final Item HEXTECH_ARMOR_BOOTS = registerItem("hextech_boots", settings -> new HextechArmor(ModArmorMaterials.HEXTECH, EquipmentType.BOOTS, settings), new Item.Settings().fireproof());
    public static final Item HEXTECH_ARMOR_LEGGINGS = registerItem("hextech_leggings", settings -> new HextechArmor(ModArmorMaterials.HEXTECH, EquipmentType.LEGGINGS, settings), new Item.Settings().fireproof());
    public static final Item HEXTECH_ARMOR_CHESTPLATE = registerItem("hextech_chestplate", settings -> new HextechArmor(ModArmorMaterials.HEXTECH, EquipmentType.CHESTPLATE, settings), new Item.Settings().fireproof());
    public static final Item HEXTECH_ARMOR_HELMET = registerItem("hextech_helmet", settings -> new HextechArmor(ModArmorMaterials.HEXTECH, EquipmentType.HELMET, settings), new Item.Settings().fireproof());

    public static Item registerItem(String id, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(HextechMod.MOD_ID, id));
        Item item = factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        }
        return Registry.register(Registries.ITEM, key, item);
    }

    public static void registerModItem() {
        HextechMod.LOGGER.info("Registering Mod Items for " + HextechMod.MOD_ID);
    }
}