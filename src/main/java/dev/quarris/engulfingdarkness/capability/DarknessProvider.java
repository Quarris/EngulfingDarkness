package dev.quarris.engulfingdarkness.capability;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.darkness.Darkness;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DarknessProvider implements ICapabilitySerializable<CompoundTag> {

    public static final ResourceLocation KEY = ModRef.res("darkness");

    private final LazyOptional<Darkness> lazyThis;

    public DarknessProvider(Player player) {
        this.lazyThis = LazyOptional.of(() -> new Darkness(player));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ModRef.Capabilities.DARKNESS) {
            return this.lazyThis.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return this.lazyThis.map(darkness -> darkness.serializeNBT(provider)).orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.lazyThis.ifPresent(darkness -> darkness.deserializeNBT(provider, tag));
    }
}
