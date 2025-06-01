package com.crepsman.hextechmod.item.smithing_templates;

import com.crepsman.hextechmod.HextechMod;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MercuryHammerCombinerSmithingTemplateItem extends SmithingTemplateItem {
    private static final Formatting DESCRIPTION_FORMATTING;
    private static final Text HEXTECH_COMBINER_APPLIES_TO_TEXT;
    private static final Text HEXTECH_COMBINER_INGREDIENTS_TEXT;
    private static final Identifier EMPTY_SLOT_HEXTECH_HANDLE;
    private static final Identifier EMPTY_SLOT_MERCURY_HAMMER_HEAD;
    private static final Text HEXTECH_COMBINER_BASE_SLOT_DESCRIPTION_TEXT;
    private static final Text HEXTECH_COMBINER_ADDITIONS_SLOT_DESCRIPTION_TEXT;

    public MercuryHammerCombinerSmithingTemplateItem(Text appliesToText, Text ingredientsText, Text baseSlotDescriptionText, Text additionsSlotDescriptionText, List<Identifier> emptyBaseSlotTextures, List<Identifier> emptyAdditionsSlotTextures, Settings settings) {
        super(appliesToText, ingredientsText, baseSlotDescriptionText, additionsSlotDescriptionText, emptyBaseSlotTextures, emptyAdditionsSlotTextures, settings);
    }

    public static SmithingTemplateItem createNetheriteUpgrade(Settings settings) {
        return new SmithingTemplateItem(HEXTECH_COMBINER_APPLIES_TO_TEXT, HEXTECH_COMBINER_INGREDIENTS_TEXT, HEXTECH_COMBINER_BASE_SLOT_DESCRIPTION_TEXT, HEXTECH_COMBINER_ADDITIONS_SLOT_DESCRIPTION_TEXT, new ArrayList<Identifier>(Collections.singleton(EMPTY_SLOT_HEXTECH_HANDLE)), new ArrayList<Identifier>(Collections.singleton(EMPTY_SLOT_MERCURY_HAMMER_HEAD)), settings);
    }

    static {
        DESCRIPTION_FORMATTING = Formatting.BLUE;
        HEXTECH_COMBINER_APPLIES_TO_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.tryParse(HextechMod.MOD_ID,"smithing_template.hextech_combiner.applies_to"))).formatted(DESCRIPTION_FORMATTING);
        HEXTECH_COMBINER_INGREDIENTS_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.tryParse(HextechMod.MOD_ID,"smithing_template.hextech_combiner.ingredients"))).formatted(DESCRIPTION_FORMATTING);
        HEXTECH_COMBINER_BASE_SLOT_DESCRIPTION_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.tryParse(HextechMod.MOD_ID,"smithing_template.hextech_combiner.base_slot_description")));
        HEXTECH_COMBINER_ADDITIONS_SLOT_DESCRIPTION_TEXT = Text.translatable(Util.createTranslationKey("item", Identifier.tryParse(HextechMod.MOD_ID,"smithing_template.hextech_combiner.additions_slot_description")));
        EMPTY_SLOT_HEXTECH_HANDLE = Identifier.tryParse(HextechMod.MOD_ID, "container/slot/empty_handle");
        EMPTY_SLOT_MERCURY_HAMMER_HEAD = Identifier.tryParse( HextechMod.MOD_ID, "container/slot/empty_head");
    }



}
