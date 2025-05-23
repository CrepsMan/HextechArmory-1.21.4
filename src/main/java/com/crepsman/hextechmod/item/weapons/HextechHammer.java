package com.crepsman.hextechmod.item.weapons;

import com.crepsman.hextechmod.item.ModItems;
import com.crepsman.hextechmod.component.ModDataComponentTypes;
import com.crepsman.hextechmod.util.HextechPowerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HextechHammer extends MiningToolItem {
    private static final int MAX_BLOCKS = 200;
    private static final int TREE_CHOP_COST = 100;
    /*private static final int MAX_POWER = 50000;
    private static final int CRYSTAL_POWER = 10000;*/

    public HextechHammer(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, BlockTags.AXE_MINEABLE, attackDamage, attackSpeed, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int power = HextechPowerUtils.getPower(stack);
        tooltip.add(Text.translatable("Power: " + power).formatted(Formatting.GOLD));
    }

    /*public static void setPower(ItemStack stack, int power) {
        stack.set(ModDataComponentTypes.HEXTECH_POWER, Math.min(power, MAX_POWER));
    }

    public static int getPower(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.HEXTECH_POWER, 0);
    }*/
    /*
    // Replace the empowerItem method:
    public static void empowerItem(PlayerEntity player) {
        if (player.getWorld().isClient) return; // Server-side only

        ItemStack heldStack = player.getMainHandStack();
        if (!(heldStack.getItem() instanceof HextechHammer)) {
            player.sendMessage(Text.translatable("message.hextechmod.no_item_held"), true);
            return;
        }

        int currentPower = HextechPowerUtils.getPower(heldStack);
        if (currentPower >= HextechPowerUtils.MAX_POWER) {
            player.sendMessage(Text.translatable("message.hextechmod.max_power"), true);
            return;
        }

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ModItems.HEXTECH_CRYSTAL)) {
                stack.decrement(1);
                HextechPowerUtils.addPower(heldStack, HextechPowerUtils.CRYSTAL_RECHARGE_AMOUNT);
                HextechPowerUtils.sendPowerStatusMessage(player, heldStack);
                player.playerScreenHandler.sendContentUpdates();
                return;
            }
        }

        player.sendMessage(Text.translatable("message.hextechmod.no_crystals"), true);
    }*/

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (miner instanceof PlayerEntity player && HextechPowerUtils.hasPower(stack, TREE_CHOP_COST)) {
            chopTree(world, pos, player, MAX_BLOCKS);
            HextechPowerUtils.consumePower(stack, TREE_CHOP_COST);
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    private void chopTree(World world, BlockPos pos, PlayerEntity player, int maxBlocks) {
        Set<BlockPos> visited = new HashSet<>();
        chopTreeRecursive(world, pos, player, visited, 0, maxBlocks);
    }

    private void chopTreeRecursive(World world, BlockPos pos, PlayerEntity player, Set<BlockPos> visited, int blocksMined, int maxBlocks) {
        if (blocksMined >= maxBlocks || visited.contains(pos)) return;

        visited.add(pos);
        BlockState state = world.getBlockState(pos);

        if (state.isIn(BlockTags.LOGS)) {
            world.breakBlock(pos, true, player);
            blocksMined++;
        }

        for (BlockPos offset : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (!visited.contains(offset) && world.getBlockState(offset).isIn(BlockTags.LOGS)) {
                chopTreeRecursive(world, offset, player, visited, blocksMined, maxBlocks);
            }
        }
    }
}
