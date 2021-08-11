package dev.quarris.engulfingdarkness.capability;

import dev.quarris.engulfingdarkness.EngulfingDarkness;
import dev.quarris.engulfingdarkness.ModConfigs;
import dev.quarris.engulfingdarkness.packets.EnteredDarknessMessage;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.server.ServerWorld;

public class Darkness implements IDarkness {

    private boolean isInDarkness;
    private float darknessLevel;
    private float dangerLevel;

    @Override
    public void tick(PlayerEntity player) {
        // Send packet to client to update whether they are in darkness
        // since clients light levels don't get fully updated
        if (!player.world.isRemote) {
            int light = player.world.getLight(player.getPosition());
            if (this.isInDarkness && light > ModConfigs.darknessLightLevel.get()) {
                this.setInDarkness(false);
                PacketHandler.sendToClient(new EnteredDarknessMessage(false), player);
            } else if (!this.isInDarkness && light <= ModConfigs.darknessLightLevel.get()) {
                this.setInDarkness(true);
                PacketHandler.sendToClient(new EnteredDarknessMessage(true), player);
            }
        }

        // Update the darkness and danger levels
        if (this.isInDarkness) {
            this.darknessLevel = (float) Math.min(this.darknessLevel + 0.01, 1);
            if (this.darknessLevel == 1.0) {
                this.dangerLevel = (float) Math.min(this.dangerLevel + 0.03, 1);
            }
        } else {
            this.darknessLevel = (float) Math.max(this.darknessLevel - 0.03, 0);
            this.dangerLevel = 0;
        }

        // Spawn Particles
        if (!player.world.isRemote && this.darknessLevel > 0.0) {
            double rx = (-1 + Math.random() * 2) * 0.3;
            double ry = Math.random();
            double rz = (-1 + Math.random() * 2) * 0.3;
            double mx = player.getRNG().nextGaussian() * 0.2;
            double my = player.getRNG().nextGaussian() * 0.2;
            double mz = player.getRNG().nextGaussian() * 0.2;
            ((ServerWorld) player.world).spawnParticle(ParticleTypes.SMOKE, player.getPosX() + rx, player.getPosY() + ry, player.getPosZ() + rz, (int) (this.darknessLevel * 10), mx, my, mz, 0.01);
        }

        // Play sound to player
        if (player.world.isRemote && this.darknessLevel == 1.0 && player.getRNG().nextDouble() > 0.95) {
            player.playSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, SoundCategory.AMBIENT, 1, 0.1f);
        }

        // Damage player
        if (!player.world.isRemote && this.dangerLevel == 1.0) {
            player.attackEntityFrom(EngulfingDarkness.damageSource, 4);
        }
    }

    @Override
    public void setInDarkness(boolean inDarkness) {
        this.isInDarkness = inDarkness;
    }

    @Override
    public float getDarkness() {
        return this.darknessLevel;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("Darkness", this.darknessLevel);
        nbt.putFloat("Danger", this.dangerLevel);
        nbt.putBoolean("IsInDarkness", this.isInDarkness);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.darknessLevel = nbt.getFloat("Darkness");
        this.dangerLevel = nbt.getFloat("Danger");
        this.isInDarkness = nbt.getBoolean("IsInDarkness");
    }
}
