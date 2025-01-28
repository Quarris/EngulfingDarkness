package dev.quarris.engulfingdarkness.darkness.burnout;

import com.google.gson.JsonObject;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class GiveItem extends BurnoutEffect<GiveItem.Serializer> {

    public static final GiveItem.Serializer SERIALIZER = new GiveItem.Serializer();

    private final Item item;
    private final int amount;

    public GiveItem(Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    @Override
    public void onBurnout(Player player, ItemStack stack, LightBringer lightBringer) {
        if (player instanceof ServerPlayer serverPlayer) {
            ItemStack toGive = new ItemStack(this.item, this.amount);
            if (!serverPlayer.addItem(toGive)) {
                serverPlayer.spawnAtLocation(serverPlayer.serverLevel(), toGive);
            }
        }
    }

    @Override
    public GiveItem.Serializer getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends BurnoutEffect.Serializer<GiveItem> {

        @Override
        public JsonObject serialize(GiveItem effect) {
            JsonObject json = new JsonObject();
            json.addProperty("item", ForgeRegistries.ITEMS.getKey(effect.item).toString());
            json.addProperty("amount", effect.amount);
            return json;
        }

        @Override
        public GiveItem deserialize(JsonObject json) {
            Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(json.get("item").getAsString()));
            int amount = json.get("amount").getAsInt();
            return new GiveItem(item, amount);
        }

        @Override
        public String id() {
            return "give_item";
        }
    }
}
