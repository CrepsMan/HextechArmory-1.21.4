package com.crepsman.hextechmod.network.packet;

import com.crepsman.hextechmod.item.weapons.HextechHammer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


public record HammerC2SPacket() implements CustomPayload {
    public static final CustomPayload.Id<HammerC2SPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("hextechmod", "empower_hammer"));
    public static final PacketCodec<RegistryByteBuf, HammerC2SPacket> PACKET_CODEC = PacketCodec.unit(new HammerC2SPacket());

    public static void receive(MinecraftServer server, ServerPlayerEntity player, PacketSender responseSender, PacketByteBuf buf, ServerPlayNetworkHandler handler) {
        // Handle the packet here
        HextechHammer.empowerItem(player);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}