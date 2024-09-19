package dev.quarris.engulfingdarkness.mixins;

import dev.quarris.engulfingdarkness.effect.CasterMobEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {

    @Shadow private int timeUntilLured;

    @Shadow @Nullable public abstract Player getPlayerOwner();

    @Inject(method = "catchingFish", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/projectile/FishingHook;timeUntilLured:I", opcode = Opcodes.PUTFIELD, ordinal = 3))
    private void lureTime(BlockPos pPos, CallbackInfo ci) {
        Player player = this.getPlayerOwner();
        if (player == null) {
            return;
        }

        int casterLevel = CasterMobEffect.getEffect(player);
        this.timeUntilLured *= (1 - CasterMobEffect.getLureReduction(casterLevel));
    }

}
