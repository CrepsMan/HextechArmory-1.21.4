package com.crepsman.hextechmod.item.weapons;

import com.crepsman.hextechmod.client.GauntletAnimationManager;
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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;


public class AtlasGauntlets extends BowItem {
    private float attackDamage;
    private float attackSpeed;
    private boolean blocking;
    private static final int NORMAL_COOLDOWN_TICKS = 140; // 7 seconds
    private static final int EXTENDED_COOLDOWN_TICKS = 200; // 10 seconds
    private static final int MAX_USE_TIME = 100; // 5 seconds (20 ticks/second)
    private static final int BLOCKING_POWER_COST_PER_TICK = 1; // Power cost per tick while blocking
    private static boolean useOffhand = true;
    private static final Map<UUID, Boolean> playerBlockingStates = new HashMap<>();

    public AtlasGauntlets(float attackDamage, float attackSpeed, Settings settings) {
        super(settings.maxDamage(450));
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.blocking = false;

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

    // In AtlasGauntlets.java
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        // Toggle the attacking hand for animation
        if (!user.getWorld().isClient()) {
            // Server-side logic remains the same
            useOffhand = !useOffhand;
        } else {
            // Client-side animation toggle
            GauntletAnimationManager.toggleAttackingHand();
        }

        // Rest of your existing code
        boolean hasMainHandGauntlet = user.getMainHandStack().getItem() instanceof AtlasGauntlets;
        boolean hasOffHandGauntlet = user.getOffHandStack().getItem() instanceof AtlasGauntlets;

        if (hasMainHandGauntlet && hasOffHandGauntlet) {
            user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.8f, 1.2f);
        }

        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // Check if player has gauntlets in both hands
        boolean hasMainHandGauntlet = user.getMainHandStack().getItem() instanceof AtlasGauntlets;
        boolean hasOffHandGauntlet = user.getOffHandStack().getItem() instanceof AtlasGauntlets;

        if (!(hasMainHandGauntlet && hasOffHandGauntlet)) {
            user.sendMessage(Text.translatable("message.hextechmod.gauntlets.need_dual_gauntlets"), true);
            return ActionResult.FAIL;
        }

        // Fix: Pass the ItemStack instead of 'this'
        if (user.getItemCooldownManager().isCoolingDown(stack)) {
            user.sendMessage(Text.translatable("message.hextechmod.gauntlets.cooldown"), true);
            return ActionResult.FAIL;
        }

        if (!user.isCreative() && !HextechPowerUtils.hasPower(stack, 20)) {
            user.sendMessage(Text.translatable("message.hextechmod.not_enough_power"), true);
            return ActionResult.FAIL;
        }

        playerBlockingStates.put(user.getUuid(), true);

        user.setCurrentHand(hand);
        user.sendMessage(Text.translatable("message.hextechmod.gauntlets.blocking_activated"), true);
        return ActionResult.CONSUME;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return false;
        playerBlockingStates.remove(player.getUuid());

        int useDuration = getMaxUseTime(stack) - remainingUseTicks;

        if (useDuration >= MAX_USE_TIME) {
            player.sendMessage(Text.translatable("message.hextechmod.gauntlets.extended_cooldown"), true);
            // Fix: Pass the ItemStack instead of 'this'
            player.getItemCooldownManager().set(stack, EXTENDED_COOLDOWN_TICKS);
        } else {
            player.sendMessage(Text.translatable("message.hextechmod.gauntlets.normal_cooldown"), true);
            // Fix: Pass the ItemStack instead of 'this'
            player.getItemCooldownManager().set(stack, NORMAL_COOLDOWN_TICKS / 2);
        }
        return true;
    }
    public static void performDash(PlayerEntity player, double dashDistance) {

        if (playerBlockingStates.getOrDefault(player.getUuid(), false)) {
            player.sendMessage(Text.translatable("message.hextechmod.gauntlets.cant_dash_while_blocking"), true);
            return;
        }

        // Check if player has gauntlets
        ItemStack mainHandStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack offHandStack = player.getStackInHand(Hand.OFF_HAND);

        boolean isMainHandGauntlet = mainHandStack.getItem() instanceof AtlasGauntlets;
        boolean isOffHandGauntlet = offHandStack.getItem() instanceof AtlasGauntlets;
        if (!(isMainHandGauntlet && isOffHandGauntlet)) {
            player.sendMessage(Text.translatable("message.hextechmod.gauntlets.need_dual_gauntlets"), true);
            return;
        }
        if (isMainHandGauntlet && isOffHandGauntlet) {
            // Get the gauntlets stack
            ItemStack gauntletsStack = isMainHandGauntlet ? mainHandStack : offHandStack;

            // Check cooldown first - important to check before consuming power
            if (!player.isCreative() && player.getItemCooldownManager().isCoolingDown(gauntletsStack)) {
                player.sendMessage(Text.translatable("message.hextechmod.gauntlets.cooldown"), true);
                return;
            }

            // Check for power - require 50 power to dash
            int dashCost = 50;
            if (!player.isCreative() && !HextechPowerUtils.hasPower(gauntletsStack, dashCost)) {
                player.sendMessage(Text.translatable("message.hextechmod.not_enough_power"), true);
                return;
            }

            // Pre-dash effects - visual/audio cue that dash is starting
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                // Initial dash sound
                serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR, SoundCategory.PLAYERS, 0.6f, 1.5f);

                // Initial particle burst
                serverWorld.spawnParticles(
                        ParticleTypes.ELECTRIC_SPARK,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        30, 0.3, 0.3, 0.3, 0.1
                );
            }

            // Apply Jump Boost effect with high amplifier to cancel fall damage
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 10, 255, false, false, false));

            // Reset fall distance
            player.fallDistance = 0.0F;

            // Calculate dash direction and velocity
            Vec3d lookDir = player.getRotationVector();

            // Set player velocity exactly in the look direction
            player.setVelocity(
                    lookDir.x * dashDistance,
                    lookDir.y * dashDistance,
                    lookDir.z * dashDistance
            );
            player.velocityModified = true;

            // Consume power
            if (!player.isCreative()) {
                HextechPowerUtils.consumePower(gauntletsStack, dashCost);
                player.getItemCooldownManager().set(gauntletsStack, 40); // 2 second cooldown
            }

            // Enhanced animation effects
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                // Create particle trail along the dash path
                Vec3d startPos = player.getPos();
                Vec3d endPos = startPos.add(lookDir.multiply(dashDistance));

                for (int i = 0; i < 20; i++) {
                    double progress = i / 20.0;
                    Vec3d particlePos = startPos.lerp(endPos, progress);

                    serverWorld.spawnParticles(
                            ParticleTypes.ELECTRIC_SPARK,
                            particlePos.x,
                            particlePos.y + 1.0,
                            particlePos.z,
                            5, 0.1, 0.1, 0.1, 0.05
                    );

                    // Add cloud particles for the whoosh effect
                    serverWorld.spawnParticles(
                            ParticleTypes.CLOUD,
                            particlePos.x,
                            particlePos.y + 1.0,
                            particlePos.z,
                            3, 0.1, 0.1, 0.1, 0.02
                    );
                }

                // Final burst at end position
                serverWorld.spawnParticles(
                        ParticleTypes.EXPLOSION,
                        endPos.x,
                        endPos.y + 1.0,
                        endPos.z,
                        1, 0, 0, 0, 0
                );
            }

            player.sendMessage(Text.translatable("message.hextechmod.gauntlets.dash"), true);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.empty());
        tooltip.add(Text.translatable("item.hextechmod.atlas_gauntlets.tooltip").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("item.hextechmod.atlas_gauntlets.mainhand").formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.hextechmod.atlas_gauntlets.offhand").formatted(Formatting.AQUA));
        tooltip.add(Text.translatable("item.hextechmod.atlas_gauntlets.dual").formatted(Formatting.GOLD));

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
            if (offhandStack.getItem() instanceof AtlasGauntlets) {

                // Apply 50% more damage when dual wielding
                target.damage(serverWorld, player.getDamageSources().generic(), this.attackDamage * 0.5F);
            }
        }
        return true;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        if (player.isUsingItem() && player.getActiveItem() == stack) {
            // Calculate how long the item has been used
            int usedTime = getMaxUseTime(stack) - remainingUseTicks;

            // Force stop when maximum time is reached (5 seconds)
            if (usedTime >= MAX_USE_TIME) {
                player.sendMessage(Text.translatable("message.hextechmod.gauntlets.max_blocking_time"), true);
                player.stopUsingItem(); // Force stop using
                playerBlockingStates.remove(player.getUuid());

                player.getItemCooldownManager().set(stack, EXTENDED_COOLDOWN_TICKS);
                return;
            }

            // Power consumption logic
            if (!player.isCreative()) {
                if (HextechPowerUtils.hasPower(stack, BLOCKING_POWER_COST_PER_TICK)) {
                    HextechPowerUtils.consumePower(stack, BLOCKING_POWER_COST_PER_TICK);

                    if (world instanceof ServerWorld serverWorld && world.getTime() % 2 == 0) {
                        createShieldParticles(player, serverWorld);
                    }
                } else {
                    player.sendMessage(Text.translatable("message.hextechmod.not_enough_power"), true);
                    player.getItemCooldownManager().set(stack, NORMAL_COOLDOWN_TICKS / 2);
                    player.stopUsingItem();
                    return;
                }
            }
        }
    }

    // Add helper method to check if a player is blocking
    public static boolean isPlayerBlocking(PlayerEntity player) {
        return playerBlockingStates.getOrDefault(player.getUuid(), false);
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
        return MAX_USE_TIME; // 5 seconds (100 ticks)
    }

    public static boolean isWornInOffhand(PlayerEntity player) {
        ItemStack offhandStack = player.getStackInHand(Hand.OFF_HAND);
        boolean isWorn = offhandStack.getItem() instanceof AtlasGauntlets;
        return isWorn;
    }

    public static void enhancePickaxe(PlayerEntity player, World world, BlockPos pos, Direction side) {
        if (isWornInOffhand(player)) {
            ItemStack mainHandStack = player.getStackInHand(Hand.MAIN_HAND);
            if (mainHandStack.getItem() == Items.DIAMOND_PICKAXE || mainHandStack.getItem() == Items.NETHERITE_PICKAXE) {
                List<BlockPos> blocksToBeDestroyed = getBlocksToBeDestroyed(1, pos, (ServerPlayerEntity) player);
                blocksToBeDestroyed.forEach(blockPos -> {
                    boolean success = world.breakBlock(blockPos, true, player);
                });
            }
        }
    }

    public static List<BlockPos> getBlocksToBeDestroyed(int range, BlockPos initialBlockPos, ServerPlayerEntity player) {
        List<BlockPos> positions = new ArrayList<>();
        HitResult hit = player.raycast(20, 0, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;

            if (blockHit.getSide() == Direction.DOWN || blockHit.getSide() == Direction.UP) {
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        positions.add(new BlockPos(initialBlockPos.getX() + x, initialBlockPos.getY(), initialBlockPos.getZ() + y));
                    }
                }
            }

            if (blockHit.getSide() == Direction.NORTH || blockHit.getSide() == Direction.SOUTH) {
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        positions.add(new BlockPos(initialBlockPos.getX() + x, initialBlockPos.getY() + y, initialBlockPos.getZ()));
                    }
                }
            }

            if (blockHit.getSide() == Direction.EAST || blockHit.getSide() == Direction.WEST) {
                for (int x = -range; x <= range; x++) {
                    for (int y = -range; y <= range; y++) {
                        positions.add(new BlockPos(initialBlockPos.getX(), initialBlockPos.getY() + y, initialBlockPos.getZ() + x));
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

    public boolean isBlocking() {
        return blocking;
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
}