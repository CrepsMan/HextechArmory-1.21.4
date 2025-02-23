package com.crepsman.hextechmod.network;

import com.crepsman.hextechmod.network.packet.HammerC2SPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class NetworkHandler {
    public static final Identifier EMPOWER_PACKET_ID = Identifier.of("hextechmod", "empower_hammer");

    public static void registerC2SPackets() {
        PayloadTypeRegistry.playC2S().register(HammerC2SPacket.PACKET_ID, HammerC2SPacket.PACKET_CODEC);
    }

    public static void registerS2CPackets() {
        // Register S2C packets here if needed
    }
}