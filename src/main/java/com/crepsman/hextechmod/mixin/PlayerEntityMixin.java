package com.crepsman.hextechmod.mixin;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import com.crepsman.hextechmod.util.DamadgeSourceUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    // Flag to prevent recursion
    private static boolean IS_IN_DAMAGE_HANDLER = false;

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // Skip if we're already processing damage to prevent recursion
        if (IS_IN_DAMAGE_HANDLER) {
            return;
        }

        PlayerEntity player = (PlayerEntity)(Object)this;

        // Check if player is blocking with gauntlets
        if (AtlasGauntlets.isPlayerBlocking(player)) {
            boolean hasMainHandGauntlet = player.getMainHandStack().getItem() instanceof AtlasGauntlets;
            boolean hasOffHandGauntlet = player.getOffHandStack().getItem() instanceof AtlasGauntlets;

            if (hasMainHandGauntlet && hasOffHandGauntlet) {
                // Handle projectiles - block them completely
                if (DamadgeSourceUtils.isProjectile(source)) {
                    // Play deflection sound
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0f, 1.2f);

                    // Create particle effect for the deflection
                    if (player.getWorld() instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(
                                ParticleTypes.CRIT,
                                player.getX(), player.getY() + 1.0, player.getZ(),
                                15, 0.5, 0.5, 0.5, 0.1
                        );
                    }

                    cir.setReturnValue(false); // Cancel damage completely
                    return;
                }

                // Reduce other damage types by half
                float reducedAmount = amount * 0.5f;

                // Play blocking sound
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.8f, 0.8f);

                // Apply reduced damage with recursion protection
                IS_IN_DAMAGE_HANDLER = true;
                try {
                    if (player.getWorld() instanceof ServerWorld serverWorld) {
                        boolean result = player.damage(serverWorld, source, reducedAmount);
                        cir.setReturnValue(result);
                    } else {
                        // Handle client-side case
                        cir.setReturnValue(true);
                    }
                } finally {
                    IS_IN_DAMAGE_HANDLER = false;
                }
            }
        }
    }
}