package com.crepsman.hextechmod.network.packet;

import com.crepsman.hextechmod.HextechMod;
import com.crepsman.hextechmod.item.weapons.HextechHammer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PowerC2SPacket() implements CustomPayload {
    public static final CustomPayload.Id<PowerC2SPacket> PACKET_ID =
            new CustomPayload.Id<>(Identifier.of("hextechmod", "empower_hammer"));

    // Simple codec since this is a singleton packet (no data)
    public static final PacketCodec<RegistryByteBuf, PowerC2SPacket> PACKET_CODEC =
            PacketCodec.unit(new PowerC2SPacket());

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}