package dev.quarris.engulfingdarkness.effect;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BustedMobEffect extends MobEffect {

    public BustedMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x552bbd);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    public static boolean shouldClearEffect(Holder<MobEffect> effect) {
        return effect.get().isBeneficial() && effect != EffectSetup.SOUL_GUARD.get() && effect != EffectSetup.SENTINEL_PROTECTION.get() && effect != EffectSetup.SOUL_VEIL.get();
    }

    @Mod.EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void bustedHealing(LivingHealEvent event) {
            // Prevent healing if busted is in effect
            if (event.getEntity().hasEffect(EffectSetup.BUSTED.getHolder().get())) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void cleansePositiveEffects(MobEffectEvent.Added event) {
            // Remove all positive effects when busted is added.
            if (event.getEffectInstance().getEffect() == EffectSetup.BUSTED.get()) {
                Set<Holder<MobEffect>> effects = event.getEntity().getActiveEffects().stream().map(MobEffectInstance::getEffect).filter(BustedMobEffect::shouldClearEffect).collect(Collectors.toSet());
                for (var effect : effects) {
                    event.getEntity().removeEffect(effect);
                }
            }
        }

        @SubscribeEvent
        public static void preventPositiveEffects(MobEffectEvent.Applicable event) {
            // Deny all positive effects being applied if busted is in effect.
            if (event.getEntity().hasEffect(EffectSetup.BUSTED.getHolder().get()) && shouldClearEffect(event.getEffectInstance().getEffect())) {
                event.setResult(Event.Result.DENY);
            }
        }

        @SubscribeEvent
        public static void preventRemovingNegativeEffects(MobEffectEvent.Remove event) {
            // Prevent cleansing of negative effects when busted is in effect.
            // Added busted as able to be removed to allow /effect command clearing.
            if (event.getEntity().hasEffect(EffectSetup.BUSTED.getHolder().get()) && !(event.getEffect().isBeneficial() || event.getEffect() == EffectSetup.BUSTED.get())) {
                event.setCanceled(true);
            }
        }
    }
}
