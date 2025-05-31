package com.crepsman.hextechmod.item.weapons;

import com.crepsman.hextechmod.util.HextechPowerUtils;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class MercuryHammer extends MiningToolItem {

    public MercuryHammer(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, BlockTags.AXE_MINEABLE, attackDamage, attackSpeed, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int power = HextechPowerUtils.getPower(stack);
        tooltip.add(Text.translatable("Power: " + power).formatted(Formatting.GOLD));
    }
}
