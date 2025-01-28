package dev.quarris.engulfingdarkness.darkness.burnout;

import com.google.gson.JsonObject;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

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
        Set<Object2IntMap.Entry<Holder<Enchantment>>> enchants = stack.getEnchantments().entrySet();
        for (var entry : enchants) {
            if (entry.getKey().is(EnchantmentTags.CURSE)) {
                return true;
            }
        }

        return false;
    }

    private static void removeCurse(ItemStack stack) {
        ItemEnchantments enchants = stack.getEnchantments();
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchants);
        List<Holder<Enchantment>> curses = new ArrayList<>();
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchants.entrySet()) {
            if (entry.getKey().is(EnchantmentTags.CURSE)) {
                curses.add(entry.getKey());
            }
        }

        if (curses.isEmpty()) {
            return;
        }

        Collections.shuffle(curses);
        mutable.removeIf(e -> e.is(curses.getFirst()));
        EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
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
