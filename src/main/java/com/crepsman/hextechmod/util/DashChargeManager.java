package com.crepsman.hextechmod.util;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.UUID;

public class DashChargeManager {
    private static final HashMap<UUID, Integer> playerCharges = new HashMap<>();
    private static final int MAX_CHARGE_TICKS = 60; // 3 seconds
    private static final double MIN_DASH_DISTANCE = 1.0;
    private static final double MAX_DASH_DISTANCE = 3.0;

    public static void startCharging(PlayerEntity player) {
        playerCharges.put(player.getUuid(), 0);
    }

    public static void incrementCharge(PlayerEntity player) {
        if (!playerCharges.containsKey(player.getUuid())) return;

        int currentCharge = playerCharges.get(player.getUuid());
        if (currentCharge < MAX_CHARGE_TICKS) {
            playerCharges.put(player.getUuid(), currentCharge + 1);

            // Show charge particles every 10 ticks
            if (currentCharge % 10 == 0 && player.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        player.getX(),
                        player.getY() + 1.0,
                        player.getZ(),
                        5, 0.3, 0.3, 0.3, 0.05
                );
            }
        }
    }

    public static void executeDash(PlayerEntity player) {
        if (!playerCharges.containsKey(player.getUuid())) return;

        int chargeAmount = playerCharges.get(player.getUuid());
        playerCharges.remove(player.getUuid());

        // Calculate dash distance based on charge
        double chargePercent = Math.min(1.0, (double)chargeAmount / MAX_CHARGE_TICKS);
        double dashDistance = MIN_DASH_DISTANCE + chargePercent * (MAX_DASH_DISTANCE - MIN_DASH_DISTANCE);

        // Execute the dash with the calculated distance
        AtlasGauntlets.performDash(player, dashDistance);
    }

    public static void cancelCharge(PlayerEntity player) {
        playerCharges.remove(player.getUuid());
    }
}