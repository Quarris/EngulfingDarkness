package dev.quarris.engulfingdarkness;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.quarris.engulfingdarkness.capability.Darkness;
import dev.quarris.engulfingdarkness.capability.DarknessCapability;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import dev.quarris.engulfingdarkness.packets.SyncDarknessMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModRef.ID)
public class EventHandler {

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(DarknessCapability.KEY, new DarknessCapability(new Darkness()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderFog(ViewportEvent.RenderFog event) {
        Minecraft.getInstance().player.getCapability(DarknessCapability.INST).ifPresent(darkness -> {
            if (darkness.getDarkness() > 0.01) {
                float startFog = event.getMode() == FogRenderer.FogMode.FOG_SKY ?
                    0 :
                    event.getFarPlaneDistance() * 0.75f * (1 - darkness.getDarkness());

                float endFog = 10 + event.getFarPlaneDistance() * (1 - darkness.getDarkness());
                RenderSystem.setShaderFogStart(startFog);
                RenderSystem.setShaderFogEnd(endFog);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void applyFogColors(ViewportEvent.ComputeFogColor event) {
        Minecraft.getInstance().player.getCapability(DarknessCapability.INST).ifPresent(darkness -> {
            if (darkness.getDarkness() > 0.01) {
                float perc = 1 - darkness.getDarkness();
                event.setRed(Mth.lerp(perc, 0, event.getRed()));
                event.setGreen(Mth.lerp(perc, 0, event.getGreen()));
                event.setBlue(Mth.lerp(perc, 0, event.getBlue()));
            }
        });
    }

    @SubscribeEvent
    public static void applyEffectOnRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.isEndConquered() || event.getEntity().level.isClientSide) {
            return;
        }

        var time = ModConfigs.spawnVeiledTimer.get() * 20;
        if (time > 0) {
            event.getEntity().addEffect(new MobEffectInstance(EngulfingDarkness.veiledMobEffect, time));
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            event.player.getCapability(DarknessCapability.INST).ifPresent(darkness -> darkness.tick(event.player));
        }
    }

    @SubscribeEvent
    public static void syncOnLogin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().getCapability(DarknessCapability.INST).ifPresent(darkness -> {
            PacketHandler.sendToClient(new SyncDarknessMessage(darkness.serializeNBT()), event.getEntity());
        });

        CompoundTag persitentData = event.getEntity().getPersistentData();
        if (!persitentData.contains("FirstJoin")) {
            persitentData.putBoolean("FirstJoin", true);
            var time = ModConfigs.spawnVeiledTimer.get() * 20;
            if (time > 0) {
                event.getEntity().addEffect(new MobEffectInstance(EngulfingDarkness.veiledMobEffect, time));
            }
        }
    }
}
