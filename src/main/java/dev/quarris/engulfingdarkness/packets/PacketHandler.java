package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketHandler {

    private static final String version = "1";
    public static final SimpleChannel INST = NetworkRegistry.newSimpleChannel(
        ModRef.res("channel"),
        () -> version,
        version::equals,
        version::equals
    );

    public static void register() {
        INST.registerMessage(0, SyncDarknessMessage.class, SyncDarknessMessage::encode, SyncDarknessMessage::decode, SyncDarknessMessage.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INST.registerMessage(1, SetLowLightMessage.class, SetLowLightMessage::encode, SetLowLightMessage::decode, SetLowLightMessage.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INST.registerMessage(2, FlameDataMessage.class, FlameDataMessage::encode, FlameDataMessage::decode, FlameDataMessage.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static <MSG> void sendToClient(MSG msg, Player player) {
        INST.sendTo(msg, ((ServerPlayer) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

}
