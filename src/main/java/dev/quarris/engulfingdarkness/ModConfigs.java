package dev.quarris.engulfingdarkness;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModConfigs {

    public static ForgeConfigSpec.IntValue darknessLightLevel;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> whitelistedDims;
    public static ForgeConfigSpec.BooleanValue treatDimsAsBlacklisted;
    public static ForgeConfigSpec.DoubleValue darknessDamage;
    public static ForgeConfigSpec.DoubleValue darknessLevelIncrement;
    public static ForgeConfigSpec.DoubleValue dangerLevelIncrement;
    public static ForgeConfigSpec.DoubleValue dangerTimer;
    public static ForgeConfigSpec.DoubleValue engulfTimer;

    public static ForgeConfigSpec.IntValue spawnVeiledTimer;

    public static ForgeConfigSpec.BooleanValue debugMode;
    public static ForgeConfigSpec.BooleanValue nightmareMode;


    public static ForgeConfigSpec.Builder init(ForgeConfigSpec.Builder builder) {
        darknessLightLevel = builder.comment(
            "The light level that the darkness should trigger at."
        ).defineInRange("darkness_level", 4, 0, 15);

        builder.push("dimension");
        whitelistedDims = builder.defineListAllowEmpty(Collections.singletonList("whitelisted_dimensions"), ModConfigs::defaultDims, o -> true);
        treatDimsAsBlacklisted = builder.define("treat_dims_as_blacklist", false);
        builder.pop();

        darknessDamage = builder.comment(
            "Percentage of max health to deal to the player every time darkness damage is dealt."
        ).defineInRange("darkness_damage", 20.0, 0.0, 100.0);

        engulfTimer = builder.comment(
            "Amount of time (in seconds) for the darkness to fully engulf you."
        ).defineInRange("darkness_timer", 5.0, 1.0, 600.0);
        dangerTimer = builder.comment(
            "Amount of time (in seconds) for the player to start taking damage after fully engulfed."
        ).defineInRange("danger_timer", 2.0, 1.0, 600.0);
        spawnVeiledTimer = builder.comment(
            "Amount of time the Veiled effect will last (in seconds) when first joining the world or after each death. Set to 0 to never apply the effect."
        ).defineInRange("spawn_veiled_timer", 90, 0, 1000000);

        darknessLevelIncrement = builder.comment(
            "[DEPRECATED (use darkness_timer)] How fast does the darkness engulf when in low light level."
        ).defineInRange("darkness_increment", 0.01, 0.001, 1.0);

        dangerLevelIncrement = builder.comment(
            "DEPRECATED (use danger_timer)] Once in full darkness, how fast until the damage starts to trigger."
        ).defineInRange("danger_increment", 0.03, 0.001, 1.0);

        builder.push("modes");
        nightmareMode = builder.comment(
            "Nightmare Mode increases difficulty by ramping up the effects and damage dealt significantly"
        ).define("nightmare_mode", false);
        debugMode = builder.comment(
            "Debug mode displays or give info about the state of darkness at any given time."
        ).define("debug_mode", false);
        builder.pop();
        return builder;
    }

    public static boolean isAllowed(ResourceLocation dimension) {
        return treatDimsAsBlacklisted.get() != whitelistedDims.get().contains(dimension.toString());
    }

    private static List<String> defaultDims() {
        List<String> list = new ArrayList<>();
        list.add(Level.OVERWORLD.location().toString());
        return list;
    }
}
