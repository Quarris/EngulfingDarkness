package dev.quarris.engulfingdarkness;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentUtils {
    public static boolean hasEnchantment(Player player, Holder<Enchantment> enchantment) {
        return getEnchantment(player, enchantment) > 0;
    }

    public static boolean hasEnchantment(Player player, Holder<Enchantment> enchantment, EquipmentSlot... slots) {
        return getEnchantment(player, enchantment, slots) > 0;
    }

    public static int getEnchantment(Player player, Holder<Enchantment> enchantment) {
        return getEnchantment(player, enchantment, EquipmentSlot.VALUES.toArray(new EquipmentSlot[0]));
    }

    public static int getEnchantment(Player player, Holder<Enchantment> enchantment, EquipmentSlot... slots) {
        for (EquipmentSlot slot : slots) {
            int level = player.getItemBySlot(slot).getEnchantments().getLevel(enchantment);
            if (level > 0) {
                return level;
            }
        }

        return 0;
    }
}
