package dev.quarris.engulfingdarkness.capability;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DarknessCapability implements ICapabilitySerializable<CompoundNBT> {

    public static final ResourceLocation KEY = ModRef.res("darkness");

    @CapabilityInject(IDarkness.class)
    public static Capability<IDarkness> INST;

    public static void register() {
        CapabilityManager.INSTANCE.register(IDarkness.class, new Capability.IStorage<IDarkness>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IDarkness> capability, IDarkness instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<IDarkness> capability, IDarkness instance, Direction side, INBT nbt) {
                instance.deserializeNBT((CompoundNBT) nbt);
            }
        }, Darkness::new);
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
    public CompoundNBT serializeNBT() {
        return this.lazyThis.map(INBTSerializable::serializeNBT).orElse(new CompoundNBT());
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.lazyThis.ifPresent(darkness -> darkness.deserializeNBT(nbt));
    }
}
