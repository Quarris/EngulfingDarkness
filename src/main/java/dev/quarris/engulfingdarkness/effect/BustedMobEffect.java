package dev.quarris.engulfingdarkness.effect;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

public class BustedMobEffect extends MobEffect {

    public BustedMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x552bbd);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    @Mod.EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void bustedHealing(LivingHealEvent event) {
            if (event.getEntity().hasEffect(EffectSetup.BUSTED.get())) {
                event.setCanceled(true);
            }
        }
    }
}
