package dev.quarris.engulfingdarkness.effect;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EasyTargetEffect extends MobEffect {

    public EasyTargetEffect() {
        super(MobEffectCategory.HARMFUL, 0x5a1c8c);
    }

    @Mod.EventBusSubscriber
    public static class Events {

        private static final TargetingConditions EASY_TARGET_TEST = TargetingConditions.forCombat().ignoreLineOfSight().selector((e, l) -> e.hasEffect(EffectSetup.EASY_TARGET.getHolder().get()));

        @SubscribeEvent
        public static void focusEasyTarget(LivingEvent.LivingTickEvent event) {
            if (!(event.getEntity() instanceof Mob mob) || mob.getRandom().nextFloat() < 0.1) {
                return;
            }

            LivingEntity target = mob.getTarget();
            if (target == null || target.hasEffect(EffectSetup.EASY_TARGET.getHolder().get())) {
                return;
            }

            LivingEntity entity = event.getEntity();
            Level level = entity.level();
            if (!(level instanceof ServerLevel serverLevel)) return;
            double followRange = event.getEntity().getAttributeValue(Attributes.FOLLOW_RANGE);
            List<Player> players = level.players().stream().filter(e -> EASY_TARGET_TEST.range(followRange).test(serverLevel, entity, e)).collect(Collectors.toList());
            if (players.isEmpty()) return;
            players.sort(Comparator.comparingDouble(entity::distanceToSqr));
            mob.setTarget(players.getFirst());
        }

        @SubscribeEvent
        public static void increaseDamageTaken(LivingDamageEvent event) {
            if (!event.getEntity().hasEffect(EffectSetup.EASY_TARGET.getHolder().get())) {
                return;
            }

            event.setAmount(event.getAmount() * 1.5f);
        }
    }
}
