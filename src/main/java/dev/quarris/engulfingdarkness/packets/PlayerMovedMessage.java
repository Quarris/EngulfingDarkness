package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerMovedMessage {

    public static void encode(PlayerMovedMessage msg, FriendlyByteBuf buf) {
    }

    public static PlayerMovedMessage decode(FriendlyByteBuf buf) {
        return new PlayerMovedMessage();
    }

    public static class Handler {
        public static void handle(PlayerMovedMessage msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ctx.get().getSender().removeEffect(EffectSetup.SOUL_GUARD.get());
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
