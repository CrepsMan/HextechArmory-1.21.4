package com.crepsman.hextechmod.util;

import com.crepsman.hextechmod.HextechMod;
import com.crepsman.hextechmod.component.ModDataComponentTypes;
import com.crepsman.hextechmod.item.ModItems;
import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import com.crepsman.hextechmod.item.weapons.MercuryHammer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Utility class for managing Hextech power in items
 */
public class HextechPowerUtils {
    // Constants
    public static final int MAX_POWER = 50000;
    public static final int CRYSTAL_RECHARGE_AMOUNT = 10000;
    private static final String POWER_KEY = "HextechPower";
    private static final int POWER_INCREMENT = 100;

    /**
     * Sets the power level for an item
     * @param stack The ItemStack to modify
     * @param power The power amount to set
     */
    public static void setPower(ItemStack stack, int power) {
        stack.set(ModDataComponentTypes.HEXTECH_POWER, Math.min(power, MAX_POWER));
    }

    /**
     * Gets the current power level of an item
     * @param stack The ItemStack to check
     * @return The current power amount
     */
    public static int getPower(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.HEXTECH_POWER, 0);
    }

    /**
     * Adds power to an item, capped at MAX_POWER
     * @param stack The ItemStack to modify
     * @param amount The amount to add
     * @return The new power level
     */
    public static int addPower(ItemStack stack, int amount) {
        int currentPower = getPower(stack);
        int newPower = Math.min(currentPower + amount, MAX_POWER);
        setPower(stack, newPower);
        return newPower;
    }

    /**
     * Consumes power from an item if available
     * @param stack The ItemStack to modify
     * @param amount The amount to consume
     * @return True if power was successfully consumed
     */
    public static boolean consumePower(ItemStack stack, int amount) {
        int currentPower = getPower(stack);
        if (currentPower >= amount) {
            setPower(stack, currentPower - amount);
            return true;
        }
        return false;
    }

    /**
     * Checks if an item has enough power
     * @param stack The ItemStack to check
     * @param amount The required amount
     * @return True if the item has sufficient power
     */
    public static boolean hasPower(ItemStack stack, int amount) {
        return getPower(stack) >= amount;
    }

    /**
     * Sends a power status message to the player
     * @param player The player to send the message to
     * @param stack The ItemStack being referenced
     */
    public static void sendPowerStatusMessage(PlayerEntity player, ItemStack stack) {
        player.sendMessage(Text.translatable("message.hextechmod.current_power", getPower(stack)), true);
    }

    public static void empowerItem(PlayerEntity player) {
        ItemStack heldStack = player.getMainHandStack();

        if (heldStack.isEmpty()) {
            player.sendMessage(Text.translatable("message.hextechmod.no_item_held"), true);
            return;
        }

        // Check if the item is a hextech powered item
        if (heldStack.getItem() instanceof MercuryHammer ||
                heldStack.getItem() instanceof AtlasGauntlets ||
                heldStack.isIn(ModTags.HEXTECH_POWERED)) {

            // Check if player has crystal in inventory
            if (!player.isCreative() && !player.getInventory().contains(new ItemStack(ModItems.HEXTECH_CRYSTAL))) {
                player.sendMessage(Text.translatable("message.hextechmod.no_crystals"), true);
                return;
            }

            int currentPower = getPower(heldStack);

            // Max power check
            if (currentPower >= MAX_POWER) {
                player.sendMessage(Text.translatable("message.hextechmod.max_power"), true);
                return;
            }

            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.isOf(ModItems.HEXTECH_CRYSTAL)) {
                    stack.decrement(1);
                    HextechPowerUtils.addPower(heldStack, HextechPowerUtils.CRYSTAL_RECHARGE_AMOUNT);
                    HextechPowerUtils.sendPowerStatusMessage(player, heldStack);
                    player.playerScreenHandler.sendContentUpdates();
                    return;
                }
            }

            // Add power
            int newPower = Math.min(MAX_POWER, currentPower + POWER_INCREMENT);
            setPower(heldStack, newPower);

            // Different messages based on item type
            String message;
            if (heldStack.getItem() instanceof MercuryHammer) {
                message = "message.hextechmod.current_power";
            } else if (heldStack.getItem() instanceof AtlasGauntlets) {
                message = "message.hextechmod.gauntlets.power";
            } else {
                message = "message.hextechmod.generic_power";
            }

            player.sendMessage(Text.translatable(message, newPower), true);

        } else {
            player.sendMessage(Text.translatable("message.hextechmod.not_empowerable"), true);
        }
    }
}