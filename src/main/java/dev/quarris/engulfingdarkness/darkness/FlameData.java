package dev.quarris.engulfingdarkness.darkness;

import net.minecraft.nbt.CompoundTag;

public class FlameData {
    private final LightBringer light;
    private int flame;

    public FlameData(LightBringer light) {
        this.light = light;
        this.flame = light.maxFlame();
    }

    public float getLife() {
        return this.flame / (float) this.light.maxFlame();
    }

    public int burn(int burnedFlame) {
        this.flame -= burnedFlame;
        return this.flame;
    }

    public boolean isBurned() {
        return this.flame <= 0;
    }

    public void reset() {
        this.flame = this.light.maxFlame();
    }

    public void saveTo(CompoundTag tag) {
        tag.putInt("Flame", this.flame);
    }

    public void loadFrom(CompoundTag tag) {
        this.flame = tag.getInt("Flame");
    }

    public void set(int flame) {
        this.flame = flame;
    }

    public int getFlame() {
        return this.flame;
    }
}
