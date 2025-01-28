package dev.quarris.engulfingdarkness.mixins;

import dev.quarris.engulfingdarkness.client.HudRenderer;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "renderHeart", at = @At(value = "HEAD"), cancellable = true)
    private void purpleinator(GuiGraphics pGuiGraphics, Gui.HeartType pHeartType, int pX, int pY, boolean pHardcore, boolean pHalfHeart, boolean pBlinking, CallbackInfo ci) {
        if (Minecraft.getInstance().player.hasEffect(EffectSetup.BUSTED.getHolder().get())) {
            pGuiGraphics.blitSprite(RenderType::guiTextured, pHeartType.getSprite(pHardcore, pBlinking, pHalfHeart), pX, pY, 9, 9, 0xffb31ae6);
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"))
    private void renderBurnoutHud(GuiGraphics pGuiGraphics, int pX, CallbackInfo ci) {
        HudRenderer.renderBurnoutHud(pGuiGraphics);
    }
}
