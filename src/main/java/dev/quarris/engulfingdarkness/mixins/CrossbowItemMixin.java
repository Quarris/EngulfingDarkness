package dev.quarris.engulfingdarkness.mixins;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CrossbowItem.class)
public class CrossbowItemMixin {
    
    @Inject(method = "getArrow", at = @At("RETURN"))
    private static void setPierceLevelFromEffect(Level pLevel, LivingEntity pLivingEntity, ItemStack pCrossbowStack, ItemStack pAmmoStack, CallbackInfoReturnable<AbstractArrow> cir) {
        if (pLivingEntity.hasEffect(EffectSetup.PIERCER.get())) {
            int piercerLevel = pLivingEntity.getEffect(EffectSetup.PIERCER.get()).getAmplifier() + 1;
            cir.getReturnValue().setPierceLevel((byte) (cir.getReturnValue().getPierceLevel() + piercerLevel));
        }
    }
    
}
