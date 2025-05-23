package com.crepsman.hextechmod;

import com.crepsman.hextechmod.client.GauntletAnimationManager;
import com.crepsman.hextechmod.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;

public class HextechModClient implements ClientModInitializer {
    private static int animationTicks = 0;
    private static boolean isAnimating = false;

    @Override
    public void onInitializeClient() {
        // Register client tick event for animations
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Update all animations each tick
            GauntletAnimationManager.updateAnimations();

            if (client.player != null) {
                if (client.player.isUsingItem() &&
                        (client.player.getMainHandStack().isOf(ModItems.ATLAS_GAUNTLETS) ||
                                client.player.getOffHandStack().isOf(ModItems.ATLAS_GAUNTLETS))) {

                    // Shield animation
                    isAnimating = true;
                    animationTicks++;

                    if (animationTicks % 5 == 0) {
                        // Spawn particles in a shield pattern
                        double radius = 1.5;
                        for (int i = 0; i < 8; i++) {
                            double angle = (Math.PI * 2.0 * i) / 8.0;
                            client.world.addParticle(
                                    ParticleTypes.ELECTRIC_SPARK,
                                    client.player.getX() + Math.cos(angle) * radius,
                                    client.player.getY() + 1.0,
                                    client.player.getZ() + Math.sin(angle) * radius,
                                    0, 0.05, 0
                            );
                        }
                    }
                } else {
                    if (isAnimating) {
                        isAnimating = false;
                        animationTicks = 0;
                    }
                }

                // Add dash particles when dashing
                if (GauntletAnimationManager.isDashing()) {
                    for (int i = 0; i < 3; i++) {
                        client.world.addParticle(
                                ParticleTypes.CLOUD,
                                client.player.getX(),
                                client.player.getY() + 1.0,
                                client.player.getZ(),
                                (client.player.getRandom().nextFloat() - 0.5) * 0.3,
                                (client.player.getRandom().nextFloat() - 0.5) * 0.1,
                                (client.player.getRandom().nextFloat() - 0.5) * 0.3
                        );
                    }
                }
            }
        });
    }

    // Call this from AtlasGauntlets.performDash
    public static void playDashAnimation(MinecraftClient client) {
        // Start the dash animation
        GauntletAnimationManager.startDashAnimation();

        if (client.world != null && client.player != null) {
            for (int i = 0; i < 20; i++) {
                client.world.addParticle(
                        ParticleTypes.CLOUD,
                        client.player.getX(),
                        client.player.getY() + 1.0,
                        client.player.getZ(),
                        (client.player.getRandom().nextFloat() - 0.5) * 0.4,
                        (client.player.getRandom().nextFloat() - 0.5) * 0.2,
                        (client.player.getRandom().nextFloat() - 0.5) * 0.4
                );
            }
        }
    }
}