package dev.quarris.engulfingdarkness.configs;

import com.google.common.collect.Lists;
import com.google.gson.*;
import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import dev.quarris.engulfingdarkness.darkness.burnout.*;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlameConfigs {

    public static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve(ModRef.ID);
    public static final Path DEFAULTS_PATH = CONFIG_PATH.resolve("defaults");
    public static final Path FLAME_DATA_PATH = CONFIG_PATH.resolve("flame_data");
    public static final Path README_PATH = CONFIG_PATH.resolve("readme.txt");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final Map<Item, LightBringer> LIGHT_BRINGERS = new HashMap<>();

    public static final Map<String, LightBringer> DEFAULT_LIGHTBRINGERS = new HashMap<>();
    public static final Map<String, BurnoutEffect.Serializer<?>> BURNOUT_EFFECTS = new HashMap<>();

    private static void initializeDefaults() {
        BURNOUT_EFFECTS.put(ApplyPotionEffect.SERIALIZER.id(), ApplyPotionEffect.SERIALIZER);
        BURNOUT_EFFECTS.put(GiveItem.SERIALIZER.id(), GiveItem.SERIALIZER);
        BURNOUT_EFFECTS.put(PlaySound.SERIALIZER.id(), PlaySound.SERIALIZER);
        BURNOUT_EFFECTS.put(RemovePotionEffect.SERIALIZER.id(), RemovePotionEffect.SERIALIZER);
        BURNOUT_EFFECTS.put(RemoveCurseEffect.SERIALIZER.id(), RemoveCurseEffect.SERIALIZER);

        DEFAULT_LIGHTBRINGERS.put("torch", new LightBringer(Items.TORCH, 64000, 200, 2.0f, 8.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("candle", new LightBringer(Items.CANDLE, 64000, 200, 2.0f, 8.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("ochre_froglight", new LightBringer(Items.OCHRE_FROGLIGHT, 64000, 35, 1.0f, 1.0f, Lists.newArrayList(new ApplyPotionEffect(MobEffects.HEAL, 20, 0, true), new PlaySound(SoundEvents.FROGLIGHT_BREAK, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("pearlescent_froglight", new LightBringer(Items.PEARLESCENT_FROGLIGHT, 64000, 35, 1.0f, 1.0f, Lists.newArrayList(new ApplyPotionEffect(MobEffects.HEAL, 20, 0, true), new PlaySound(SoundEvents.FROGLIGHT_BREAK, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("verdant_froglight", new LightBringer(Items.VERDANT_FROGLIGHT, 64000, 35, 1.0f, 1.0f, Lists.newArrayList(new ApplyPotionEffect(MobEffects.HEAL, 20, 0, true), new PlaySound(SoundEvents.FROGLIGHT_BREAK, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("glowstone", new LightBringer(Items.GLOWSTONE, 64000, 100, 1.0f, 1.0f, Lists.newArrayList(new GiveItem(Items.GLOWSTONE_DUST, 1), new PlaySound(SoundEvents.GLASS_BREAK, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("jack_o_lantern", new LightBringer(Items.JACK_O_LANTERN, 64000, 175, 1.5f, 7.0f, Lists.newArrayList(new PlaySound(SoundEvents.WOOD_BREAK, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("lantern", new LightBringer(Items.LANTERN, 64000, 125, 1.0f, 5.0f, Lists.newArrayList(new PlaySound(SoundEvents.LANTERN_BREAK, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("lava_bucket", new LightBringer(Items.LAVA_BUCKET, 64000, 40, 2.0f, 100.0f, Lists.newArrayList(new GiveItem(Items.BUCKET, 1), new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(EffectSetup.EASY_TARGET.get(), 2400, 0, false))));
        DEFAULT_LIGHTBRINGERS.put("campfire", new LightBringer(Items.CAMPFIRE, 64000, 150, 1.2f, 8.0f, Lists.newArrayList(new GiveItem(Items.CHARCOAL, 1), new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("sea_lantern", new LightBringer(Items.SEA_LANTERN, 64000, 150, 0.8f, 0.5f, Lists.newArrayList(new GiveItem(Items.PRISMARINE_SHARD, 1), new PlaySound(SoundEvents.GLASS_BREAK, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("shroomlight", new LightBringer(Items.SHROOMLIGHT, 64000, 150, 1f, 1f, Lists.newArrayList(new PlaySound(SoundEvents.SHROOMLIGHT_BREAK, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("end_rod", new LightBringer(Items.END_ROD, 64000, 200, 1f, 1f, Lists.newArrayList(new PlaySound(SoundEvents.WOOD_BREAK, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("white_candle", new LightBringer(Items.WHITE_CANDLE, 64000, 205, 1.3f, 3.3f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(MobEffects.DARKNESS, 80, 0, true))));
        DEFAULT_LIGHTBRINGERS.put("light_gray_candle", new LightBringer(Items.LIGHT_GRAY_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(EffectSetup.PIERCER.get(), 80, 0, true))));
        DEFAULT_LIGHTBRINGERS.put("gray_candle", new LightBringer(Items.GRAY_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(MobEffects.DAMAGE_RESISTANCE, 60, 0, false))));
        DEFAULT_LIGHTBRINGERS.put("brown_candle", new LightBringer(Items.BROWN_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(MobEffects.SATURATION, 2, 0, false))));
        DEFAULT_LIGHTBRINGERS.put("red_candle", new LightBringer(Items.RED_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(MobEffects.DAMAGE_BOOST, 80, 1, false))));
        DEFAULT_LIGHTBRINGERS.put("orange_candle", new LightBringer(Items.ORANGE_CANDLE, 64000, 200, 1.5f, 5.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(EffectSetup.RESILIENCE.get(), 1200, 0, false))));
        DEFAULT_LIGHTBRINGERS.put("yellow_candle", new LightBringer(Items.YELLOW_CANDLE, 64000, 400, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(MobEffects.DIG_SPEED, 80, 1, false))));
        DEFAULT_LIGHTBRINGERS.put("lime_candle", new LightBringer(Items.LIME_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(MobEffects.JUMP, 600, 1, false), new ApplyPotionEffect(MobEffects.SLOW_FALLING, 600, 1, false))));
        DEFAULT_LIGHTBRINGERS.put("green_candle", new LightBringer(Items.GREEN_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(MobEffects.LUCK, 2400, 0, false), new ApplyPotionEffect(EffectSetup.CASTER.get(), 2400, 2, false))));
        DEFAULT_LIGHTBRINGERS.put("cyan_candle", new LightBringer(Items.CYAN_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIREWORK_ROCKET_LAUNCH, 1, 1), new ApplyPotionEffect(MobEffects.LEVITATION, 20, 59, false))));
        DEFAULT_LIGHTBRINGERS.put("light_blue_candle", new LightBringer(Items.LIGHT_BLUE_CANDLE, 64000, 200, 0.8f, 0.5f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(MobEffects.WATER_BREATHING, 1200, 0, false))));
        DEFAULT_LIGHTBRINGERS.put("blue_candle", new LightBringer(Items.BLUE_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(MobEffects.MOVEMENT_SPEED, 80, 1, false))));
        DEFAULT_LIGHTBRINGERS.put("purple_candle", new LightBringer(Items.PURPLE_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(MobEffects.NIGHT_VISION, 460, 0, false))));
        DEFAULT_LIGHTBRINGERS.put("magenta_candle", new LightBringer(Items.MAGENTA_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new RemovePotionEffect(EffectSetup.BUSTED.get(), false))));
        DEFAULT_LIGHTBRINGERS.put("pink_candle", new LightBringer(Items.PINK_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new ApplyPotionEffect(EffectSetup.DEATH_WARD.get(), 40, 0, false))));
        // TODO vvv Has a 3% chance to remove curses (utilizing the Forge Curse tag for mod compatibility) vvv
        DEFAULT_LIGHTBRINGERS.put("black_candle", new LightBringer(Items.BLACK_CANDLE, 64000, 200, 1.5f, 4.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1), new RemoveCurseEffect(0.03f))));
        DEFAULT_LIGHTBRINGERS.put("sea_pickle", new LightBringer(Items.SEA_PICKLE, true, 64000, 200, 1.0f, 0.1f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("soul_campfire", new LightBringer(Items.SOUL_CAMPFIRE, 64000, 120, 1.3f, 6.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("soul_lantern", new LightBringer(Items.SOUL_LANTERN, 64000, 100, 1.0f, 1.4f, Lists.newArrayList(new PlaySound(SoundEvents.LANTERN_BREAK, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("soul_torch", new LightBringer(Items.SOUL_TORCH, 64000, 150, 1.8f, 6.0f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1))));
        DEFAULT_LIGHTBRINGERS.put("glow_lichen", new LightBringer(Items.GLOW_LICHEN, 64000, 400, 0.9f, 0.7f, Lists.newArrayList(new PlaySound(SoundEvents.FIRE_EXTINGUISH, 1, 1))));
    }

    public static void generateDefaults() {

        // Defaults
        File defaultsDir = DEFAULTS_PATH.toFile();
        defaultsDir.mkdirs();
        ModRef.LOGGER.info("Generating default flame data.");
        for (Map.Entry<String, LightBringer> entry : DEFAULT_LIGHTBRINGERS.entrySet()) {
            try {
                File file = new File(defaultsDir, entry.getKey() + ".json");
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write(GSON.toJson(serialize(entry.getValue())));
                writer.close();
            } catch (IOException e) {
                ModRef.LOGGER.error("Could not generate default flame data for " + entry.getKey() + ".json");
            }
        }

        // Flame Data Configs
        File flameDataDir = FLAME_DATA_PATH.toFile();
        if (flameDataDir.exists()) {
            ModRef.LOGGER.debug("Skipping generating initial flame data.");
        } else {
            flameDataDir.mkdirs();
            ModRef.LOGGER.info("Generating initial flame data.");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            for (Map.Entry<String, LightBringer> entry : DEFAULT_LIGHTBRINGERS.entrySet()) {
                try {
                    File file = new File(flameDataDir, entry.getKey() + ".json");
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);
                    writer.write(gson.toJson(serialize(entry.getValue())));
                    writer.close();
                } catch (IOException e) {
                    ModRef.LOGGER.error("Could not generate initial flame data for " + entry.getKey() + ".json", e);
                }
            }
        }

        // Readme file
        File readmeFile = README_PATH.toFile();
        if (!readmeFile.exists()) {
            ModRef.LOGGER.info("Generating readme file.");
        }

        try {
            readmeFile.createNewFile();
            FileWriter writer = new FileWriter(readmeFile);
            writer.write(README_TEXT);
            writer.close();
        } catch (IOException e) {
            ModRef.LOGGER.error("Could not generate the readme file.", e);
        }
    }

    public static void load() {
        initializeDefaults();
        generateDefaults();
        int count = 0;
        for (File file : FLAME_DATA_PATH.toFile().listFiles()) {
            try {
                FileReader reader = new FileReader(file);
                LightBringer lightbringer = deserialize(JsonParser.parseReader(reader).getAsJsonObject());
                LIGHT_BRINGERS.put(lightbringer.item(), lightbringer);
                count++;
            } catch (FileNotFoundException e) {
                ModRef.LOGGER.error("Could not read flame data for " + file.getName(), e);
            }
        }
        ModRef.LOGGER.info("Loaded " + count + " lightbringers.");
    }

    public static JsonObject serialize(LightBringer lightBringer) {
        JsonObject json = new JsonObject();
        json.addProperty("item", ForgeRegistries.ITEMS.getKey(lightBringer.item()).toString());
        if (lightBringer.isWaterOnly()) {
            json.addProperty("waterOnly", true);
        }
        json.addProperty("maxFlame", lightBringer.maxFlame());
        json.addProperty("baseConsumption", lightBringer.baseConsumption());
        json.addProperty("rainConsumptionMultiplier", lightBringer.rainConsumptionMultiplier());
        json.addProperty("underwaterConsumptionMultiplier", lightBringer.underwaterConsumptionMultiplier());

        JsonArray effectsJson = new JsonArray();
        for (BurnoutEffect effect : lightBringer.effects()) {
            JsonObject effectJson = effect.getSerializer().serialize(effect);
            effectJson.addProperty("type", effect.getSerializer().id());
            effectsJson.add(effectJson);
        }

        json.add("effects", effectsJson);

        return json;
    }

    public static LightBringer deserialize(JsonObject json) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(json.get("item").getAsString()));
        boolean waterOnly = json.has("waterOnly") && json.get("waterOnly").getAsBoolean();
        int maxFlame = json.get("maxFlame").getAsInt();
        int consumption = json.get("baseConsumption").getAsInt();
        float rainMultiplier = json.get("rainConsumptionMultiplier").getAsFloat();
        float underwaterMultiplier = json.get("underwaterConsumptionMultiplier").getAsFloat();

        List<BurnoutEffect<?>> effects = new ArrayList<>();
        JsonArray effectsJson = json.get("effects").getAsJsonArray();
        for (JsonElement effectElement : effectsJson) {
            JsonObject effectJson = effectElement.getAsJsonObject();
            String type = effectJson.get("type").getAsString();
            BurnoutEffect.Serializer<?> effectSerializer = BURNOUT_EFFECTS.get(type);
            BurnoutEffect effect = effectSerializer.deserialize(effectJson);
            effects.add(effect);
        }

        return new LightBringer(item, waterOnly, maxFlame, consumption, rainMultiplier, underwaterMultiplier, effects);
    }

    public static final String README_TEXT = """
        'defaults' folder is only for reference, it does not do anything in game.
                
        'flame_data' folder holds json file which define lightbringers.
        You can delete any default file in there to remove it from the game, or add your own lightbringers.
        You can regenerate the 'flame_data' folder by removing it.
        """;

}
