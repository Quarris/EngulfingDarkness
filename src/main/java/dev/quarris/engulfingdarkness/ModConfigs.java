package dev.quarris.engulfingdarkness;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfigs {

    public static ForgeConfigSpec.IntValue darknessLightLevel;

    public static ForgeConfigSpec.Builder init(ForgeConfigSpec.Builder builder) {
        darknessLightLevel = builder.comment(
            "The light level that the darkness should trigger at."
        ).defineInRange("darkness_level", 4, 0, 15);

        return builder;
    }
}
