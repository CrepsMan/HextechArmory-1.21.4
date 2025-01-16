package com.crepsman.hextechmod.item.weapons;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HextechHammer extends MiningToolItem {
    private static final int MAX_BLOCKS = 150;
    private static final Map<Block, Block> Exlode_Map =
            Map.of(
                    Blocks.DIRT,
                    Blocks.SAND

            );

    public HextechHammer(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, BlockTags.AXE_MINEABLE, attackDamage, attackSpeed, settings);
    }


    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        System.out.println("postMine called"); // Debug statement
        if (!world.isClient && miner instanceof PlayerEntity && !miner.isSneaking() && state.isIn(BlockTags.LOGS)) {
            System.out.println("Chopping tree"); // Debug statement
            chopTree(world, pos, (PlayerEntity) miner);
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        Block clickedBlock = world.getBlockState(context.getBlockPos()).getBlock();

        if (Exlode_Map.containsKey(clickedBlock)) {
            if (!world.isClient) {
                explodeBlock(world, context);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void explodeBlock(World world, ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0F, World.ExplosionSourceType.TNT);
    }

    private void chopTree(World world, BlockPos pos, PlayerEntity player) {
        Set<BlockPos> visited = new HashSet<>();
        chopTreeRecursive(world, pos, player, visited, 0);
    }

    private void chopTreeRecursive(World world, BlockPos pos, PlayerEntity player, Set<BlockPos> visited, int blocksMined) {
        visited.add(pos);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        System.out.println("Visiting: " + pos + " Block: " + block); // Debug statement

        System.out.println("Breaking block at: " + pos); // Debug statement
        world.breakBlock(pos, true, player);
        blocksMined++;

        for (BlockPos offset : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (blocksMined >= MAX_BLOCKS) {
                break;
            }
            if (!offset.equals(pos) && !visited.contains(offset) && (world.getBlockState(offset).isIn(BlockTags.LOGS) || world.getBlockState(offset).isIn(BlockTags.LEAVES))) {
                chopTreeRecursive(world, offset, player, visited, blocksMined);
            }
        }
    }
}