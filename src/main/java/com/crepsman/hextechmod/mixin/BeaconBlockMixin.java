package com.crepsman.hextechmod.mixin;

import com.crepsman.hextechmod.HextechMod;
import com.crepsman.hextechmod.item.ModItems;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeaconBlock.class)
public class BeaconBlockMixin {
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        HextechMod.LOGGER.info("BeaconBlockMixin onUse called");
        if (!world.isClient && player.isSneaking()) {
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
            if (stack.getItem() == ModItems.HEXTECH_CRYSTAL) {
                BeaconBlockEntity beacon = (BeaconBlockEntity)world.getBlockEntity(pos);
                if (beacon == null) {
                    HextechMod.LOGGER.warn("Beacon entity is null!");
                    return;
                }

                BeaconBlockEntityInterface beaconInterface = (BeaconBlockEntityInterface)beacon;
                int currentCrystals = beaconInterface.getHextechCrystals();
                HextechMod.LOGGER.info("Current crystals: {}", currentCrystals);

                if (currentCrystals < 10) {
                    beaconInterface.setHextechCrystals(currentCrystals + 1);
                    stack.decrement(1);
                    HextechMod.LOGGER.info("Added crystal, new count: {}", beaconInterface.getHextechCrystals());

                    // Effects
                    world.playSound(null, pos, SoundEvents.BLOCK_BEACON_POWER_SELECT,
                            SoundCategory.BLOCKS, 1.0f, 1.0f);

                    if (world instanceof ServerWorld serverWorld) {
                        for (int i = 0; i < 50; i++) {
                            double x = pos.getX() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
                            double y = pos.getY() + 1.0 + world.random.nextDouble() * 2.0;
                            double z = pos.getZ() + 0.5 + (world.random.nextDouble() - 0.5) * 2.0;
                            serverWorld.spawnParticles(ParticleTypes.END_ROD, x, y, z,
                                    1, 0, 0.1, 0, 0.1);
                        }
                    }

                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
    }
}