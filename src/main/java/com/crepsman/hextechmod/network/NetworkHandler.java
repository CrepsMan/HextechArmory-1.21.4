package com.crepsman.hextechmod.network;

import com.crepsman.hextechmod.HextechMod;
import com.crepsman.hextechmod.network.packet.DashC2SPacket;
import com.crepsman.hextechmod.network.packet.PowerC2SPacket;
import com.crepsman.hextechmod.util.HextechPowerUtils;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import com.crepsman.hextechmod.item.weapons.AtlasGauntlets;

public class NetworkHandler {
    public static void registerC2SPackets() {
        // Register the packet codec
        PayloadTypeRegistry.playC2S().register(PowerC2SPacket.PACKET_ID, PowerC2SPacket.PACKET_CODEC);
        PayloadTypeRegistry.playC2S().register(DashC2SPacket.PACKET_ID, DashC2SPacket.PACKET_CODEC);

        // Register the packet receiver
        ServerPlayNetworking.registerGlobalReceiver(PowerC2SPacket.PACKET_ID, (payload, context) -> {
            context.server().execute(() -> {
                HextechMod.LOGGER.info("PowerC2SPacket received");
                HextechPowerUtils.empowerItem(context.player());
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(DashC2SPacket.PACKET_ID, (payload, context) -> {
            context.server().execute(() -> {
                HextechMod.LOGGER.info("DashC2SPacket received with charge time: " + payload.chargeTime());

                // Calculate dash distance based on charge time
                int chargeTime = payload.chargeTime();
                double minDistance = 1.0;
                double maxDistance = 3.0;
                int maxChargeTime = 60; // 3 seconds max charge

                // Calculate dash distance based on charge time (capped at maxChargeTime)
                double dashDistance = minDistance +
                        (Math.min(chargeTime, maxChargeTime) / (double)maxChargeTime) * (maxDistance - minDistance);

                // Call the performDash method with the calculated distance
                AtlasGauntlets.performDash(context.player(), dashDistance);
            });
        });
    }

}