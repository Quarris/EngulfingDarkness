package dev.quarris.engulfingdarkness.registry;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PotionSetup {

    public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, ModRef.ID);
    public static final RegistryObject<Potion> SOUL_VEIL = REGISTRY.register("soul_veil", () -> new Potion(new MobEffectInstance(EffectSetup.SOUL_VEIL.get(), 300 * 20)));

    public static void init(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
