package dev.quarris.engulfingdarkness.capability;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DarknessCapability implements ICapabilitySerializable<CompoundTag> {

    public static final ResourceLocation KEY = ModRef.res("darkness");

    public static final Capability<IDarkness> INST = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IDarkness.class);
    }

    private final LazyOptional<IDarkness> lazyThis;

    public DarknessCapability(IDarkness darkness) {
        this.lazyThis = LazyOptional.of(() -> darkness);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == INST) {
            return lazyThis.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.lazyThis.map(INBTSerializable::serializeNBT).orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.lazyThis.ifPresent(darkness -> darkness.deserializeNBT(nbt));
    }
}
