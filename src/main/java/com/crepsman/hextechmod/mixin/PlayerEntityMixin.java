package com.crepsman.hextechmod.mixin;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import com.crepsman.hextechmod.util.DamadgeSourceUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (AtlasGauntlets.isPlayerBlocking(player)) {
            boolean hasMainHandGauntlet = player.getMainHandStack().getItem() instanceof AtlasGauntlets;
            boolean hasOffHandGauntlet = player.getOffHandStack().getItem() instanceof AtlasGauntlets;

            if (hasMainHandGauntlet) {
                if (DamadgeSourceUtils.isProjectile(source)) {
                    // Block projectile completely
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0f, 1.2f);

                    if (player.getWorld() instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.CRIT, player.getX(), player.getY() + 1.0, player.getZ(),
                                15, 0.5, 0.5, 0.5, 0.1);
                    }

                    cir.setReturnValue(false); // cancel damage entirely
                    return;
                }

                // Half damage
                float reducedAmount = amount * 0.5f;

                // Apply reduced damage directly by setting health manually
                float newHealth = player.getHealth() - reducedAmount;
                if (newHealth <= 0.0F) {
                    player.setHealth(0.0F);
                    player.onDeath(source);
                } else {
                    player.setHealth(newHealth);
                }

                // Play sound and particles
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.8f, 0.8f);

                if (player.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1.0, player.getZ(),
                            10, 0.3, 0.3, 0.3, 0.05);
                }

                cir.setReturnValue(true); // We handled the damage
            }
        }
    }
}