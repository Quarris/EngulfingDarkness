package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public class PacketHandler {

    private static final String version = "1";
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(ModRef.res("channel")).simpleChannel();


    public static void init() {
        CHANNEL.messageBuilder(SyncDarknessMessage.class).encoder(SyncDarknessMessage::encode).decoder(SyncDarknessMessage::decode).consumer(SyncDarknessMessage.Handler::handle).direction(PacketFlow.CLIENTBOUND).add();
        CHANNEL.messageBuilder(SetLowLightMessage.class).encoder(SetLowLightMessage::encode).decoder(SetLowLightMessage::decode).consumer(SetLowLightMessage.Handler::handle).direction(PacketFlow.CLIENTBOUND).add();
        CHANNEL.messageBuilder(FlameDataMessage.class).encoder(FlameDataMessage::encode).decoder(FlameDataMessage::decode).consumer(FlameDataMessage.Handler::handle).direction(PacketFlow.CLIENTBOUND).add();
        CHANNEL.messageBuilder(PlayerMovedMessage.class).encoder(PlayerMovedMessage::encode).decoder(PlayerMovedMessage::decode).consumer(PlayerMovedMessage.Handler::handle).direction(PacketFlow.SERVERBOUND).add();
        CHANNEL.build();
        //CHANNEL.registerMessage(0, SyncDarknessMessage.class, SyncDarknessMessage::encode, SyncDarknessMessage::decode, SyncDarknessMessage.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //CHANNEL.registerMessage(1, SetLowLightMessage.class, SetLowLightMessage::encode, SetLowLightMessage::decode, SetLowLightMessage.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //CHANNEL.registerMessage(2, FlameDataMessage.class, FlameDataMessage::encode, FlameDataMessage::decode, FlameDataMessage.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //CHANNEL.registerMessage(3, PlayerMovedMessage.class, PlayerMovedMessage::encode, PlayerMovedMessage::decode, PlayerMovedMessage.Handler::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static <MSG> void sendTo(MSG msg, ServerPlayer player) {
        CHANNEL.send(msg, player.connection.getConnection());
    }

    public static <MSG> void sendTo(MSG msg, LocalPlayer player) {
        CHANNEL.send(msg, player.connection.getConnection());
    }

}
