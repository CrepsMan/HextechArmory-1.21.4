package com.crepsman.hextechmod.item.weapons;

import com.crepsman.hextechmod.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class HextechHammer extends MiningToolItem {
    private static final int MAX_BLOCKS = 150;
    private boolean isCrossbowMode = false;

    public HextechHammer(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, BlockTags.AXE_MINEABLE, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (isCrossbowMode) {
            return false; // Do not mine in crossbow mode
        }

        if (!world.isClient && miner instanceof PlayerEntity && !miner.isSneaking() && state.isIn(BlockTags.LOGS)) {
            chopTree(world, pos, (PlayerEntity) miner);
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (isCrossbowMode) {
            return false; // Do not deal melee damage in crossbow mode
        }
        return super.postHit(stack, target, attacker);
    }

    private void chopTree(World world, BlockPos pos, PlayerEntity player) {
        Set<BlockPos> visited = new HashSet<>();
        chopTreeRecursive(world, pos, player, visited, 0);
    }

    private void chopTreeRecursive(World world, BlockPos pos, PlayerEntity player, Set<BlockPos> visited, int blocksMined) {
        if (blocksMined >= MAX_BLOCKS) {
            return;
        }

        visited.add(pos);
        BlockState state = world.getBlockState(pos);

        if (state.isIn(BlockTags.LOGS)) {
            world.breakBlock(pos, true, player);
            blocksMined++;
        }

        for (BlockPos offset : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (!offset.equals(pos) && !visited.contains(offset)) {
                BlockState offsetState = world.getBlockState(offset);
                if (offsetState.isIn(BlockTags.LOGS)) {
                    chopTreeRecursive(world, offset, player, visited, blocksMined);
                }
            }
        }
    }

    public void toggleCrossbowMode(PlayerEntity player) {
        player.sendMessage(Text.literal("Hextech Hammer, blaster mode enabled"), true);
        player.setStackInHand(Hand.MAIN_HAND, new ItemStack(ModItems.HEXTECH_HAMMER_BLASTER_MODE));
    }
}