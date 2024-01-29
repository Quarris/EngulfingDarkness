package dev.quarris.engulfingdarkness.darkness.burnout;

import com.google.gson.JsonObject;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public abstract class BurnoutEffect<T extends BurnoutEffect.Serializer<?>> {

    public abstract void onBurnout(Player player, ItemStack stack, LightBringer lightBringer);

    public abstract T getSerializer();

    public abstract static class Serializer<T extends BurnoutEffect> {
        public abstract JsonObject serialize(T effect);

        public abstract T deserialize(JsonObject json);

        public abstract String id();
    }
}
