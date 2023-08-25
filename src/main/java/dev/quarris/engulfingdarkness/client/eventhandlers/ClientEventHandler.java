package dev.quarris.engulfingdarkness.client.eventhandlers;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModRef.ID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void renderFog(ViewportEvent.RenderFog event) {
        Minecraft.getInstance().player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> {
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

    @SubscribeEvent
    public static void applyFogColors(ViewportEvent.ComputeFogColor event) {
        Minecraft.getInstance().player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> {
            if (darkness.getDarkness() > 0.01) {
                float perc = 1 - darkness.getDarkness();
                event.setRed(Mth.lerp(perc, 0, event.getRed()));
                event.setGreen(Mth.lerp(perc, 0, event.getGreen()));
                event.setBlue(Mth.lerp(perc, 0, event.getBlue()));
            }
        });
    }

}
