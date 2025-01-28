package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.darkness.LightBringer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModConfigs {

    private final ForgeConfigSpec.IntValue rawDarknessLightLevel;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> rawWhitelistedDims;
    private final ForgeConfigSpec.BooleanValue rawTreatDimsAsBlacklisted;
    private final ForgeConfigSpec.DoubleValue rawDarknessDamage;
    private final ForgeConfigSpec.DoubleValue rawDarknessLevelIncrement;
    private final ForgeConfigSpec.DoubleValue rawDangerLevelIncrement;
    private final ForgeConfigSpec.DoubleValue rawDangerTimer;
    private final ForgeConfigSpec.DoubleValue rawEngulfTimer;
    private final ForgeConfigSpec.IntValue rawSpawnVeiledTimer;
    private final ForgeConfigSpec.BooleanValue rawDebugMode;
    private final ForgeConfigSpec.BooleanValue rawNightmareMode;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> rawLightBringers;

    public int darknessLightLevel;
    public Set<ResourceLocation> whitelistedDims;
    public boolean treatDimsAsBlacklisted;
    public double darknessDamage;
    public double darknessLevelIncrement;
    public double dangerLevelIncrement;
    public double dangerTimer;
    public double engulfTimer;
    public int spawnVeiledTimer;
    public boolean debugMode;
    public boolean nightmareMode;

    public ModConfigs(ForgeConfigSpec.Builder builder) {
        this.rawDarknessLightLevel = builder.comment(
            "The light level that the darkness should trigger at."
        ).defineInRange("darkness_level", 4, 0, 15);

        builder.push("dimension");
        this.rawWhitelistedDims = builder.defineListAllowEmpty(Collections.singletonList("whitelisted_dimensions"), ModConfigs::defaultDims, o -> o instanceof String name && ResourceLocation.tryParse(name) != null);
        this.rawTreatDimsAsBlacklisted = builder.define("treat_dims_as_blacklist", false);
        builder.pop();

        this.rawDarknessDamage = builder.comment(
            "Percentage of max health to deal to the player every time darkness damage is dealt."
        ).defineInRange("darkness_damage", 20.0, 0.0, 100.0);

        this.rawEngulfTimer = builder.comment(
            "Amount of time (in seconds) for the darkness to fully engulf you."
        ).defineInRange("darkness_timer", 5.0, 1.0, 600.0);
        this.rawDangerTimer = builder.comment(
            "Amount of time (in seconds) for the player to start taking damage after fully engulfed."
        ).defineInRange("danger_timer", 2.0, 1.0, 600.0);
        this.rawSpawnVeiledTimer = builder.comment(
            "Amount of time the Veiled effect will last (in seconds) when first joining the world or after each death. Set to 0 to never apply the effect."
        ).defineInRange("spawn_veiled_timer", 90, 0, 1000000);

        this.rawDarknessLevelIncrement = builder.comment(
            "[DEPRECATED (use darkness_timer)] How fast does the darkness engulf when in low light level."
        ).defineInRange("darkness_increment", 0.01, 0.001, 1.0);

        this.rawDangerLevelIncrement = builder.comment(
            "DEPRECATED (use danger_timer)] Once in full darkness, how fast until the damage starts to trigger."
        ).defineInRange("danger_increment", 0.03, 0.001, 1.0);

        this.rawLightBringers = builder.comment(
            "Defines items can be used as light bringers and their durability (flame).",
            "Format: <mod>:<item>;<flame>"
        ).defineListAllowEmpty(Collections.singletonList("light_bringers"), ModConfigs::defaultLightBringers, o -> {
            if (!(o instanceof String entry)) {
                return false;
            }

            try {
                String[] split = entry.split(";");
                Integer.parseInt(split[1]); // Check if int.
                return ResourceLocation.tryParse(split[0]) != null;
            } catch (Exception e) {
                ModRef.LOGGER.error("Could not parse Light Bringer config data", e);
                return false;
            }
        });

        builder.push("modes");
        this.rawNightmareMode = builder.comment(
            "Nightmare Mode increases difficulty by ramping up the effects and damage dealt significantly"
        ).define("nightmare_mode", false);
        this.rawDebugMode = builder.comment(
            "Debug mode displays or give info about the state of darkness at any given time."
        ).define("debug_mode", false);
        builder.pop();
    }

    public void onLoad(ModConfigEvent event) {
        if (!ModRef.ID.equals(event.getConfig().getModId())) {
            return;
        }

        this.darknessLightLevel = this.rawDarknessLightLevel.get();
        this.treatDimsAsBlacklisted = this.rawTreatDimsAsBlacklisted.get();
        this.darknessDamage = this.rawDarknessDamage.get();
        this.darknessLevelIncrement = this.rawDarknessLevelIncrement.get();
        this.dangerLevelIncrement = this.rawDangerLevelIncrement.get();
        this.dangerTimer = this.rawDangerTimer.get();
        this.engulfTimer = this.rawEngulfTimer.get();
        this.spawnVeiledTimer = this.rawSpawnVeiledTimer.get();
        this.debugMode = this.rawDebugMode.get();
        this.nightmareMode = this.rawNightmareMode.get();

        this.whitelistedDims = this.rawWhitelistedDims.get().stream().map(ResourceLocation::parse).collect(Collectors.toSet());

        this.rawLightBringers.get().forEach(s -> {
            String[] split = s.split(";");
            ResourceLocation itemId = ResourceLocation.parse(split[0]);
            int flame = Integer.parseInt(split[1]);
            LightBringer.addLightBringer(ForgeRegistries.ITEMS.getValue(itemId), flame);
        });
    }

    public boolean isAllowed(ResourceLocation dimension) {
        return rawTreatDimsAsBlacklisted.get() != rawWhitelistedDims.get().contains(dimension.toString());
    }

    private static List<String> defaultDims() {
        List<String> list = new ArrayList<>();
        list.add(Level.OVERWORLD.location().toString());
        return list;
    }

    private static List<String> defaultLightBringers() {
        List<String> list = new ArrayList<>();
        list.add(ForgeRegistries.ITEMS.getKey(Items.TORCH) + ";640");
        list.add(ForgeRegistries.ITEMS.getKey(Items.SOUL_TORCH) + ";1280");
        list.add(ForgeRegistries.ITEMS.getKey(Items.GLOWSTONE_DUST) + ";6400");
        return list;
    }
}
