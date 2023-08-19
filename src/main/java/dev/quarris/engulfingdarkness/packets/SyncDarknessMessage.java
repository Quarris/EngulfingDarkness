package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.capability.Darkness;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncDarknessMessage {

    private final CompoundTag nbt;

    public SyncDarknessMessage(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public static void encode(SyncDarknessMessage msg, FriendlyByteBuf buf) {
        buf.writeNbt(msg.nbt);
    }

    public static SyncDarknessMessage decode(FriendlyByteBuf buf) {
        return new SyncDarknessMessage(buf.readNbt());
    }

    public static void handle(SyncDarknessMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft.getInstance().player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> {
                if (darkness instanceof Darkness d) {
                    d.deserializeNBT(msg.nbt);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
