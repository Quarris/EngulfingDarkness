package dev.quarris.engulfingdarkness.effect;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BustedMobEffect extends MobEffect {

    public BustedMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x552bbd);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    public static boolean shouldClearEffect(MobEffect effect) {
        return effect.isBeneficial() && effect != EffectSetup.SOUL_GUARD.get();
    }

    @Mod.EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void bustedHealing(LivingHealEvent event) {
            // Prevent healing if busted is in effect
            if (event.getEntity().hasEffect(EffectSetup.BUSTED.get())) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void cleansePositiveEffects(MobEffectEvent.Added event) {
            // Remove all positive effects when busted is added.
            if (event.getEffectInstance().getEffect() == EffectSetup.BUSTED.get()) {
                Iterator<MobEffectInstance> iterator = event.getEntity().getActiveEffects().iterator();
                while (iterator.hasNext()) {
                    MobEffectInstance effect = iterator.next();
                    if (shouldClearEffect(effect.getEffect())) {
                        event.getEntity().removeEffect(effect.getEffect());
                    }
                }
            }
        }

        @SubscribeEvent
        public static void preventPositiveEffects(MobEffectEvent.Applicable event) {
            // Deny all positive effects being applied if busted is in effect.
            if (event.getEntity().hasEffect(EffectSetup.BUSTED.get()) && shouldClearEffect(event.getEffectInstance().getEffect())) {
                event.setResult(Event.Result.DENY);
            }
        }

        @SubscribeEvent
        public static void preventRemovingNegativeEffects(MobEffectEvent.Remove event) {
            // Prevent cleansing of negative effects when busted is in effect.
            if (event.getEntity().hasEffect(EffectSetup.BUSTED.get()) && !shouldClearEffect(event.getEffect())) {
                event.setCanceled(true);
            }
        }
    }
}
