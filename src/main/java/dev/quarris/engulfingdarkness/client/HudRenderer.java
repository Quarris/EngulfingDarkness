package dev.quarris.engulfingdarkness.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.engulfingdarkness.ModConfigs;
import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.darkness.FlameData;
import dev.quarris.engulfingdarkness.darkness.IDarkness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class HudRenderer {

    private static final ResourceLocation HUD_TEXTURE = ModRef.res("textures/gui/hud.png");
    private static final ResourceLocation BURNOUT_SLOT = ModRef.res("textures/gui/burnout_slot.png");

    public static boolean renderItemBurnout(Font font, ItemStack stack, int xOffset, int yOffset, float blitOffset) {
        Player player = Minecraft.getInstance().player;
        player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> {
            FlameData light = darkness.getLight(stack);
            if (light == null) return;
            PoseStack poseStack = new PoseStack();
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, BURNOUT_SLOT);
            float startY = 24 - (int) (light.getLife() * 24);
            //Gui.blit(poseStack, xOffset - 4, yOffset - 4 + (int) startY, 0, startY, 24, (int) (24 - startY), 24, 24);
            Gui.fill(poseStack, xOffset, yOffset + 16 - (int) (light.getLife() * 16), xOffset + 16, yOffset + 16, 0xdd000000);
        });

        return true;
    }

    public static void renderBurnoutHud(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> {
            if (!darkness.isInLowLight() || !darkness.isHoldingFlame()) return;

            float flameLife = darkness.getFlameLife();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShaderTexture(0, HUD_TEXTURE);
            final int barWidth = 182;
            final int barHeight = 5;
            int barX = (screenWidth - barWidth) / 2;
            int barY = 5;

            // Burnout
            float renderWidth = barWidth * flameLife;
            GuiComponent.blit(poseStack, barX, barY, 0, 0, 0, barWidth, barHeight, 256, 256); // Burnout backdrop

            float modifier = 1 - Mth.clamp(darkness.getConsumptionAmplifier() / 5f, 0, 1);
            RenderSystem.setShaderColor(1, modifier, modifier, 1); // Redden the burnout value
            GuiComponent.blit(poseStack, barX, barY, 0, 0, 5, (int) renderWidth, barHeight, 256, 256); // Burnout value

            IDarkness.Popup popup = darkness.getPopup();
            if (popup.isActive() && (popup.time() / 5) % 2 == 0) {
                int alpha = (popup.color() >> 24) & 255;
                int red = (popup.color() >> 16) & 255;
                int green = (popup.color() >> 8) & 255;
                int blue = (popup.color()) & 255;
                RenderSystem.setShaderColor(red / 255f, green / 255f, blue / 255f, alpha / 255f); // Popup color
                GuiComponent.blit(poseStack, barX, barY, 0, 0, 10, barWidth, barHeight, 256, 256); // Popup
            }

            RenderSystem.setShaderColor(1, 1, 1, 1);

            if (ModConfigs.debugMode.get()) {
                // Darkness
                barY = 15;
                renderWidth = barWidth * (1 - darkness.getEngulfLevel());
                GuiComponent.blit(poseStack, barX, barY, 0, 0, 0, barWidth, barHeight, 256, 256);
                GuiComponent.blit(poseStack, barX, barY, 0, 0, 5, (int) renderWidth, barHeight, 256, 256);

                // Danger
                barY = 25;
                renderWidth = barWidth * (1 - darkness.getDangerLevel());
                GuiComponent.blit(poseStack, barX, barY, 0, 0, 0, barWidth, barHeight, 256, 256);
                GuiComponent.blit(poseStack, barX, barY, 0, 0, 5, (int) renderWidth, barHeight, 256, 256);
            }
        });
    }
}
