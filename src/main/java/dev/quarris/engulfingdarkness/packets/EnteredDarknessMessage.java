package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.capability.DarknessCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class EnteredDarknessMessage {

    private boolean entered;
    public EnteredDarknessMessage(boolean entered) {
        this.entered = entered;
    }

    public static void encode(EnteredDarknessMessage msg, PacketBuffer buf) {
        buf.writeBoolean(msg.entered);
    }

    public static EnteredDarknessMessage decode(PacketBuffer buf) {
        return new EnteredDarknessMessage(buf.readBoolean());
    }

    public static void handle(EnteredDarknessMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> Minecraft.getInstance().player.getCapability(DarknessCapability.INST).ifPresent(darkness -> darkness.setInDarkness(msg.entered)));
        ctx.get().setPacketHandled(true);
    }
}
