package com.crepsman.hextechmod.item.weapons.util;

import com.crepsman.hextechmod.util.HextechPowerUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GauntletBlockingHandler {
    private static final int NORMAL_COOLDOWN_TICKS = 140;
    private static final int EXTENDED_COOLDOWN_TICKS = 280;
    private static final int MAX_USE_TIME = 100;
    private static final int ABSOLUTE_MAX_USE_TIME = 200;
    private static final int BLOCKING_POWER_COST_PER_TICK = 1;

    private static final Map<UUID, Long> playerBlockingStartTimes = new HashMap<>();

    public static ActionResult handleUse(World world, PlayerEntity user, Hand hand, ItemStack stack) {
        if (user.getItemCooldownManager().isCoolingDown(stack)) {
            return ActionResult.FAIL;
        }
        if (!user.isCreative() && !HextechPowerUtils.hasPower(stack, 20)) {
            user.sendMessage(Text.translatable("message.hextechmod.not_enough_power"), true);
            return ActionResult.FAIL;
        }
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    public static void handleOnStoppedUsing(ItemStack stack, World world, LivingEntity entity, int remainingUseTicks) {
        if (!(entity instanceof PlayerEntity player)) return;
        // Calculate duration
        Long start = playerBlockingStartTimes.remove(player.getUuid());
        if (start == null) return;
        int used = (int)(world.getTime() - start);
        int cooldown = used >= MAX_USE_TIME ? EXTENDED_COOLDOWN_TICKS : NORMAL_COOLDOWN_TICKS;
        player.getItemCooldownManager().set(stack, cooldown);
        if (used >= MAX_USE_TIME) {
            player.sendMessage(Text.translatable("message.hextechmod.gauntlets.extended_cooldown"), true);
        }
    }

    public static void handleUsageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;
        UUID id = player.getUuid();
        // record start
        if (!playerBlockingStartTimes.containsKey(id) && player.isUsingItem() && player.getActiveItem() == stack) {
            playerBlockingStartTimes.put(id, world.getTime());
        }
        long startTime = playerBlockingStartTimes.getOrDefault(id, world.getTime());
        int timeUsed = (int)(world.getTime() - startTime);
        // absolute limit
        if (timeUsed >= ABSOLUTE_MAX_USE_TIME) {
            player.stopUsingItem();
            player.clearActiveItem();
            playerBlockingStartTimes.remove(id);
            if (!world.isClient) {
                player.getItemCooldownManager().set(stack, EXTENDED_COOLDOWN_TICKS);
                player.sendMessage(Text.translatable("message.hextechmod.gauntlets.max_blocking_time"), true);
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 0.6f, 1.2f);
            }
            return;
        }
        // power cost per tick
        if (!player.isCreative() && !HextechPowerUtils.hasPower(stack, BLOCKING_POWER_COST_PER_TICK)) {
            player.stopUsingItem();
            playerBlockingStartTimes.remove(id);
            return;
        }
        if (!player.isCreative()) {
            HextechPowerUtils.consumePower(stack, BLOCKING_POWER_COST_PER_TICK);
        }
    }
}
