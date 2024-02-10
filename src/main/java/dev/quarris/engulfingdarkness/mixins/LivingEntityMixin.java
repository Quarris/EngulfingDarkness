package dev.quarris.engulfingdarkness.mixins;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow protected abstract void hurtArmor(DamageSource pDamageSource, float pDamageAmount);

    @Shadow public abstract int getArmorValue();

    @Shadow public abstract double getAttributeValue(Attribute pAttribute);

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At(value = "HEAD"), cancellable = true)
    private void ignoreArmorFromPiercer(DamageSource pDamageSource, float pDamageAmount, CallbackInfoReturnable<Float> cir) {
        if (pDamageSource.isBypassArmor() || !(pDamageSource.getEntity() instanceof LivingEntity shooter)) {
            return;
        }

        // Only modify output if the shooter has piercer, else skip and use default logic
        if (shooter.hasEffect(EffectSetup.PIERCER.get())) {
            this.hurtArmor(pDamageSource, pDamageAmount);
            cir.setReturnValue(CombatRules.getDamageAfterAbsorb(pDamageAmount, this.getArmorValue() * 0.75f, (float) this.getAttributeValue(Attributes.ARMOR_TOUGHNESS) * 0.75f));
        }
    }

}
