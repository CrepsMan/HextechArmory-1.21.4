package com.crepsman.hextechmod.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DashC2SPacket(int chargeTime) implements CustomPayload {
    public static final CustomPayload.Id<DashC2SPacket> PACKET_ID =
            new CustomPayload.Id<>(Identifier.of("hextechmod", "dash"));

    public static final PacketCodec<RegistryByteBuf, DashC2SPacket> PACKET_CODEC =
            new PacketCodec<>() {
                @Override
                public void encode(RegistryByteBuf buf, DashC2SPacket value) {
                    buf.writeInt(value.chargeTime());
                }

                @Override
                public DashC2SPacket decode(RegistryByteBuf buf) {
                    return new DashC2SPacket(buf.readInt());
                }

            };

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}