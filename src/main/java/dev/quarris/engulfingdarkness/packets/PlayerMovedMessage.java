package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class PlayerMovedMessage {

    public static void encode(PlayerMovedMessage msg, FriendlyByteBuf buf) {
    }

    public static PlayerMovedMessage decode(FriendlyByteBuf buf) {
        return new PlayerMovedMessage();
    }

    public static class Handler {
        public static void handle(PlayerMovedMessage msg, CustomPayloadEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                ctx.getSender().removeEffect(EffectSetup.SOUL_GUARD.getHolder().get());
            });
            ctx.setPacketHandled(true);
        }
    }

}
