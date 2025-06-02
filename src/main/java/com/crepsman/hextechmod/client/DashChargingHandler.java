package com.crepsman.hextechmod.client;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import com.crepsman.hextechmod.util.KeyBindingHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class DashChargingHandler {
    // Constants for charge timing
    private static final int OPTIMAL_CHARGE_TIME = 30; // 1.5 seconds optimal
    private static final int MAX_CHARGE_TIME = 60;     // 3 seconds max

    // Charging state
    private static int chargeTicks = 0;
    private static boolean isCharging = false;
    private static boolean releasedKey = true;

    // XP bar tracking
    private static float originalXpProgress = 0f;
    private static int originalXpLevel = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = client.player;
            if (player == null) return;

            // Check if player has gauntlets in MAIN HAND only
            boolean hasGauntletsInMainHand = player.getMainHandStack().getItem() instanceof AtlasGauntlets;

            if (!hasGauntletsInMainHand) {
                if (isCharging) {
                    // Restore XP bar if we were charging
                    restoreXpBar(player);
                    isCharging = false;
                }
                chargeTicks = 0;
                return;
            }

            // Start charging when key is pressed
            if (KeyBindingHandler.dashKey.isPressed() && releasedKey &&
                    !player.getItemCooldownManager().isCoolingDown(player.getMainHandStack())) {
                isCharging = true;
                chargeTicks = 0;
                releasedKey = false;

                // Store original XP values
                originalXpProgress = player.experienceProgress;
                originalXpLevel = player.experienceLevel;
            }

            // Increment charge while holding
            if (isCharging && KeyBindingHandler.dashKey.isPressed()) {
                chargeTicks++;

                // Update XP bar to show charge progress (like horse jump)
                updateXpBar(player);

                // Auto-release if held too long
                if (chargeTicks >= MAX_CHARGE_TIME) {
                    release(player);
                }
            }

            // Release the dash when key is released
            if (!KeyBindingHandler.dashKey.isPressed() && !releasedKey) {
                releasedKey = true;
                if (isCharging) {
                    release(player);
                }
            }
        });
    }

    private static void updateXpBar(PlayerEntity player) {
        // Calculate charge progress using horse-like jump curve
        float chargeProgress;

        if (chargeTicks <= OPTIMAL_CHARGE_TIME) {
            // Rising phase - linear increase to max
            chargeProgress = (float)chargeTicks / OPTIMAL_CHARGE_TIME;
        } else {
            // Decreasing phase - falls off after optimal point
            float overcharge = chargeTicks - OPTIMAL_CHARGE_TIME;
            float decreaseFactor = Math.min(overcharge / (MAX_CHARGE_TIME - OPTIMAL_CHARGE_TIME), 1.0f);
            chargeProgress = 1.0f - decreaseFactor;
        }

        // Set XP bar to show charge progress
        player.experienceProgress = chargeProgress;
        player.experienceLevel = 0; // Hide level number, just show bar
    }

    private static void restoreXpBar(PlayerEntity player) {
        // Restore original XP values
        player.experienceProgress = originalXpProgress;
        player.experienceLevel = originalXpLevel;
    }

    private static void release(PlayerEntity player) {
        if (isCharging) {
            // Send packet with the charge time
            KeyBindingHandler.sendDashPacket(chargeTicks);

            // Reset state
            isCharging = false;
            chargeTicks = 0;

            // Restore player's original XP display
            restoreXpBar(player);
        }
    }
}