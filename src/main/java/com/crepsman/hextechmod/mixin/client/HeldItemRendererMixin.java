package com.crepsman.hextechmod.mixin.client;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import com.crepsman.hextechmod.client.GauntletAnimationManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void adjustGauntletRender(AbstractClientPlayerEntity player, float tickDelta, float pitch,
                                      Hand hand, float swingProgress, ItemStack item,
                                      float equipProgress, MatrixStack matrices,
                                      VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();

        boolean isDualWielding = mainHandStack.getItem() instanceof AtlasGauntlets &&
                offHandStack.getItem() instanceof AtlasGauntlets;

        if (isDualWielding && item.getItem() instanceof AtlasGauntlets) {
            boolean isMainHand = hand == Hand.MAIN_HAND;
            boolean isBlocking = player.isUsingItem() &&
                    (player.getActiveItem().getItem() instanceof AtlasGauntlets);
            boolean isDashing = GauntletAnimationManager.isDashing();

            // Initial positioning for gauntlets
            if (isMainHand) {
                matrices.translate(-0.1F, 0F, 0F);
            } else {
                matrices.translate(0F, 0F, 0F); // Reduced from 0.05F to bring left hand closer to body
            }

            // Handle dash animation
            if (isDashing) {
                float dashProgress = GauntletAnimationManager.getDashProgress();
                float easedProgress = easeOutCubic(dashProgress);

                // Push both hands forward during dash
                matrices.translate(0.0F, -0.1F * easedProgress, -0.3F * easedProgress);

                // Rotate slightly to angle forward
                float rotationX = 15.0F * easedProgress;
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));

                // Push hands together in a streamlined position
                float sideOffset = isMainHand ? 0.1F * easedProgress : -0.1F * easedProgress;
                matrices.translate(sideOffset, 0.0F, 0.0F);

                return; // Skip other animations if dashing
            }



            // Inside adjustGauntletRender, replace the blocking section:
            if (isBlocking) {
                float blockingProgress = MathHelper.clamp(player.getItemUseTime() / 10.0F, 0.0F, 1.0F);
                float easedProgress = easeOutCubic(blockingProgress);

                // Bring both hands up and forward, but less extreme
                matrices.translate(0.0F, -0.05F * easedProgress, -0.11F * easedProgress);

                if (isMainHand) {
                    // Right hand: less turned, closer to center
                    matrices.translate(-0.004F * easedProgress, 0.01F * easedProgress, 0.0F);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(5.0F * easedProgress));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-5.0F * easedProgress));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(8.0F * easedProgress));
                } else {
                    // Left hand: closer to player, less vertical offset
                    matrices.translate(0.012F * easedProgress, -0.01F * easedProgress, 0.02F * easedProgress);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(7.0F * easedProgress));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(12.0F * easedProgress));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-7.0F * easedProgress));
                }
            }

            // Apply attack animation if needed
            if (!isBlocking && !isDashing && swingProgress > 0) {
                boolean isActiveHand = (isMainHand && GauntletAnimationManager.useRightHand()) ||
                        (!isMainHand && !GauntletAnimationManager.useRightHand());

                if (isActiveHand) {
                    // Forward punch motion
                    float punchProgress = swingProgress * 2.0F;
                    if (punchProgress > 1.0F) punchProgress = 2.0F - punchProgress;

                    // Forward thrust for punch
                    matrices.translate(0.0F, 0.0F, -0.2F * punchProgress);
                }
            }
        }
    }

    // Smooth easing function for animations
    private float easeOutCubic(float x) {
        return 1 - (float)Math.pow(1 - x, 3);
    }
}