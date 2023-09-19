package dev.quarris.engulfingdarkness.darkness;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LightBringer {
    public static final int MAX_FLAME = 640;
    private final Item item;
    private int flame;

    public LightBringer(Item item) {
        this.item = item;
    }

    public void tick(ItemStack stack) {

    }

    public float getLife() {
        return this.flame / (float) MAX_FLAME;
    }

    public int burn(int burnedFlame) {
        this.flame -= burnedFlame;
        return this.flame;
    }

    public boolean isBurned() {
        return this.flame <= 0;
    }

    public void reset() {
        this.flame = MAX_FLAME;
    }

    public void saveTo(CompoundTag tag) {
        tag.putInt("Flame", this.flame);
    }

    public void loadFrom(CompoundTag tag) {
        this.flame = tag.getInt("Flame");
    }
}
