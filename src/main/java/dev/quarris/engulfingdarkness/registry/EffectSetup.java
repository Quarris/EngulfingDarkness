package dev.quarris.engulfingdarkness.registry;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.effect.*;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectSetup {

    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModRef.ID);

    public static final RegistryObject<MobEffect> SOUL_GUARD = REGISTRY.register("soul_guard", SoulGuardMobEffect::new);
    public static final RegistryObject<MobEffect> SOUL_VEIL = REGISTRY.register("soul_veil", SoulVeilMobEffect::new);
    public static final RegistryObject<MobEffect> BUSTED = REGISTRY.register("busted", BustedMobEffect::new);
    public static final RegistryObject<MobEffect> SENTINEL_PROTECTION = REGISTRY.register("sentinel_protection", SentinelProtectionEffect::new);
    public static final RegistryObject<MobEffect> DEATH_WARD = REGISTRY.register("death_ward", DeathWardEffect::new);
    public static final RegistryObject<MobEffect> RESILIENCE = REGISTRY.register("resilience", ResilienceEffect::new);
    public static final RegistryObject<MobEffect> PIERCER = REGISTRY.register("piercer", PiercerEffect::new);
    public static final RegistryObject<MobEffect> EASY_TARGET = REGISTRY.register("easy_target", EasyTargetEffect::new);
    public static final RegistryObject<MobEffect> CASTER = REGISTRY.register("caster", CasterMobEffect::new);

    public static void init(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
