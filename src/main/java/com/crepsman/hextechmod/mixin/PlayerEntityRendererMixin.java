package com.crepsman.hextechmod.mixin;

import com.crepsman.hextechmod.client.GauntletAnimationManager;
import com.crepsman.hextechmod.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(BipedEntityModel.class)
public abstract class PlayerEntityRendererMixin<T extends BipedEntityRenderState> extends EntityModel<T> implements ModelWithArms, ModelWithHead {
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart body;
    @Shadow @Final public ModelPart head;

    protected PlayerEntityRendererMixin(ModelPart modelPart, Function<Identifier, RenderLayer> function) {
        super(modelPart, function);
    }

    @Shadow protected abstract ModelPart getArm(Arm arm);

    @Inject(method = "setAngles", at = @At("TAIL"))
    private void onSetAngles(T state, CallbackInfo ci) {
        // Third person animations for gauntlets
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Get player's held items
        ItemStack mainHandItem = client.player.getStackInHand(Hand.MAIN_HAND);
        ItemStack offHandItem = client.player.getStackInHand(Hand.OFF_HAND);

        // Check if the player is using the Atlas Gauntlets
        boolean hasGauntlets = mainHandItem.isOf(ModItems.ATLAS_GAUNTLETS) ||
                offHandItem.isOf(ModItems.ATLAS_GAUNTLETS);

        if (hasGauntlets) {
            // Blocking animation in third person
            if (client.player.isUsingItem()) {
                // Create a defensive X pattern with arms
                float blockingIntensity = 0.8F;

                this.rightArm.pitch = -0.5F * blockingIntensity;
                this.rightArm.yaw = -0.4F * blockingIntensity;

                this.leftArm.pitch = -0.5F * blockingIntensity;
                this.leftArm.yaw = 0.4F * blockingIntensity;

                // Slight forward lean for defensive stance
                this.body.pitch = 0.05F;
            }

            // Dash animation in third person
            if (GauntletAnimationManager.isDashing()) {
                float dashProgress = GauntletAnimationManager.getDashProgress();

                // Superman flying pose
                this.rightArm.pitch = -2.5F * dashProgress;
                this.rightArm.roll = 0.2F * dashProgress;

                this.leftArm.pitch = -2.5F * dashProgress;
                this.leftArm.roll = -0.2F * dashProgress;

                // Lean forward during dash
                this.body.pitch = 0.3F * dashProgress;
                this.head.pitch = 0.3F * dashProgress;
            }
        }
    }

    @Inject(method = "animateArms", at = @At("TAIL"))
    protected void animateArms(T state, float animationProgress, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        ItemStack mainHandItem = client.player.getStackInHand(Hand.MAIN_HAND);
        ItemStack offHandItem = client.player.getStackInHand(Hand.OFF_HAND);

        float f = animationProgress;
        if (f > 0.0F && (mainHandItem.isOf(ModItems.ATLAS_GAUNTLETS) ||
                offHandItem.isOf(ModItems.ATLAS_GAUNTLETS))) {

            // Determine which arm is mainhand
            boolean isRightMainHand = client.player.getMainArm() == Arm.RIGHT;

            // Reduce body rotation for more direct forward punches
            this.body.yaw = MathHelper.sin(MathHelper.sqrt(f) * 6.2831855F) * 0.1F;

            // Calculate main hand animation progress
            float mainProgress = f;
            float mainEase = 1.0F - mainProgress;
            mainEase = 1.0F - (mainEase * mainEase * mainEase);

            // Calculate off hand animation progress (with delay)
            float offDelay = 0.2F; // Slightly reduced delay for more responsive feel
            float offProgress = Math.max(0.0F, f - offDelay) * (1.0F / (1.0F - offDelay));
            offProgress = MathHelper.clamp(offProgress, 0.0F, 1.0F);
            float offEase = 1.0F - offProgress;
            offEase = 1.0F - (offEase * offEase * offEase);

            // Apply more dynamic punch animations
            if (isRightMainHand) {
                // Right arm - improved forward punch with proper shoulder rotation
                float rightPunchPhase = mainEase * 3.1415927F;
                this.rightArm.pitch = -1.2F + MathHelper.cos(rightPunchPhase) * 0.7F;
                this.rightArm.yaw = -0.2F + this.body.yaw;
                this.rightArm.roll = -0.2F;

                // Left arm follows with proper delay
                float leftPunchPhase = offEase * 3.1415927F;
                this.leftArm.pitch = -1.2F + MathHelper.cos(leftPunchPhase) * 0.7F;
                this.leftArm.yaw = 0.2F + this.body.yaw;
                this.leftArm.roll = 0.2F;
            } else {
                // Left arm - improved forward punch
                float leftPunchPhase = mainEase * 3.1415927F;
                this.leftArm.pitch = -1.2F + MathHelper.cos(leftPunchPhase) * 0.7F;
                this.leftArm.yaw = 0.2F + this.body.yaw;
                this.leftArm.roll = 0.2F;

                // Right arm follows with proper delay
                float rightPunchPhase = offEase * 3.1415927F;
                this.rightArm.pitch = -1.2F + MathHelper.cos(rightPunchPhase) * 0.7F;
                this.rightArm.yaw = -0.2F + this.body.yaw;
                this.rightArm.roll = -0.2F;
            }
        }
    }
}