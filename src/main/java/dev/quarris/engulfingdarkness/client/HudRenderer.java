package dev.quarris.engulfingdarkness.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.engulfingdarkness.ModConfigs;
import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.darkness.IDarkness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class HudRenderer {

    private static final ResourceLocation HUD_TEXTURE = ModRef.res("textures/gui/hud.png");

    public static void renderBurnoutHud(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> {
            if (!darkness.isInDarkness() || darkness.getBurnout() >= IDarkness.MAX_BURNOUT) return;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShaderTexture(0, HUD_TEXTURE);
            final int barWidth = 182;
            final int barHeight = 5;
            int barX = (screenWidth - barWidth) / 2;
            int barY = 5;

            // Burnout
            float renderWidth = barWidth * (darkness.getBurnout() / IDarkness.MAX_BURNOUT);
            GuiComponent.blit(poseStack, barX, barY, 0, 0, 0, barWidth, barHeight, 256, 256); // Burnout backdrop

            float modifier = 1 - (darkness.getBurnoutModifier() - 1) / 5f;
            RenderSystem.setShaderColor(1, modifier, modifier, 1); // Redden the burnout value
            GuiComponent.blit(poseStack, barX, barY, 0, 0, 5, (int) renderWidth, barHeight, 256, 256); // Burnout value
            RenderSystem.setShaderColor(1, 1, 1, 1);

            if (ModConfigs.debugMode.get()) {
                // Darkness
                barY = 15;
                renderWidth = barWidth * (1 - darkness.getDarkness());
                GuiComponent.blit(poseStack, barX, barY, 0, 0, 0, barWidth, barHeight, 256, 256);
                GuiComponent.blit(poseStack, barX, barY, 0, 0, 5, (int) renderWidth, barHeight, 256, 256);

                // Danger
                barY = 25;
                renderWidth = barWidth * (1 - darkness.getDanger());
                GuiComponent.blit(poseStack, barX, barY, 0, 0, 0, barWidth, barHeight, 256, 256);
                GuiComponent.blit(poseStack, barX, barY, 0, 0, 5, (int) renderWidth, barHeight, 256, 256);
            }
        });
    }
}
