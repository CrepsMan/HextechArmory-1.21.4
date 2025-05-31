package com.crepsman.hextechmod.client;

public class GauntletAnimationManager {
    private static boolean isCharging = false;
    private static float chargeScale = 1.0f;
    private static boolean useRightHand = true;
    private static float attackAnimationProgress = 0.0f;
    private static long lastAnimationTime = 0;

    // Dash animation
    private static boolean isDashing = false;
    private static float dashProgress = 0.0f;
    private static long dashStartTime = 0;
    private static final long DASH_ANIMATION_DURATION = 500; // ms

    // Called when dash key is pressed
    public static void startCharging() {
        isCharging = true;
        chargeScale = 0.0f; // Start at 0 and build up
    }

    // Update during charging
    public static void updateCharge(float chargeProgress) {
        // Smooth increase from 0 to 1 as charging increases
        chargeScale = Math.min(chargeProgress, 1.0f);
    }

    // Called when dash is performed
    public static void endCharging() {
        isCharging = false;
        chargeScale = 1.0f;

        // Start dash animation
        startDashAnimation();
    }

    // Start dash animation
    public static void startDashAnimation() {
        isDashing = true;
        dashProgress = 1.0f;
        dashStartTime = System.currentTimeMillis();
    }

    // Toggle which hand attacks with smooth transition
    public static void toggleAttackingHand() {
        useRightHand = !useRightHand;
        attackAnimationProgress = 1.0f;
        lastAnimationTime = System.currentTimeMillis();
    }

    // Update all animations
    public static void updateAnimations() {
        long currentTime = System.currentTimeMillis();

        // Update attack animation
        if (attackAnimationProgress > 0) {
            long elapsed = currentTime - lastAnimationTime;
            attackAnimationProgress = Math.max(0, attackAnimationProgress - (elapsed / 250.0f));
            lastAnimationTime = currentTime;
        }

        // Update dash animation
        if (isDashing) {
            long dashElapsed = currentTime - dashStartTime;
            if (dashElapsed >= DASH_ANIMATION_DURATION) {
                isDashing = false;
                dashProgress = 0.0f;
            } else {
                dashProgress = 1.0f - (dashElapsed / (float)DASH_ANIMATION_DURATION);
            }
        }
    }

    public static float getAttackAnimationProgress() {
        return attackAnimationProgress;
    }

    public static boolean isCharging() {
        return isCharging;
    }

    public static float getChargeScale() {
        return chargeScale;
    }

    public static boolean useRightHand() {
        return useRightHand;
    }

    public static boolean isDashing() {
        return isDashing;
    }

    public static float getDashProgress() {
        return dashProgress;
    }
}