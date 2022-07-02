package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.capability.IDarkness;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    @SubscribeEvent
    public static void registerEffect(RegistryEvent.Register<MobEffect> event) {
        event.getRegistry().register(EngulfingDarkness.veiledMobEffect = new VeiledMobEffect().setRegistryName(ModRef.res("veiled")));
    }

    @SubscribeEvent
    public static void registerPotion(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(new Potion(new MobEffectInstance(EngulfingDarkness.veiledMobEffect, 6000)).setRegistryName(ModRef.res("veiled")));
    }

    @SubscribeEvent
    public static void registerDarknessCap(RegisterCapabilitiesEvent event) {
        event.register(IDarkness.class);
    }

}
