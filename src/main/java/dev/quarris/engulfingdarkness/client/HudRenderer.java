package dev.quarris.engulfingdarkness.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.capability.IDarkness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class HudRenderer {

    private static final ResourceLocation HUD_TEXTURE = ModRef.res("textures/gui/hud.png");

    @SubscribeEvent
    public static void renderBurnout(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.EXPERIENCE_BAR.id(), "burnout", HudRenderer::renderBurnoutHud);
    }

    public static void renderBurnoutHud(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> {
            if (!darkness.isInDarkness() || darkness.getBurnout() >= IDarkness.MAX_BURNOUT) return;

            Window window = gui.getMinecraft().getWindow();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShaderTexture(0, HUD_TEXTURE);
            final int barWidth = 182;
            final int barHeight = 5;
            int barX = (window.getGuiScaledWidth() - barWidth) / 2;
            int barY = 5;
            float renderWidth = barWidth * (darkness.getBurnout() / IDarkness.MAX_BURNOUT);
            GuiComponent.blit(poseStack, barX, barY, 0, 0, 0, barWidth, barHeight, 256, 256);
            GuiComponent.blit(poseStack, barX, barY, 0, 0, 5, (int) renderWidth, barHeight, 256, 256);

            barY = 15;
            renderWidth = barWidth * (1 - darkness.getDarkness());
            GuiComponent.blit(poseStack, barX, barY, 0, 0, 0, barWidth, barHeight, 256, 256);
            GuiComponent.blit(poseStack, barX, barY, 0, 0, 5, (int) renderWidth, barHeight, 256, 256);

            barY = 25;
            renderWidth = barWidth * (1 - darkness.getDanger());
            GuiComponent.blit(poseStack, barX, barY, 0, 0, 0, barWidth, barHeight, 256, 256);
            GuiComponent.blit(poseStack, barX, barY, 0, 0, 5, (int) renderWidth, barHeight, 256, 256);
        });
    }
}
