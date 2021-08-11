package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.capability.DarknessCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDarknessMessage {

    private final CompoundNBT nbt;

    public SyncDarknessMessage(CompoundNBT nbt) {
        this.nbt = nbt;
    }

    public static void encode(SyncDarknessMessage msg, PacketBuffer buf) {
        buf.writeCompoundTag(msg.nbt);
    }

    public static SyncDarknessMessage decode(PacketBuffer buf) {
        return new SyncDarknessMessage(buf.readCompoundTag());
    }

    public static void handle(SyncDarknessMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft.getInstance().player.getCapability(DarknessCapability.INST).ifPresent(darkness -> {
                darkness.deserializeNBT(msg.nbt);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
