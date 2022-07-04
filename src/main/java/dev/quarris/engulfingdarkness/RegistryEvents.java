package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.capability.IDarkness;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    @SubscribeEvent
    public static void registerEffect(RegisterEvent event) {
        event.register(ForgeRegistries.MOB_EFFECTS.getRegistryKey(), ModRef.res("veiled"), () -> EngulfingDarkness.veiledMobEffect = new VeiledMobEffect());
    }

    @SubscribeEvent
    public static void registerPotion(RegisterEvent event) {
        event.register(ForgeRegistries.POTIONS.getRegistryKey(), ModRef.res("veiled"), () -> new Potion(new MobEffectInstance(EngulfingDarkness.veiledMobEffect, 6000)));
    }

    @SubscribeEvent
    public static void registerDarknessCap(RegisterCapabilitiesEvent event) {
        event.register(IDarkness.class);
    }

}
