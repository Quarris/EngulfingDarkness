package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.packets.PacketHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModRef.ID)
public class EngulfingDarkness {

    public static final DamageSource damageSource = new DamageSource("engulfingDarkness").bypassMagic().bypassArmor().setScalesWithDifficulty();

    public static MobEffect veiledMobEffect;

    public EngulfingDarkness() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigs.init(new ForgeConfigSpec.Builder()).build());
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
    }




}
