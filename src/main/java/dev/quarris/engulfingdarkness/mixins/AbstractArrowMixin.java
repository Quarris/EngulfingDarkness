package dev.quarris.engulfingdarkness.mixins;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

    @Shadow @Final private static EntityDataAccessor<Byte> PIERCE_LEVEL;

    protected AbstractArrowMixin(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "setPierceLevel", at = @At("HEAD"), cancellable = true)
    public void increaseLevelFromEffect(byte pPierceLevel, CallbackInfo ci) {
        if (this.getOwner() instanceof LivingEntity owner && owner.hasEffect(EffectSetup.PIERCER.get())) {
            this.entityData.set(PIERCE_LEVEL, (byte) (pPierceLevel + owner.getEffect(EffectSetup.PIERCER.get()).getAmplifier() + 1));
            ci.cancel();
        }
    }

}
