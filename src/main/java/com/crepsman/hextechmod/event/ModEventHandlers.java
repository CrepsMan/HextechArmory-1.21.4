package com.crepsman.hextechmod.event;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import com.crepsman.hextechmod.util.DamadgeSourceUtils;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class ModEventHandlers {
    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == Hand.MAIN_HAND && AtlasGauntlets.isWornInOffhand(player)) {
                AtlasGauntlets gauntlets = (AtlasGauntlets) player.getStackInHand(Hand.OFF_HAND).getItem();
                if (gauntlets.isBlocking()) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hand == Hand.MAIN_HAND && AtlasGauntlets.isWornInOffhand(player)) {
                AtlasGauntlets gauntlets = (AtlasGauntlets) player.getStackInHand(Hand.OFF_HAND).getItem();
                if (gauntlets.isBlocking()) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (hand == Hand.MAIN_HAND && AtlasGauntlets.isWornInOffhand(player)) {
                AtlasGauntlets gauntlets = (AtlasGauntlets) player.getStackInHand(Hand.OFF_HAND).getItem();
                if (gauntlets.isBlocking()) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });
    }
}