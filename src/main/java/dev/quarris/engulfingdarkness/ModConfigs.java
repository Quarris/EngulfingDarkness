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

    public static ForgeConfigSpec.IntValue spawnVeiledTimer;


    public static ForgeConfigSpec.Builder init(ForgeConfigSpec.Builder builder) {
        darknessLightLevel = builder.comment(
            "The light level that the darkness should trigger at."
        ).defineInRange("darkness_level", 4, 0, 15);

        builder.push("dimension");
        whitelistedDims = builder.defineListAllowEmpty(Collections.singletonList("whitelisted_dimensions"), ModConfigs::defaultDims, o -> true);
        treatDimsAsBlacklisted = builder.define("treat_dims_as_blacklist", false);
        builder.pop();

        darknessDamage = builder.defineInRange("darkness_damage", 4.0, 0.0, 100.0);

        darknessLevelIncrement = builder.comment(
            "How fast does the darkness engulf when in low light level."
        ).defineInRange("darkness_increment", 0.01, 0.001, 1.0);

        dangerLevelIncrement = builder.comment(
            "Once in full darkness, how fast until the damage starts to trigger."
        ).defineInRange("danger_increment", 0.03, 0.001, 1.0);

        spawnVeiledTimer = builder.comment(
                "Amount of time the Veiled effect will last (in seconds) when first joining the world or after each death. Set to 0 to never apply the effect."
        ).defineInRange("spawn_veiled_timer", 300, 0, 1000000);
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
