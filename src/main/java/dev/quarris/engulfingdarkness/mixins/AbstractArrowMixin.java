package dev.quarris.engulfingdarkness.mixins;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

    protected AbstractArrowMixin(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "getPierceLevel", at = @At("RETURN"), cancellable = true)
    public void increaseLevelFromEffect(CallbackInfoReturnable<Byte> ci) {
        if (this.getOwner() instanceof LivingEntity owner && owner.hasEffect(EffectSetup.PIERCER.get())) {
            ci.setReturnValue((byte) (ci.getReturnValue() + owner.getEffect(EffectSetup.PIERCER.get()).getAmplifier() + 1));
        }
    }
}
