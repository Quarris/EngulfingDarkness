package dev.quarris.engulfingdarkness.datagen.server;

import dev.quarris.engulfingdarkness.enchantment.SoulSentinelEnchantment;
import dev.quarris.engulfingdarkness.registry.EnchantmentSetup;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentGen {

    public static void bootstrap(BootstrapContext<Enchantment> pContext) {
        HolderGetter<Item> items = pContext.lookup(Registries.ITEM);
        pContext.register(EnchantmentSetup.VALIANCE, Enchantment.enchantment(
            Enchantment.definition(
                items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                2,
                4,
                Enchantment.dynamicCost(25, 25),
                Enchantment.dynamicCost(75, 25),
                4,
                EquipmentSlotGroup.CHEST
            )
        ).build(EnchantmentSetup.VALIANCE.location()));

        pContext.register(EnchantmentSetup.SOUL_SENTINEL, Enchantment.enchantment(
            Enchantment.definition(
                items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                3,
                3,
                Enchantment.constantCost(1),
                Enchantment.constantCost(41),
                4,
                EquipmentSlotGroup.HEAD
            )
        ).build(EnchantmentSetup.SOUL_SENTINEL.location()));
    }
}
