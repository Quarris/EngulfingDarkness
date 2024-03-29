package dev.quarris.engulfingdarkness.eventhandlers;

import dev.quarris.engulfingdarkness.ModConfigs;
import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.capability.DarknessProvider;
import dev.quarris.engulfingdarkness.capability.IDarkness;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModRef.ID)
public class CommonEventHandler {

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(DarknessProvider.KEY, new DarknessProvider(player));
        }
    }

    @SubscribeEvent
    public static void applyEffectOnRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.isEndConquered() || event.getEntity().level.isClientSide) {
            return;
        }

        var time = ModConfigs.spawnVeiledTimer.get() * 20;
        if (time > 0) {
            event.getEntity().addEffect(new MobEffectInstance(EffectSetup.SOUL_VEIL.get(), time));
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            event.player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(IDarkness::tick);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        CompoundTag persitentData = event.getEntity().getPersistentData();
        if (!persitentData.contains("FirstJoin")) {
            persitentData.putBoolean("FirstJoin", true);
            var time = ModConfigs.spawnVeiledTimer.get() * 20;
            if (time > 0) {
                event.getEntity().addEffect(new MobEffectInstance(EffectSetup.SOUL_VEIL.get(), time));
            }
        }
    }
}
