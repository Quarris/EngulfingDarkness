package dev.quarris.engulfingdarkness.mixins;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin<T extends LivingEntity> extends TargetGoal {

    @Shadow @Final protected Class<T> targetType;

    @Shadow @Nullable protected LivingEntity target;

    @Shadow protected abstract AABB getTargetSearchArea(double pTargetDistance);

    @Shadow protected TargetingConditions targetConditions;

    public NearestAttackableTargetGoalMixin(Mob pMob, boolean pMustSee) {
        super(pMob, pMustSee);
    }

    @Inject(method = "findTarget", at = @At("HEAD"))
    private void nearestattackabletargetgoal_focusEasyTargets(CallbackInfo ci) {
        if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
            this.target = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance() * 3.0), (entity) -> entity.hasEffect(EffectSetup.EASY_TARGET.get())), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        } else {
            this.target = this.mob.level.getNearestPlayer(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.getFollowDistance() * 3.0, (entity) -> entity instanceof LivingEntity living && living.hasEffect(EffectSetup.EASY_TARGET.get()) && this.targetConditions.copy().range(this.getFollowDistance() * 3.0).test(this.mob, living));
        }
    }

}
