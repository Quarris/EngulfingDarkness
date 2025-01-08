package dev.quarris.engulfingdarkness.darkness.burnout;

import com.google.gson.JsonObject;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.*;

public class RemoveCurseEffect extends BurnoutEffect<RemoveCurseEffect.Serializer> {

    public static final RemoveCurseEffect.Serializer SERIALIZER = new RemoveCurseEffect.Serializer();

    private final float chance;
    public RemoveCurseEffect(float chance) {
        this.chance = chance;
    }

    @Override
    public void onBurnout(Player player, ItemStack stack, LightBringer lightBringer) {
        if (player.getRandom().nextFloat() >= this.chance) {
            return;
        }

        Inventory inventory = player.getInventory();
        List<Integer> cursedItemSlots = new ArrayList<>();

        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack testStack = inventory.getItem(slot);
            if (isCursed(testStack)) {
                cursedItemSlots.add(slot);
            }
        }

        if (cursedItemSlots.isEmpty()) {
            return;
        }

        Collections.shuffle(cursedItemSlots);
        removeCurse(inventory.getItem(cursedItemSlots.get(0)));
    }

    private static boolean isCursed(ItemStack stack) {
        Map<Enchantment, Integer> enchants = stack.getAllEnchantments();
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            if (entry.getKey().isCurse()) {
                return true;
            }
        }

        return false;
    }

    private static void removeCurse(ItemStack stack) {
        Map<Enchantment, Integer> enchants = stack.getAllEnchantments();
        List<Enchantment> curses = new ArrayList<>();
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            if (entry.getKey().isCurse()) {
                curses.add(entry.getKey());
            }
        }

        if (curses.isEmpty()) {
            return;
        }

        Collections.shuffle(curses);
        enchants.remove(curses.get(0));
        EnchantmentHelper.setEnchantments(enchants, stack);
    }

    @Override
    public RemoveCurseEffect.Serializer getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer extends BurnoutEffect.Serializer<RemoveCurseEffect> {

        @Override
        public JsonObject serialize(RemoveCurseEffect effect) {
            JsonObject json = new JsonObject();
            json.addProperty("chance", effect.chance);
            return json;
        }

        @Override
        public RemoveCurseEffect deserialize(JsonObject json) {
            float chance = json.get("chance").getAsFloat();
            return new RemoveCurseEffect(chance);
        }

        @Override
        public String id() {
            return "remove_curse";
        }
    }
}
