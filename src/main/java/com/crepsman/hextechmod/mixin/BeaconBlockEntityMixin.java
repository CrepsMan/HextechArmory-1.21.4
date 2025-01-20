package com.crepsman.hextechmod.mixin;

import com.crepsman.hextechmod.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private static void onTick(World world, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity, CallbackInfo ci) {
        if (!world.isClient) {
            BlockPos abovePos = pos.up();
            for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, new Box(abovePos), item -> true)) {
                ItemStack itemStack = itemEntity.getStack();
                if (itemStack.getItem() == ModItems.UNREFINED_HEXTECH_CRYSTAL) {
                    itemEntity.discard();
                    ItemStack hextechCrystal = new ItemStack(ModItems.HEXTECH_CRYSTAL);
                    ItemEntity newItemEntity = new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), hextechCrystal);
                    world.spawnEntity(newItemEntity);

                    // Play sound effect
                    world.playSound(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, itemEntity.getSoundCategory(), 1.0F, 1.0F);

                    // Enhanced particle explosion effect
// Triggered when an item is dropped on a beacon, turning into a Hextech crystal

// Particle explosion settings
                    // Ensure particles are client-side
                    if (world.isClient) {
                        // Exploding particles
                        for (int i = 0; i < 500; i++) {
                            double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
                            double offsetY = world.random.nextDouble() * 1.5;
                            double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;
                            double velocityX = offsetX * 0.1;
                            double velocityY = offsetY * 0.1;
                            double velocityZ = offsetZ * 0.1;

                            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                                    itemEntity.getX() + offsetX,
                                    itemEntity.getY() + offsetY,
                                    itemEntity.getZ() + offsetZ,
                                    velocityX, velocityY, velocityZ);
                        }

                        // Ring pattern
                        for (int i = 0; i < 360; i += 10) {
                            double angle = Math.toRadians(i);
                            double radius = 2.0;
                            double ringX = Math.cos(angle) * radius;
                            double ringZ = Math.sin(angle) * radius;

                            world.addParticle(ParticleTypes.ENCHANTED_HIT,
                                    itemEntity.getX() + ringX,
                                    itemEntity.getY() + 1.0,
                                    itemEntity.getZ() + ringZ,
                                    0, 0.1, 0);
                        }
                    }


// Console feedback for debugging
                    System.out.println("Hextech crystal transformation triggered. Particle explosion generated.");
                }
            }
        }
    }
}