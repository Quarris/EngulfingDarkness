package dev.quarris.engulfingdarkness.mixins;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow protected abstract void hurtArmor(DamageSource pDamageSource, float pDamageAmount);

    @Shadow public abstract int getArmorValue();

    @Shadow public abstract double getAttributeValue(Holder<Attribute> pAttribute);

    // PIERCER
    // Effectively reduces armor by 75%
    @Inject(method = "getDamageAfterArmorAbsorb", at = @At(value = "HEAD"), cancellable = true)
    private void livingentity_ignoreArmorFromPiercer(DamageSource pDamageSource, float pDamageAmount, CallbackInfoReturnable<Float> cir) {
        if (pDamageSource.is(DamageTypeTags.BYPASSES_ARMOR) || !(pDamageSource.getEntity() instanceof LivingEntity shooter)) {
            return;
        }

        // Only modify output if the shooter has piercer, else skip and use default logic
        if (shooter.hasEffect(EffectSetup.PIERCER.getHolder().get())) {
            this.hurtArmor(pDamageSource, pDamageAmount);
            cir.setReturnValue(CombatRules.getDamageAfterAbsorb((LivingEntity) (Object) this, pDamageAmount, pDamageSource, this.getArmorValue() * 0.75f, (float) this.getAttributeValue(Attributes.ARMOR_TOUGHNESS) * 0.75f));
        }
    }

    // EASY TARGET
    // Forces line of sight if the target has Easy Target effect, ignoring walls.
    @Inject(method = "hasLineOfSight(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/ClipContext$Block;Lnet/minecraft/world/level/ClipContext$Fluid;D)Z", at = @At(value = "RETURN", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void livingentity_forceLineOfSightFromEasyTarget(Entity pEntity, ClipContext.Block pBlock, ClipContext.Fluid pFluid, double pY, CallbackInfoReturnable<Boolean> cir, Vec3 thisPos, Vec3 targetPos) {
        if (!cir.getReturnValueZ() && pEntity instanceof LivingEntity entity && entity.hasEffect(EffectSetup.EASY_TARGET.getHolder().get()) && thisPos.distanceTo(targetPos) > 128.0) {
            cir.setReturnValue(true);
        }
    }
}
