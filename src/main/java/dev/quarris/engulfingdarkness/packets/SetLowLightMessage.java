package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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
        public static void handle(SetLowLightMessage msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> Minecraft.getInstance().player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> darkness.setInLowLight(msg.isInLowLight)));
            ctx.get().setPacketHandled(true);
        }
    }
}
