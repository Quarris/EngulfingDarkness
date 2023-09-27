package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.darkness.LightBringer;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import dev.quarris.engulfingdarkness.registry.EnchantmentSetup;
import dev.quarris.engulfingdarkness.registry.PotionSetup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        // Load light bringers
        LightBringer.addLightBringer(Items.TORCH, 640);
        LightBringer.addLightBringer(Items.SOUL_TORCH, 1280);
        LightBringer.addLightBringer(Items.GLOWSTONE_DUST, 6400);
    }
}
