package dev.quarris.engulfingdarkness.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class SentinelProtectionEffect extends MobEffect {

    public SentinelProtectionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xfcba03);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
