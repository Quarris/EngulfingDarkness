package dev.quarris.engulfingdarkness.darkness.burnout;

import com.google.gson.JsonObject;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ApplyPotionEffect extends BurnoutEffect<ApplyPotionEffect.Serializer> {

    public static final ApplyPotionEffect.Serializer SERIALIZER = new ApplyPotionEffect.Serializer();

    private final MobEffect effect;
    private final int duration;
    private final int amplifier;
    private final boolean hidden;

    public ApplyPotionEffect(MobEffect effect, int duration, int amplifier, boolean hidden) {
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.hidden = hidden;
    }

    @Override
    public void onBurnout(Player player, ItemStack stack, LightBringer lightBringer) {
        player.addEffect(new MobEffectInstance(this.effect, this.duration, this.amplifier, false, !this.hidden, !this.hidden));
    }

    @Override
    public ApplyPotionEffect.Serializer getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends BurnoutEffect.Serializer<ApplyPotionEffect> {

        @Override
        public JsonObject serialize(ApplyPotionEffect effect) {
            JsonObject json = new JsonObject();
            json.addProperty("effect", ForgeRegistries.MOB_EFFECTS.getKey(effect.effect).toString());
            json.addProperty("duration", effect.duration);
            json.addProperty("amplifier", effect.amplifier);
            json.addProperty("hidden", effect.hidden);
            return json;
        }

        @Override
        public ApplyPotionEffect deserialize(JsonObject json) {
            MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(json.get("effect").getAsString()));
            int duration = json.get("duration").getAsInt();
            int amplifier = json.get("amplifier").getAsInt();
            boolean hidden = json.get("hidden").getAsBoolean();
            return new ApplyPotionEffect(mobEffect, duration, amplifier, hidden);
        }

        @Override
        public String id() {
            return "apply_effect";
        }
    }
}
