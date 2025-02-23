package com.crepsman.hextechmod.util;

import com.crepsman.hextechmod.HextechMod;
import com.crepsman.hextechmod.network.NetworkHandler;
import com.crepsman.hextechmod.network.packet.HammerC2SPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class KeyBindingHandler {
    private static final String CATEGORY = "key.categories.hextechmod";
    private static final String EMPOWER_ITEM = "key.hextechmod.empower_item";
    public static KeyBinding empowerItemKey;

    public static void register() {
        empowerItemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                EMPOWER_ITEM,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_E,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (empowerItemKey.wasPressed()) {
                if (client.player != null) {
                    HextechMod.LOGGER.info("Empower item key pressed");
                    ClientPlayNetworking.send(new HammerC2SPacket());
                }
            }
        });
    }
}