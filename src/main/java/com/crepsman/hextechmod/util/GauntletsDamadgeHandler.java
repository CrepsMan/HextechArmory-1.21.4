package com.crepsman.hextechmod.util;

import com.crepsman.hextechmod.item.weapons.HextechGauntlets;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class GauntletsDamadgeHandler {

    public static void register() {
        PlayerTickCallback.EVENT.register(player -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ItemStack mainHandItem = serverPlayer.getMainHandStack();
                if (mainHandItem.getItem() instanceof HextechGauntlets gauntlets && gauntlets.isBlocking()) {
                    serverPlayer.getAbilities().invulnerable = true;
                } else {
                    serverPlayer.getAbilities().invulnerable = false;
                }
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ItemStack mainHandItem = serverPlayer.getMainHandStack();
                if (mainHandItem.getItem() instanceof HextechGauntlets gauntlets && gauntlets.isBlocking()) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ItemStack mainHandItem = serverPlayer.getMainHandStack();
                if (mainHandItem.getItem() instanceof HextechGauntlets gauntlets && gauntlets.isBlocking()) {
                    return false;
                }
            }
            return true;
        });
    }
}