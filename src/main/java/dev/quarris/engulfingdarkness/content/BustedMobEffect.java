package dev.quarris.engulfingdarkness.content;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class BustedMobEffect extends MobEffect {

    public BustedMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xffcd61);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }
}
