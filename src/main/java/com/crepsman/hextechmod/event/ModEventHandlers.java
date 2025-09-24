package com.crepsman.hextechmod.event;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class ModEventHandlers {
    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (hand == Hand.MAIN_HAND && AtlasGauntlets.isBlocking(player)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hand == Hand.MAIN_HAND && AtlasGauntlets.isBlocking(player)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (hand == Hand.MAIN_HAND && AtlasGauntlets.isBlocking(player)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}