package dev.quarris.engulfingdarkness.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

public interface IDarkness extends INBTSerializable<CompoundTag> {

    float getDarkness();
    void tick(Player player);
    void setInDarkness(boolean inDarkness);
}
