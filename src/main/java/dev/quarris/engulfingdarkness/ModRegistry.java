package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.capability.IDarkness;
import dev.quarris.engulfingdarkness.content.BustedMobEffect;
import dev.quarris.engulfingdarkness.content.SoulVeilMobEffect;
import net.minecraft.core.registries.BuiltInRegistries;
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

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModRef.ID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, ModRef.ID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ModRef.ID);

    public static final RegistryObject<MobEffect> VEILED_EFFECT = EFFECTS.register("soul_veil", SoulVeilMobEffect::new);
    public static final RegistryObject<MobEffect> BUSTED_EFFECT = EFFECTS.register("busted", BustedMobEffect::new);
    public static final RegistryObject<Potion> VEILED_POTION = POTIONS.register("soul_veil", () -> new Potion(new MobEffectInstance(VEILED_EFFECT.get(), 6000)));

    public static void init(IEventBus bus) {
        EFFECTS.register(bus);
        POTIONS.register(bus);
    }

    @SubscribeEvent
    public static void registerDarknessCap(RegisterCapabilitiesEvent event) {
        event.register(IDarkness.class);
    }

}
