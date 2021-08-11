package dev.quarris.engulfingdarkness;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.quarris.engulfingdarkness.capability.Darkness;
import dev.quarris.engulfingdarkness.capability.DarknessCapability;
import dev.quarris.engulfingdarkness.capability.IDarkness;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import dev.quarris.engulfingdarkness.packets.SyncDarknessMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModRef.ID)
public class EventHandler {

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(DarknessCapability.KEY, new DarknessCapability(new Darkness()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderFog(EntityViewRenderEvent.RenderFogEvent event) {
        Minecraft.getInstance().player.getCapability(DarknessCapability.INST).ifPresent(darkness -> {
            if (darkness.getDarkness() > 0.01) {
                float startFog = event.getType() == FogRenderer.FogType.FOG_SKY ?
                    0 :
                    event.getFarPlaneDistance() * 0.75f * (1 - darkness.getDarkness());

                float endFog = 10 + event.getFarPlaneDistance() * (1 - darkness.getDarkness());
                RenderSystem.fogStart(startFog);
                RenderSystem.fogEnd(endFog);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void applyFogColors(EntityViewRenderEvent.FogColors event) {
        Minecraft.getInstance().player.getCapability(DarknessCapability.INST).ifPresent(darkness -> {
            if (darkness.getDarkness() > 0.01) {
                float perc = 1 - darkness.getDarkness();
                event.setRed(MathHelper.lerp(perc, 0, event.getRed()));
                event.setGreen(MathHelper.lerp(perc, 0, event.getGreen()));
                event.setBlue(MathHelper.lerp(perc, 0, event.getBlue()));
            }
        });
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            event.player.getCapability(DarknessCapability.INST).ifPresent(darkness -> darkness.tick(event.player));
        }
    }

    @SubscribeEvent
    public static void syncOnLogin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getPlayer().getCapability(DarknessCapability.INST).ifPresent(darkness -> {
            PacketHandler.sendToClient(new SyncDarknessMessage(darkness.serializeNBT()), event.getPlayer());
        });
    }
}
