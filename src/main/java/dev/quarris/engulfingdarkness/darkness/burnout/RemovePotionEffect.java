package dev.quarris.engulfingdarkness.darkness.burnout;

import com.google.gson.JsonObject;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class RemovePotionEffect extends BurnoutEffect<RemovePotionEffect.Serializer> {

    public static final RemovePotionEffect.Serializer SERIALIZER = new RemovePotionEffect.Serializer();

    private final MobEffect effect;
    public final boolean removeAll;

    public RemovePotionEffect(MobEffect effect, boolean removeAll) {
        this.effect = effect;
        this.removeAll = removeAll;
    }

    @Override
    public void onBurnout(Player player, ItemStack stack, LightBringer lightBringer) {
        if (this.removeAll) {
            player.removeAllEffects();
        } else if (this.effect != null) {
            player.removeEffect(this.effect);
        }
    }

    @Override
    public RemovePotionEffect.Serializer getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends BurnoutEffect.Serializer<RemovePotionEffect> {

        @Override
        public JsonObject serialize(RemovePotionEffect effect) {
            JsonObject json = new JsonObject();
            if (effect.effect != null) {
                json.addProperty("effect", ForgeRegistries.MOB_EFFECTS.getKey(effect.effect).toString());
            }

            if (effect.removeAll) {
                json.addProperty("removeAll", true);
            }

            return json;
        }

        @Override
        public RemovePotionEffect deserialize(JsonObject json) {
            MobEffect effect = json.has("effect") ? ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(json.get("effect").getAsString())) : null;
            boolean removeAll = json.has("removeAll") && json.get("removeAll").getAsBoolean();
            return new RemovePotionEffect(effect, removeAll);
        }

        @Override
        public String id() {
            return "remove_effect";
        }
    }
}
