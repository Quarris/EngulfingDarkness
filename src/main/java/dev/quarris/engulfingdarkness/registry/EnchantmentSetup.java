package dev.quarris.engulfingdarkness.registry;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentSetup {

    public static final ResourceKey<Enchantment> SOUL_SENTINEL = ResourceKey.create(Registries.ENCHANTMENT, ModRef.res("soul_sentinel"));
    public static final ResourceKey<Enchantment> VALIANCE = ResourceKey.create(Registries.ENCHANTMENT, ModRef.res("valiance"));


}
