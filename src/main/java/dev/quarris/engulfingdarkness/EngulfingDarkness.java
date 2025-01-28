package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.configs.FlameConfigs;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import dev.quarris.engulfingdarkness.registry.PotionSetup;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

@Mod(ModRef.ID)
public class EngulfingDarkness {

    public EngulfingDarkness(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        Pair<ModConfigs, ForgeConfigSpec> configSpec = new ForgeConfigSpec.Builder().configure(ModConfigs::new);
        ModRef.CONFIGS = configSpec.getKey();
        context.registerConfig(ModConfig.Type.COMMON, configSpec.getValue());

        modBus.addListener(ModRef.configs()::onLoad);
        modBus.addListener(this::commonSetup);
        EffectSetup.init(modBus);
        PotionSetup.init(modBus);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.init();
        FlameConfigs.load();
    }
}
