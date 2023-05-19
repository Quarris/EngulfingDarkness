package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.capability.IDarkness;
import dev.quarris.engulfingdarkness.content.BustedMobEffect;
import dev.quarris.engulfingdarkness.content.SoulSentinelEnchantment;
import dev.quarris.engulfingdarkness.content.SoulVeilMobEffect;
import dev.quarris.engulfingdarkness.content.ValianceEnchantment;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRegistry {


    public static void init(IEventBus bus) {
        Effects.REGISTRY.register(bus);
        Potions.REGISTRY.register(bus);
        Enchantments.REGISTRY.register(bus);
    }

    @SubscribeEvent
    public static void registerDarknessCap(RegisterCapabilitiesEvent event) {
        event.register(IDarkness.class);
    }

    public static class Effects {

        public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModRef.ID);
        public static final RegistryObject<MobEffect> SOUL_VEIL = REGISTRY.register("soul_veil", SoulVeilMobEffect::new);
        public static final RegistryObject<MobEffect> BUSTED = REGISTRY.register("busted", BustedMobEffect::new);
    }

    public static class Potions {

        public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, ModRef.ID);
        public static final RegistryObject<Potion> SOUL_VEIL = REGISTRY.register("soul_veil", () -> new Potion(new MobEffectInstance(Effects.SOUL_VEIL.get(), 300 * 20)));
    }

    public static class Enchantments {

        public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ModRef.ID);
        public static final RegistryObject<Enchantment> VALIANCE = REGISTRY.register("valiance", ValianceEnchantment::new);
        public static final RegistryObject<Enchantment> SOUL_SENTINEL = REGISTRY.register("soul_sentinel", SoulSentinelEnchantment::new);
    }

}
