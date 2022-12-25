package dev.quarris.engulfingdarkness.content;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ValianceEnchantment extends Enchantment {
    protected ValianceEnchantment(Rarity rarity) {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_HEAD, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }
}
