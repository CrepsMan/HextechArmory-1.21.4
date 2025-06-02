package com.crepsman.hextechmod.util;

import com.crepsman.hextechmod.HextechMod;
import com.crepsman.hextechmod.network.packet.DashC2SPacket;
import com.crepsman.hextechmod.network.packet.PowerC2SPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindingHandler {
    private static final String CATEGORY = "key.categories.hextechmod";
    private static final String EMPOWER_ITEM = "key.hextechmod.empower_item";
    private static final String DASH = "key.hextechmod.dash";

    public static KeyBinding empowerItemKey;
    public static KeyBinding dashKey;

    public static void register() {
        empowerItemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                EMPOWER_ITEM,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_E,
                CATEGORY
        ));

        dashKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                DASH,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R, // Default to R key
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (empowerItemKey.wasPressed()) {
                if (client.player != null) {
                    HextechMod.LOGGER.info("Empower item key pressed");
                    ClientPlayNetworking.send(new PowerC2SPacket());
                }
            }
        });
    }

    // Method to send the dash packet with charge time
    public static void sendDashPacket(int chargeTime) {
        ClientPlayNetworking.send(new DashC2SPacket(chargeTime));
    }
}