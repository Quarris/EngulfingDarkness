package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.packets.PacketHandler;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import dev.quarris.engulfingdarkness.registry.EnchantmentSetup;
import dev.quarris.engulfingdarkness.registry.PotionSetup;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModRef.ID)
public class EngulfingDarkness {
    public EngulfingDarkness() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigs.init(new ForgeConfigSpec.Builder()).build());

        modBus.addListener(this::commonSetup);
        EffectSetup.init(modBus);
        PotionSetup.init(modBus);
        EnchantmentSetup.init(modBus);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
    }
}
