package dev.quarris.engulfingdarkness.darkness.burnout;

import com.google.gson.JsonObject;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PlaySound extends BurnoutEffect<PlaySound.Serializer> {

    public static final PlaySound.Serializer SERIALIZER = new PlaySound.Serializer();

    private final SoundEvent sound;
    private final float volume;
    private final float pitch;

    public PlaySound(SoundEvent sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void onBurnout(Player player, ItemStack stack, LightBringer lightBringer) {
        player.level.playSound(null, player, this.sound, SoundSource.PLAYERS, this.volume, this.pitch);
    }

    @Override
    public PlaySound.Serializer getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends BurnoutEffect.Serializer<PlaySound> {

        @Override
        public JsonObject serialize(PlaySound effect) {
            JsonObject json = new JsonObject();
            json.addProperty("sound", ForgeRegistries.SOUND_EVENTS.getKey(effect.sound).toString());
            json.addProperty("volume", effect.volume);
            json.addProperty("pitch", effect.pitch);
            return json;
        }

        @Override
        public PlaySound deserialize(JsonObject json) {
            SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(json.get("sound").getAsString()));
            float volume = json.get("volume").getAsInt();
            float pitch = json.get("pitch").getAsInt();
            return new PlaySound(sound, volume, pitch);
        }

        @Override
        public String id() {
            return "play_sound";
        }
    }
}
