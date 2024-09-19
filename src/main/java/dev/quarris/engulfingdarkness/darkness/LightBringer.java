package dev.quarris.engulfingdarkness.darkness;

import dev.quarris.engulfingdarkness.configs.FlameConfigs;
import dev.quarris.engulfingdarkness.darkness.burnout.BurnoutEffect;
import net.minecraft.world.item.Item;

import java.util.*;

public final class LightBringer {

    private final Item item;
    private final boolean waterOnly;
    private final int maxFlame;
    private final int baseConsumption;
    private final float rainConsumptionMultiplier;
    private final float underwaterConsumptionMultiplier;
    private final List<BurnoutEffect<?>> effects;

    public LightBringer(Item item,
                        boolean waterOnly,
                        int maxFlame,
                        int baseConsumption,
                        float rainConsumptionMultiplier,
                        float underwaterConsumptionMultiplier,
                        List<BurnoutEffect<?>> effects) {
        this.item = item;
        this.waterOnly = waterOnly;
        this.maxFlame = maxFlame;
        this.baseConsumption = baseConsumption;
        this.rainConsumptionMultiplier = rainConsumptionMultiplier;
        this.underwaterConsumptionMultiplier = underwaterConsumptionMultiplier;
        this.effects = effects;
    }

    public LightBringer(Item item,
                        int maxFlame,
                        int baseConsumption,
                        float rainConsumptionMultiplier,
                        float underwaterConsumptionMultiplier,
                        List<BurnoutEffect<?>> effects) {
        this(item, false, maxFlame, baseConsumption, rainConsumptionMultiplier, underwaterConsumptionMultiplier, effects);
    }

    public static void addLightBringer(Item item, int maxFlame) {
        //LightBringer lightBringer = new LightBringer(item, maxFlame, Collections.emptyList());
        //REGISTRY.put(item, lightBringer);
    }

    public static LightBringer getLightBringer(Item item) {
        return FlameConfigs.LIGHT_BRINGERS.get(item);
    }

    public Item item() {
        return item;
    }

    public boolean isWaterOnly() {
        return this.waterOnly;
    }

    public int maxFlame() {
        return maxFlame;
    }

    public int baseConsumption() {
        return baseConsumption;
    }

    public float rainConsumptionMultiplier() {
        return rainConsumptionMultiplier;
    }

    public float underwaterConsumptionMultiplier() {
        return underwaterConsumptionMultiplier;
    }

    public List<BurnoutEffect<?>> effects() {
        return effects;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LightBringer) obj;
        return Objects.equals(this.item, that.item) &&
            this.waterOnly == that.waterOnly &&
            this.maxFlame == that.maxFlame &&
            this.baseConsumption == that.baseConsumption &&
            Float.floatToIntBits(this.rainConsumptionMultiplier) == Float.floatToIntBits(that.rainConsumptionMultiplier) &&
            Float.floatToIntBits(this.underwaterConsumptionMultiplier) == Float.floatToIntBits(that.underwaterConsumptionMultiplier) &&
            Objects.equals(this.effects, that.effects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, waterOnly, maxFlame, baseConsumption, rainConsumptionMultiplier, underwaterConsumptionMultiplier, effects);
    }

    @Override
    public String toString() {
        return "LightBringer[" +
            "item=" + item + ", " +
            "waterOnly=" + waterOnly + ", " +
            "maxFlame=" + maxFlame + ", " +
            "baseConsumption=" + baseConsumption + ", " +
            "rainConsumptionMultiplier=" + rainConsumptionMultiplier + ", " +
            "underwaterConsumptionMultiplier=" + underwaterConsumptionMultiplier + ", " +
            "effects=" + effects + ']';
    }

}
