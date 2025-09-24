package com.crepsman.hextechmod.util;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GauntlestsUsageEvent implements PlayerBlockBreakEvents.Before {
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();
    private static final int ENERGY_PER_BLOCK = 5; // Energy cost per additional block

    @Override
    public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos,
                                    BlockState state, @Nullable BlockEntity blockEntity) {
        // Skip if this block is already being processed
        if (HARVESTED_BLOCKS.contains(pos)) {
            return true;
        }

        // Check if player has Atlas Gauntlets in off-hand
        ItemStack offHandItem = player.getOffHandStack();
        if (!(offHandItem.getItem() instanceof AtlasGauntlets) || !(player instanceof ServerPlayerEntity serverPlayer)) {
            return true;
        }

        // Check if main hand has a valid tool
        ItemStack mainHandStack = player.getStackInHand(Hand.MAIN_HAND);
        Item mainHandItem = mainHandStack.getItem();
        boolean isValidTool = (
                mainHandItem == Items.DIAMOND_PICKAXE || mainHandItem == Items.NETHERITE_PICKAXE ||
                        mainHandItem == Items.DIAMOND_SHOVEL || mainHandItem == Items.NETHERITE_SHOVEL ||
                        mainHandItem == Items.DIAMOND_AXE || mainHandItem == Items.NETHERITE_AXE
        );

        if (!isValidTool || !mainHandStack.isSuitableFor(state)) {
            return true;
        }

        // Get the direction the player is looking at
        Direction side = null;
        HitResult hitResult = player.raycast(5.0, 0, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            side = ((BlockHitResult)hitResult).getSide();
        }

        // Get additional blocks to break based on the direction
        List<BlockPos> blocksToBreak = AtlasGauntlets.getBlocksToBeDestroyed(1, pos, serverPlayer);

        // Skip additional blocks if there aren't any
        if (blocksToBreak.size() <= 1) {
            return true;
        }

        // Calculate power needed (excluding the original block)
        int additionalBlocks = 0;
        for (BlockPos blockPos : blocksToBreak) {
            if (!blockPos.equals(pos)) {
                BlockState blockState = world.getBlockState(blockPos);
                if (mainHandStack.isSuitableFor(blockState)) {
                    additionalBlocks++;
                }
            }
        }

        int powerRequired = additionalBlocks * ENERGY_PER_BLOCK;

        // Check if we have enough power
        if (!player.isCreative() && !HextechPowerUtils.hasPower(offHandItem, powerRequired)) {
            if (!world.isClient) {
                serverPlayer.sendMessage(Text.translatable("message.hextechmod.not_enough_power"), true);
            }
            return true;
        }

        // Break each valid block and consume power
        int powerUsed = 0;

        for (BlockPos blockPos : blocksToBreak) {
            // Skip the original block
            if (blockPos.equals(pos)) {
                continue;
            }

            BlockState blockState = world.getBlockState(blockPos);
            if (!mainHandStack.isSuitableFor(blockState)) {
                continue;
            }

            // Mark this block as being processed to avoid recursion
            HARVESTED_BLOCKS.add(blockPos);
            serverPlayer.interactionManager.tryBreakBlock(blockPos);
            HARVESTED_BLOCKS.remove(blockPos);

            // Damage the tool if not in creative
            if (!player.isCreative()) {
                mainHandStack.damage(1, player, null);
            }

            // Count power used
            powerUsed += ENERGY_PER_BLOCK;
        }

        // Deduct power from gauntlets
        if (!player.isCreative() && powerUsed > 0) {
            HextechPowerUtils.consumePower(offHandItem, powerUsed);

            // Show remaining power to the player occasionally
            if (world.getTime() % 20 == 0) {
                HextechPowerUtils.sendPowerStatusMessage(player, offHandItem);
            }
        }

        return true;
    }
}