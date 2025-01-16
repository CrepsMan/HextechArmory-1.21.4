package com.crepsman.hextechmod.item.equipment;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;

import java.util.EnumMap;

public interface ModArmorMaterials {
    ArmorMaterial HEXTECH = new ArmorMaterial(40, Util.make(new EnumMap<EquipmentType, Integer>(EquipmentType.class), (map) -> {
        map.put(EquipmentType.BOOTS, 4);
        map.put(EquipmentType.LEGGINGS, 7);
        map.put(EquipmentType.CHESTPLATE, 9);
        map.put(EquipmentType.HELMET, 4);
        map.put(EquipmentType.BODY, 12);
    }), 20, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 3.5F, 0.2F, ItemTags.REPAIRS_NETHERITE_ARMOR, EquipmentAssetKeys.NETHERITE);
}