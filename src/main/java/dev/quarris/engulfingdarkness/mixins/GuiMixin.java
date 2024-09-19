package dev.quarris.engulfingdarkness.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "renderHeart", at = @At("HEAD"))
    private void purpleinator(PoseStack pPoseStack, Gui.HeartType pHeartType, int pX, int pY, int p_168705_, boolean pRenderHighlight, boolean pHalfHeart, CallbackInfo ci) {
        if (Minecraft.getInstance().player.hasEffect(EffectSetup.BUSTED.get())) {
            RenderSystem.setShaderColor(0.7f, 0.1f, 0.9f, 1);
        }
    }

}
