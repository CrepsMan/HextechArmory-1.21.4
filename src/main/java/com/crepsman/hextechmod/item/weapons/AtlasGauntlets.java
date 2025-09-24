package com.crepsman.hextechmod.item.weapons;

import com.crepsman.hextechmod.util.HextechPowerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import com.crepsman.hextechmod.item.weapons.util.GauntletBlockingHandler;


public class AtlasGauntlets extends Item {
    private float attackDamage;
    private float attackSpeed;
    private static boolean useOffhand = true;

    public AtlasGauntlets(float attackDamage, float attackSpeed, Settings settings) {
        super(settings.maxDamage(450));
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;

        // New component-based attribute system
        AttributeModifiersComponent attributeModifiers = AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes.ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                Item.BASE_ATTACK_DAMAGE_MODIFIER_ID,
                                (double)(attackDamage + 2.0F),
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes.ATTACK_SPEED,
                        new EntityAttributeModifier(
                                Item.BASE_ATTACK_SPEED_MODIFIER_ID,
                                (double)attackSpeed - 4.0,
                                EntityAttributeModifier.Operation.ADD_VALUE
                        ),
                        AttributeModifierSlot.MAINHAND
                )
                .build();

        // Apply the component to the item settings
        settings.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiers);
    }

    public static void performDash(PlayerEntity player, double dashDistance) {
        // Get player's hand items
        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();

        // Check if player has gauntlets
        ItemStack gauntletStack = null;

        boolean isMainHandGauntlet = mainHandStack.getItem() instanceof AtlasGauntlets;
        boolean isOffHandGauntlet = offHandStack.getItem() instanceof AtlasGauntlets;

        if (isMainHandGauntlet) {
            gauntletStack = mainHandStack;
        } else if (isOffHandGauntlet) {
            gauntletStack = offHandStack;
        }

        if (gauntletStack == null) {
            player.sendMessage(Text.translatable("message.hextechmod.gauntlets.need_gauntlet"), true);
            return;
        }

        if (player.isCreative() || HextechPowerUtils.hasPower(gauntletStack, 50)) {
            player.sendMessage(Text.translatable("message.hextechmod.gauntlets.dash"), true);

            // Check cooldown first - important to check before consuming power
            if (!player.isCreative() && player.getItemCooldownManager().isCoolingDown(gauntletStack)) {
                player.sendMessage(Text.translatable("message.hextechmod.gauntlets.cooldown"), true);
                return;
            }

            // Pre-dash effects and velocity calculation
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                // Initial dash sound and particles
                serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR, SoundCategory.PLAYERS, 0.6f, 1.5f);

                serverWorld.spawnParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        30, 0.3, 0.3, 0.3, 0.1
                );
            }

            player.fallDistance = 0.0F;

            // Calculate dash direction and apply velocity
            Vec3d lookDir = player.getRotationVector();
            player.setVelocity(
                    lookDir.x * dashDistance,
                    lookDir.y * dashDistance,
                    lookDir.z * dashDistance
            );
            player.velocityModified = true;

            // Consume power and apply cooldown
            if (!player.isCreative()) {
                HextechPowerUtils.consumePower(gauntletStack, 50);
                // Set cooldown using the Item from the ItemStack
                player.getItemCooldownManager().set(gauntletStack, 40);
            }
        } else {
            player.sendMessage(Text.translatable("message.hextechmod.not_enough_power"), true);
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        return GauntletBlockingHandler.handleUse(world, user, hand, stack);
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        GauntletBlockingHandler.handleOnStoppedUsing(stack, world, user, remainingUseTicks);
        // Removed super.onStoppedUsing
        return false;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        GauntletBlockingHandler.handleUsageTick(world, user, stack, remainingUseTicks);
        // Removed super.usageTick
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.empty());

        // Display power if using HextechPowerUtils
        int power = HextechPowerUtils.getPower(stack);
        if (power > 0) {
            tooltip.add(Text.translatable("Power: " + power).formatted(Formatting.GOLD));
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player && player.getWorld() instanceof ServerWorld serverWorld) {
            // Apply damage with the correct method signature: ServerWorld, DamageSource, float
            ItemStack offhandStack = player.getOffHandStack();
            target.damage(serverWorld, player.getDamageSources().generic(), this.attackDamage);
        }
        return true;
    }

    private void createShieldParticles(PlayerEntity player, ServerWorld serverWorld) {
        Vec3d pos = player.getPos().add(0, 1.0, 0);
        double radius = 1.5; // Radius of the shield
        int numPoints = 60; // More points for better coverage
        double goldenRatio = (1 + Math.sqrt(5)) / 2;

        for (int i = 0; i < numPoints; i++) {
            // Fibonacci sphere algorithm for uniform point distribution
            double y = 1 - (i / (float)(numPoints - 1)) * 2; // Range from 1 to -1
            double r = Math.sqrt(1 - y * y); // Radius at this y

            // Golden angle increment
            double phi = i * 2 * Math.PI / goldenRatio;

            // Convert to Cartesian coordinates
            double x = Math.cos(phi) * r;
            double z = Math.sin(phi) * r;

            // Scale to desired radius and position
            x = pos.x + x * radius;
            y = pos.y + y * radius;
            z = pos.z + z * radius;

            // Alternate particle types
            boolean altParticle = i % 7 == 0;

            // Make some particles brighter than others
            if (serverWorld.getRandom().nextFloat() < 0.7) {
                serverWorld.spawnParticles(
                        altParticle ? ParticleTypes.END_ROD : ParticleTypes.ELECTRIC_SPARK,
                        x, y, z,
                        1, 0.01, 0.01, 0.01, 0.005
                );
            }
        }

        // Play sound effect for the shield
        if (serverWorld.getTime() % 20 == 0) {
            serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.PLAYERS, 0.15f, 1.5f);
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        return 280;
    }

    public static boolean isWornInOffhand(PlayerEntity player) {
        ItemStack offhandStack = player.getStackInHand(Hand.OFF_HAND);
        boolean isWorn = offhandStack.getItem() instanceof AtlasGauntlets;
        return isWorn;
    }

    public static List<BlockPos> getBlocksToBeDestroyed(int range, BlockPos initalBlockPos, ServerPlayerEntity player) {
        List<BlockPos> positions = new ArrayList<>();

        // Check if Atlas Gauntlets are in the off-hand
        if (!isWornInOffhand(player)) {
            return positions;
        }

        ItemStack mainHandStack = player.getStackInHand(Hand.MAIN_HAND);
        Item mainHandItem = mainHandStack.getItem();

        // Validate the tool type
        boolean isValidTool = (
                mainHandItem == Items.DIAMOND_PICKAXE || mainHandItem == Items.NETHERITE_PICKAXE ||
                        mainHandItem == Items.DIAMOND_SHOVEL || mainHandItem == Items.NETHERITE_SHOVEL ||
                        mainHandItem == Items.DIAMOND_AXE || mainHandItem == Items.NETHERITE_AXE
        );

        if (!isValidTool) {
            return positions;
        }

        World world = player.getWorld();
        BlockState state = world.getBlockState(initalBlockPos);
        if (!mainHandStack.isSuitableFor(state)) {
            return positions;
        }

        HitResult hit = player.raycast(20, 0, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;

            if(blockHit.getSide() == Direction.DOWN || blockHit.getSide() == Direction.UP) {
                for(int x = -range; x <= range; x++) {
                    for(int y = -range; y <= range; y++) {
                        positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY(), initalBlockPos.getZ() + y));
                    }
                }
            }

            if(blockHit.getSide() == Direction.NORTH || blockHit.getSide() == Direction.SOUTH) {
                for(int x = -range; x <= range; x++) {
                    for(int y = -range; y <= range; y++) {
                        positions.add(new BlockPos(initalBlockPos.getX() + x, initalBlockPos.getY() + y, initalBlockPos.getZ()));
                    }
                }
            }

            if(blockHit.getSide() == Direction.EAST || blockHit.getSide() == Direction.WEST) {
                for(int x = -range; x <= range; x++) {
                    for(int y = -range; y <= range; y++) {
                        positions.add(new BlockPos(initalBlockPos.getX(), initalBlockPos.getY() + y, initalBlockPos.getZ() + x));
                    }
                }
            }
        }

        return positions;
    }

    public boolean isUsing(PlayerEntity player) {
        return player.isUsingItem() &&
                (player.getActiveItem().getItem() instanceof AtlasGauntlets);
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return 1.0F; // Not efficient for mining
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

    public boolean isCorrectForDrops(ItemStack stack, BlockState state) {
        return true; // Implementation needed based on your requirements
    }

    public static boolean isBlocking(PlayerEntity player) {
        return player.isUsingItem()
               && player.getActiveItem().getItem() instanceof AtlasGauntlets;
    }

    /**
     * Checks if the player is currently blocking with Atlas Gauntlets in either hand.
     */
    public static boolean isPlayerBlocking(PlayerEntity player) {
        return isBlocking(player);
    }
}
