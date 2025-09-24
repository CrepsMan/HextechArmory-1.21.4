package com.crepsman.hextechmod.item.weapons;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class GloveBlockingItem extends Item {
    private static final int NORMAL_COOLDOWN_TICKS = 140; // 7 seconds
    private static final int EXTENDED_COOLDOWN_TICKS = 280; // 14 seconds
    private static final int MAX_USE_TICKS = 200; // max hold duration

    public GloveBlockingItem(Settings settings) {
        super(settings.maxDamage(450));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    public int getMaxUseTime(ItemStack stack) {
        return MAX_USE_TICKS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user.getItemCooldownManager().isCoolingDown(stack)) {
            return ActionResult.FAIL;
        }
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity entity, int remainingUseTicks) {
        if (entity instanceof PlayerEntity player) {
            int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;
            int cooldown = usedTicks >= userThreshold() ? EXTENDED_COOLDOWN_TICKS : NORMAL_COOLDOWN_TICKS;
            player.getItemCooldownManager().set(stack, cooldown);
        }
        return false;
    }

    private int userThreshold() {
        return 100; // threshold for extended cooldown
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
