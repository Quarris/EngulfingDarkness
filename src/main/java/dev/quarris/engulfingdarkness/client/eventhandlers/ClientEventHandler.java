package dev.quarris.engulfingdarkness.client.eventhandlers;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.client.Minecraft;
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
                float scale = (float) Mth.clamp((1-darkness.getDarkness()), 0.01, 1);
                event.scaleNearPlaneDistance(scale);
                event.scaleFarPlaneDistance(scale + 0.05f);
                event.setCanceled(true);
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
