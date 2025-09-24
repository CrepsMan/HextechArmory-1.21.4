package com.crepsman.hextechmod.mixin;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import com.crepsman.hextechmod.util.DamageSourceUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    // Replaced @Inject with @ModifyVariable for better compatibility
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float modifyDamage(float originalAmount, ServerWorld world, DamageSource source, float amount)
    {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (AtlasGauntlets.isBlocking(player)) {
            boolean hasMainHandGauntlet = player.getMainHandStack().getItem() instanceof AtlasGauntlets;

            if (hasMainHandGauntlet) {
                if (DamageSourceUtils.isProjectile(source)) {
                    player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0f, 1.2f);

                    if (player.getWorld() instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.CRIT, player.getX(), player.getY() + 1.0, player.getZ(),
                                15, 0.5, 0.5, 0.5, 0.1);
                    }

                    return 0.0f; // Fully block projectile damage
                }

                return originalAmount * 0.5f; // Halve non-projectile damage
            }
        }

        return originalAmount;
    }
}