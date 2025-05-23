package com.crepsman.hextechmod.util;

import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class GauntlestsUsageEvent implements PlayerBlockBreakEvents.Before {
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();

    @Override
    public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos,
                                    BlockState state, @Nullable BlockEntity blockEntity) {
        ItemStack offHandItem = player.getOffHandStack();

        if (offHandItem.getItem() instanceof AtlasGauntlets gauntlets && player instanceof ServerPlayerEntity serverPlayer) {
            if (HARVESTED_BLOCKS.contains(pos)) {
                return true;
            }

            AtlasGauntlets.enhancePickaxe(player, world, pos, null);

            for (BlockPos position : AtlasGauntlets.getBlocksToBeDestroyed(1, pos, serverPlayer)) {
                if (pos == position || !gauntlets.isCorrectForDrops(offHandItem, world.getBlockState(position))) {
                    continue;
                }

                HARVESTED_BLOCKS.add(position);
                serverPlayer.interactionManager.tryBreakBlock(position);
                HARVESTED_BLOCKS.remove(position);
            }
        }

        return true;
    }
}