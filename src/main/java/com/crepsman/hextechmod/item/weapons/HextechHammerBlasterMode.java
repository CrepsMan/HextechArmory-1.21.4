package com.crepsman.hextechmod.item.weapons;

import com.crepsman.hextechmod.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class HextechHammerBlasterMode extends CrossbowItem {

    public HextechHammerBlasterMode(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand); // Use crossbow functionality
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
        shootProjectile(world, (PlayerEntity) user);
        return false;
    }

    private void shootProjectile(World world, PlayerEntity user) {
        if (!world.isClient) {
            ArrowEntity arrow = new ArrowEntity(EntityType.ARROW, world);
            arrow.setOwner(user);
            arrow.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 3.0F, 1.0F);
            world.spawnEntity(arrow);
        }
    }

    public void toggleHammerMode(PlayerEntity player) {
        player.sendMessage(Text.literal("Hammer mode enabled"), true);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(ModItems.HEXTECH_HAMMER));
    }
}