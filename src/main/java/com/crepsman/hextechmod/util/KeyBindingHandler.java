package com.crepsman.hextechmod.util;

import com.crepsman.hextechmod.item.weapons.HextechHammer;
import com.crepsman.hextechmod.item.weapons.HextechHammerBlasterMode;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindingHandler {
    private static final String CATEGORY = "key.categories.hextechmod";
    private static final String TOGGLE_BLASTER_MODE = "key.hextechmod.toggle_hextech_hammer_blaster_mode";
    private static KeyBinding toggleBlasterModeKey;

    public static void register() {
        toggleBlasterModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                TOGGLE_BLASTER_MODE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_COMMA,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleBlasterModeKey.wasPressed()) {
                if (client.player != null && client.player.getMainHandStack().getItem() instanceof HextechHammer) {
                    HextechHammer hammer = (HextechHammer) client.player.getMainHandStack().getItem();
                    hammer.toggleCrossbowMode(client.player);
                }
                else if (client.player != null && client.player.getMainHandStack().getItem() instanceof HextechHammerBlasterMode) {
                    HextechHammerBlasterMode crossbowMode = (HextechHammerBlasterMode) client.player.getMainHandStack().getItem();
                    crossbowMode.toggleHammerMode(client.player);
                }
            }
        });
    }
}