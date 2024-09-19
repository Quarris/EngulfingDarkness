package dev.quarris.engulfingdarkness.effect;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;
import java.util.List;

public class EasyTargetEffect extends MobEffect {

    public EasyTargetEffect() {
        super(MobEffectCategory.HARMFUL, 0x5a1c8c);
    }

    @Mod.EventBusSubscriber
    public static class Events {

        private static final TargetingConditions EASY_TARGET_TEST = TargetingConditions.forCombat().ignoreLineOfSight().selector(e -> e.hasEffect(EffectSetup.EASY_TARGET.get()));

        @SubscribeEvent
        public static void focusEasyTarget(LivingEvent.LivingTickEvent event) {
            if (!(event.getEntity() instanceof Mob mob) || mob.getRandom().nextFloat() < 0.1) {
                return;
            }

            LivingEntity target = mob.getTarget();
            if (target == null || target.hasEffect(EffectSetup.EASY_TARGET.get())) {
                return;
            }

            LivingEntity entity = event.getEntity();
            Level level = entity.getLevel();
            double followRange = event.getEntity().getAttributeValue(Attributes.FOLLOW_RANGE);
            List<Player> players = level.getNearbyPlayers(EASY_TARGET_TEST.range(followRange), event.getEntity(), entity.getBoundingBox().inflate(followRange, 4, followRange));
            if (players.isEmpty()) return;
            players.sort(Comparator.comparingDouble(entity::distanceToSqr));
            mob.setTarget(players.get(0));
        }

        @SubscribeEvent
        public static void increaseDamageTaken(LivingDamageEvent event) {
            if (!event.getEntity().hasEffect(EffectSetup.EASY_TARGET.get())) {
                return;
            }

            event.setAmount(event.getAmount() * 1.5f);
        }
    }
}
