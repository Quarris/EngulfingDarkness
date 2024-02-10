package dev.quarris.engulfingdarkness.mixins;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public class ArrowItemMixin {

    @Inject(method = "createArrow", at = @At("RETURN"))
    public void setPierceLevelFromEffect(Level pLevel, ItemStack pStack, LivingEntity pShooter, CallbackInfoReturnable<AbstractArrow> cir) {
        if (pShooter.hasEffect(EffectSetup.PIERCER.get())) {
            int piercerLevel = pShooter.getEffect(EffectSetup.PIERCER.get()).getAmplifier() + 1;
            cir.getReturnValue().setPierceLevel((byte) (cir.getReturnValue().getPierceLevel() + piercerLevel));
        }
    }

}
