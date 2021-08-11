package dev.quarris.engulfingdarkness.capability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IDarkness extends INBTSerializable<CompoundNBT> {

    float getDarkness();
    void tick(PlayerEntity player);
    void setInDarkness(boolean inDarkness);
}
