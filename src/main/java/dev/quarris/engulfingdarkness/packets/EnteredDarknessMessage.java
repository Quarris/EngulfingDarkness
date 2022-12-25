package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EnteredDarknessMessage {

    private boolean entered;
    public EnteredDarknessMessage(boolean entered) {
        this.entered = entered;
    }

    public static void encode(EnteredDarknessMessage msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.entered);
    }

    public static EnteredDarknessMessage decode(FriendlyByteBuf buf) {
        return new EnteredDarknessMessage(buf.readBoolean());
    }

    public static void handle(EnteredDarknessMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Minecraft.getInstance().player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> darkness.setInDarkness(msg.entered)));
        ctx.get().setPacketHandled(true);
    }
}
