package dev.quarris.engulfingdarkness.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public interface IDarkness extends INBTSerializable<CompoundTag> {

    float MAX_BURNOUT = 32.0f;

    float getDarkness();
    float getBurnout();

    void setInDarkness(boolean inDarkness);

    boolean isResistant(Player player);

    void tick(Player player);

    boolean isInDarkness();

    float getDanger();

    void resetBurnout(Player player);

    void syncToClient(Player player);
}
