package dev.quarris.engulfingdarkness.mixins;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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

    // PIERCER
    // Effectively reduces armor by 75%
    @Inject(method = "getDamageAfterArmorAbsorb", at = @At(value = "HEAD"), cancellable = true)
    private void livingentity_ignoreArmorFromPiercer(DamageSource pDamageSource, float pDamageAmount, CallbackInfoReturnable<Float> cir) {
        if (pDamageSource.isBypassArmor() || !(pDamageSource.getEntity() instanceof LivingEntity shooter)) {
            return;
        }

        // Only modify output if the shooter has piercer, else skip and use default logic
        if (shooter.hasEffect(EffectSetup.PIERCER.get())) {
            this.hurtArmor(pDamageSource, pDamageAmount);
            cir.setReturnValue(CombatRules.getDamageAfterAbsorb(pDamageAmount, this.getArmorValue() * 0.75f, (float) this.getAttributeValue(Attributes.ARMOR_TOUGHNESS) * 0.75f));
        }
    }

    // EASY TARGET
    // Forces line of sight if the target has Easy Target effect, ignoring walls.
    @Inject(method = "hasLineOfSight", at = @At(value = "RETURN", ordinal = 2), cancellable = true)
    private void livingentity_forceLineOfSightFromEasyTarget(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && pEntity instanceof LivingEntity entity && entity.hasEffect(EffectSetup.EASY_TARGET.get())) {
            cir.setReturnValue(true);
        }
    }
}
