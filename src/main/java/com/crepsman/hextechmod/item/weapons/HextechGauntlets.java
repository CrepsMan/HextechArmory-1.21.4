package com.crepsman.hextechmod.item.weapons;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class HextechGauntlets extends Item {
    private float attackDamage = 0;
    private float attackSpeed = 0;
    private boolean blocking;

    public HextechGauntlets( Settings settings) {
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
        return offhandStack.getItem() instanceof HextechGauntlets;
    }

    public static void enhancePickaxe(PlayerEntity player, World world, BlockPos pos, Direction side) {
        if (isWornInOffhand(player)) {
            ItemStack mainHandStack = player.getStackInHand(Hand.MAIN_HAND);
            if (mainHandStack.getItem() instanceof MiningToolItem) {
                mine3x3(world, pos, side, player, Hand.MAIN_HAND);

                // Simulate vein mining
                BlockState state = world.getBlockState(pos);
                if (isOreBlock(state)) {
                    mineVein(world, pos, player, Hand.MAIN_HAND, new HashSet<>());
                }
            }
        }
    }

    private static void mine3x3(World world, BlockPos pos, Direction side, PlayerEntity player, Hand hand) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos targetPos = pos.add(dx, dy, dz);
                    BlockState targetState = world.getBlockState(targetPos);
                    if (targetState.isIn(BlockTags.PICKAXE_MINEABLE)) {
                        world.breakBlock(targetPos, true, player);
                    }
                }
            }
        }
    }

    private static void mineVein(World world, BlockPos pos, PlayerEntity player, Hand hand, Set<BlockPos> visited) {
        if (visited.contains(pos)) {
            return;
        }

        visited.add(pos);
        BlockState state = world.getBlockState(pos);
        if (isOreBlock(state)) {
            world.breakBlock(pos, true, player);
            for (BlockPos offset : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
                mineVein(world, offset, player, hand, visited);
            }
        }
    }

    private static boolean isOreBlock(BlockState state) {
        return state.isIn(BlockTags.IRON_ORES) ||
                state.isIn(BlockTags.DIAMOND_ORES) ||
                state.isIn(BlockTags.REDSTONE_ORES) ||
                state.isIn(BlockTags.LAPIS_ORES) ||
                state.isIn(BlockTags.COAL_ORES) ||
                state.isIn(BlockTags.EMERALD_ORES) ||
                state.isIn(BlockTags.COPPER_ORES);
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
