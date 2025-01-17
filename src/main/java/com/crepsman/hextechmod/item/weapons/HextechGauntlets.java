package com.crepsman.hextechmod.item.weapons;

import com.crepsman.hextechmod.HextechMod;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HextechGauntlets extends Item {
    private float attackDamage = 0;
    private float attackSpeed = 0;
    private boolean blocking;

    public HextechGauntlets(Settings settings) {
        super(settings);
        this.blocking = false;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            blocking = !blocking;
            if (blocking) {
                user.sendMessage(Text.of("Blocking enabled. Damage reduced."), true);
            } else {
                user.sendMessage(Text.of("Blocking disabled. You can attack now."), true);
            }
        }
        return ActionResult.SUCCESS;
    }

    public static boolean isWornInOffhand(PlayerEntity player) {
        ItemStack offhandStack = player.getStackInHand(Hand.OFF_HAND);
        boolean isWorn = offhandStack.getItem() instanceof HextechGauntlets;
        HextechMod.LOGGER.info("isWornInOffhand: " + isWorn);
        return isWorn;
    }

    public static void enhancePickaxe(PlayerEntity player, World world, BlockPos pos, Direction side) {
        HextechMod.LOGGER.info("enhancePickaxe called");
        if (isWornInOffhand(player)) {
            ItemStack mainHandStack = player.getStackInHand(Hand.MAIN_HAND);
            HextechMod.LOGGER.info("Main hand item: " + mainHandStack.getItem());
            if (mainHandStack.getItem() == Items.DIAMOND_PICKAXE || mainHandStack.getItem() == Items.NETHERITE_PICKAXE) {
                HextechMod.LOGGER.info("Enhancing pickaxe");
                List<BlockPos> blocksToBeDestroyed = getBlocksToBeDestroyed(1, pos, (ServerPlayerEntity) player);
                HextechMod.LOGGER.info("Blocks to be destroyed: " + blocksToBeDestroyed.size());
                blocksToBeDestroyed.forEach(blockPos -> {
                    HextechMod.LOGGER.info("Breaking block at: " + blockPos);
                    boolean success = world.breakBlock(blockPos, true, player);
                    HextechMod.LOGGER.info("Block broken: " + success);
                });
            }
        }
    }

    public static List<BlockPos> getBlocksToBeDestroyed(int range, BlockPos initialBlockPos, ServerPlayerEntity player) {
        List<BlockPos> positions = new ArrayList<>();
        HitResult hit = player.raycast(20, 0, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;

            if (blockHit.getSide() == Direction.DOWN || blockHit.getSide() == Direction.UP) {
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        positions.add(new BlockPos(initialBlockPos.getX() + x, initialBlockPos.getY(), initialBlockPos.getZ() + y));
                    }
                }
            }

            if (blockHit.getSide() == Direction.NORTH || blockHit.getSide() == Direction.SOUTH) {
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        positions.add(new BlockPos(initialBlockPos.getX() + x, initialBlockPos.getY() + y, initialBlockPos.getZ()));
                    }
                }
            }

            if (blockHit.getSide() == Direction.EAST || blockHit.getSide() == Direction.WEST) {
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        positions.add(new BlockPos(initialBlockPos.getX(), initialBlockPos.getY() + y, initialBlockPos.getZ() + x));
                    }
                }
            }
        }

        return positions;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(float attackDamage) {
        this.attackDamage = attackDamage;
    }
}