package dev.quarris.engulfingdarkness.packets;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class FlameDataMessage {

    private final LightBringer light;
    private final int flame;

    public FlameDataMessage(LightBringer light, int flame) {
        this.light = light;
        this.flame = flame;
    }

    public static void encode(FlameDataMessage msg, FriendlyByteBuf buf) {
        buf.writeUtf(ForgeRegistries.ITEMS.getKey(msg.light.item()).toString());
        buf.writeVarInt(msg.flame);
    }

    public static FlameDataMessage decode(FriendlyByteBuf buf) {
        ResourceLocation itemId = ResourceLocation.parse(buf.readUtf());
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        LightBringer light = LightBringer.getLightBringer(item);
        int flame = buf.readVarInt();
        return new FlameDataMessage(light, flame);
    }

    public static class Handler {
        public static void handle(FlameDataMessage msg, CustomPayloadEvent.Context ctx) {
            ctx.enqueueWork(() -> Minecraft.getInstance().player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> darkness.setFlame(msg.light, msg.flame)));
            ctx.setPacketHandled(true);
        }
    }
}
