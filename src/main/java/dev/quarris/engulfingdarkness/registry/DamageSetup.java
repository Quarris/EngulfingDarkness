package dev.quarris.engulfingdarkness.registry;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DamageSetup {

    public static final ResourceKey<DamageType> DARKNESS_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ModRef.res("darkness"));
    public static final ResourceKey<DamageType> DARKNESS_DAMAGE_SENTINEL = ResourceKey.create(Registries.DAMAGE_TYPE, ModRef.res("darkness_sentinel"));

    public static DamageSource darknessDamage(Level pLevel) {
        return new DamageSource(pLevel.holderLookup(Registries.DAMAGE_TYPE).getOrThrow(DARKNESS_DAMAGE));
    }

    public static DamageSource darknessSentinelDamage(Level pLevel, Player causingPlayer) {
        return new DamageSource(pLevel.holderLookup(Registries.DAMAGE_TYPE).getOrThrow(DARKNESS_DAMAGE_SENTINEL), null, causingPlayer);
    }

}
