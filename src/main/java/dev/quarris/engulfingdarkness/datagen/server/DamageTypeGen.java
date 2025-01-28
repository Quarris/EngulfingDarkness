package dev.quarris.engulfingdarkness.datagen.server;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.registry.DamageSetup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypeGen {

    public static void bootstrap(BootstrapContext<DamageType> ctx) {
        ctx.register(DamageSetup.DARKNESS_DAMAGE,
            new DamageType(
                DamageSetup.DARKNESS_DAMAGE.location().toString(),
                DamageScaling.NEVER,
                0.0f
            ));

        ctx.register(DamageSetup.DARKNESS_DAMAGE_SENTINEL,
            new DamageType(
                DamageSetup.DARKNESS_DAMAGE_SENTINEL.location().toString(),
                DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER,
                0.1f
            ));
    }

}
