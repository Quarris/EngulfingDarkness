package dev.quarris.engulfingdarkness.registry;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.effect.BustedMobEffect;
import dev.quarris.engulfingdarkness.effect.SentinelProtectionEffect;
import dev.quarris.engulfingdarkness.effect.SoulVeilMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectSetup {

    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModRef.ID);

    public static final RegistryObject<MobEffect> SOUL_VEIL = REGISTRY.register("soul_veil", SoulVeilMobEffect::new);
    public static final RegistryObject<MobEffect> BUSTED = REGISTRY.register("busted", BustedMobEffect::new);
    public static final RegistryObject<MobEffect> SENTINEL_PROTECTION = REGISTRY.register("sentinel_protection", SentinelProtectionEffect::new);

    public static void init(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
