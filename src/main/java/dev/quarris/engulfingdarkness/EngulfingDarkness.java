package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.packets.PacketHandler;
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
        modBus.addListener(this::commonSetup);
        ModRegistry.init(modBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigs.init(new ForgeConfigSpec.Builder()).build());
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
    }




}
