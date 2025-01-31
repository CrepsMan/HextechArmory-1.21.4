package com.crepsman.hextechmod.item.weapons;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class HextechHammer extends MiningToolItem implements Inventory {
    private static final int MAX_BLOCKS_LEVEL_1 = 150;
    //private boolean isCrossbowMode = false;

    public HextechHammer(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, BlockTags.AXE_MINEABLE, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && miner instanceof PlayerEntity && !miner.isSneaking() && state.isIn(BlockTags.LOGS)) {
            chopTree(world, pos, (PlayerEntity) miner);
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return super.postHit(stack, target, attacker);
    }

    private void chopTree(World world, BlockPos pos, PlayerEntity player) {
        Set<BlockPos> visited = new HashSet<>();
        chopTreeRecursive(world, pos, player, visited, 0, HextechHammer.MAX_BLOCKS_LEVEL_1);
    }

    private void chopTreeRecursive(World world, BlockPos pos, PlayerEntity player, Set<BlockPos> visited, int blocksMined, int MAX_BLOCKS_LEVEL_1) {
        if (blocksMined >= MAX_BLOCKS_LEVEL_1) {
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
                    chopTreeRecursive(world, offset, player, visited, blocksMined, MAX_BLOCKS_LEVEL_1);
                }
            }
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getStack(int slot) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return null;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {

    }
}