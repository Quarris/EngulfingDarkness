package dev.quarris.engulfingdarkness.darkness;

import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public record LightBringer(Item item, int maxFlame) {

    public static final Map<Item, LightBringer> REGISTRY = new HashMap<>();

    public static void addLightBringer(Item item, int maxFlame) {
        LightBringer lightBringer = new LightBringer(item, maxFlame);
        REGISTRY.put(item, lightBringer);
    }

    public static LightBringer getLightBringer(Item item) {
        return REGISTRY.get(item);
    }
}
