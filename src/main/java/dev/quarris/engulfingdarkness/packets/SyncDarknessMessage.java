package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.darkness.Darkness;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

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

    public static class Handler {
        public static void handle(SyncDarknessMessage msg, CustomPayloadEvent.Context ctx) {
            ctx.enqueueWork(() -> {
                Minecraft.getInstance().player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> {
                    if (darkness instanceof Darkness d) {
                        d.deserializeNBT(Minecraft.getInstance().level.registryAccess(), msg.nbt);
                    }
                });
            });
            ctx.setPacketHandled(true);
        }
    }
}
