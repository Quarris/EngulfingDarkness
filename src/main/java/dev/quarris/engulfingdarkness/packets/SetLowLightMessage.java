package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SetLowLightMessage {

    private boolean isInLowLight;

    public SetLowLightMessage(boolean isInLowLight) {
        this.isInLowLight = isInLowLight;
    }

    public static void encode(SetLowLightMessage msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isInLowLight);
    }

    public static SetLowLightMessage decode(FriendlyByteBuf buf) {
        return new SetLowLightMessage(buf.readBoolean());
    }

    public static class Handler {
        public static void handle(SetLowLightMessage msg, CustomPayloadEvent.Context ctx) {
            ctx.enqueueWork(() -> Minecraft.getInstance().player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> darkness.setInLowLight(msg.isInLowLight)));
            ctx.setPacketHandled(true);
        }
    }
}
