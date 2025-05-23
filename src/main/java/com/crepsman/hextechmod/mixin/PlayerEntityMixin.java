package com.crepsman.hextechmod.mixin;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import com.crepsman.hextechmod.util.DamadgeSourceUtils;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
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

        // Check both hands for gauntlets
        ItemStack mainHandStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack offHandStack = player.getStackInHand(Hand.OFF_HAND);

        AtlasGauntlets gauntlets = null;
        if (mainHandStack.getItem() instanceof AtlasGauntlets && player.getActiveHand() == Hand.MAIN_HAND) {
            gauntlets = (AtlasGauntlets) mainHandStack.getItem();
        } else if (offHandStack.getItem() instanceof AtlasGauntlets && player.getActiveHand() == Hand.OFF_HAND) {
            gauntlets = (AtlasGauntlets) offHandStack.getItem();
        }

        // If player is blocking with gauntlets
        if (gauntlets != null && gauntlets.isBlocking() && player.isUsingItem()) {
            if (DamadgeSourceUtils.isProjectile(source)) {
                // Block all projectile damage
                player.sendMessage(Text.of("Projectile blocked!"), true);
                cir.setReturnValue(false); // Cancel damage completely
            } else {
                // Get blocking efficiency (70% reduction)
                float blockingEfficiency = 0.5f;
                float reducedAmount = amount * (1 - blockingEfficiency);

                player.sendMessage(Text.of(String.format("Blocked %.0f%% damage!", blockingEfficiency * 100)), true);

                // Apply damage with recursion protection
                IS_IN_DAMAGE_HANDLER = true;
                try {
                    cir.setReturnValue(player.damage(world, source, reducedAmount));
                } finally {
                    IS_IN_DAMAGE_HANDLER = false;
                }
            }
        }
    }
}