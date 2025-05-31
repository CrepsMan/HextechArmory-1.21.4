package com.crepsman.hextechmod.mixin;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemFeatureRenderer.class)
public class HeldItemFeatureRendererMixin {

    @Inject(method = "render*", at = @At("RETURN"))
    private void afterRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                             int light, ArmedEntityRenderState state, float limbAngle,
                             float limbDistance, CallbackInfo ci) {
        // Use client.player directly instead of trying to cast state
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Get items from player hands
        ItemStack mainHandStack = client.player.getMainHandStack();
        ItemStack offHandStack = client.player.getOffHandStack();

        // Check if Atlas Gauntlets are in either hand
        boolean mainHandHasGauntlets = mainHandStack.getItem() instanceof AtlasGauntlets;
        boolean offHandHasGauntlets = offHandStack.getItem() instanceof AtlasGauntlets;

        // Return if no gauntlets are held or if both hands already have gauntlets
        if ((!mainHandHasGauntlets && !offHandHasGauntlets) || (mainHandHasGauntlets && offHandHasGauntlets)) {
            return;
        }


    }
}