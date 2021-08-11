package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String version = "1";
    public static final SimpleChannel INST = NetworkRegistry.newSimpleChannel(
        ModRef.res("channel"),
        () -> version,
        version::equals,
        version::equals
    );

    public static void register() {
        INST.registerMessage(0, SyncDarknessMessage.class, SyncDarknessMessage::encode, SyncDarknessMessage::decode, SyncDarknessMessage::handle);
        INST.registerMessage(1, EnteredDarknessMessage.class, EnteredDarknessMessage::encode, EnteredDarknessMessage::decode, EnteredDarknessMessage::handle);
    }

    public static <MSG> void sendToClient(MSG msg, PlayerEntity player) {
        INST.sendTo(msg, ((ServerPlayerEntity) player).connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }

}
