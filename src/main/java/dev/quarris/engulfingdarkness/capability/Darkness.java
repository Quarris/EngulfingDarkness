package dev.quarris.engulfingdarkness.capability;

import dev.quarris.engulfingdarkness.EngulfingDarkness;
import dev.quarris.engulfingdarkness.ModConfigs;
import dev.quarris.engulfingdarkness.packets.EnteredDarknessMessage;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;

public class Darkness implements IDarkness {

    private boolean isInDarkness;
    private float darknessLevel;
    private float dangerLevel;

    @Override
    public void tick(Player player) {
        // Send packet to client to update whether they are in darkness
        // since clients light levels don't get fully updated
        if (!player.level.isClientSide() && ModConfigs.isAllowed(player.level.dimension().location())) {
            if (player.isCreative() && this.isInDarkness) {
                this.setInDarkness(false);
                PacketHandler.sendToClient(new EnteredDarknessMessage(false), player);
                return;
            } else {
                int light = player.level.getMaxLocalRawBrightness(new BlockPos(player.getEyePosition(1)));
                if (this.isInDarkness && (player.isCreative() || light > ModConfigs.darknessLightLevel.get())) {
                    this.setInDarkness(false);
                    PacketHandler.sendToClient(new EnteredDarknessMessage(false), player);
                } else if (!player.isCreative() && !this.isInDarkness && light <= ModConfigs.darknessLightLevel.get()) {
                    this.setInDarkness(true);
                    PacketHandler.sendToClient(new EnteredDarknessMessage(true), player);
                }
            }
        }

        // Update the darkness and danger levels
        if (this.isInDarkness) {
            this.darknessLevel = (float) Math.min(this.darknessLevel + ModConfigs.darknessLevelIncrement.get(), 1);
            if (this.darknessLevel == 1.0) {
                this.dangerLevel = (float) Math.min(this.dangerLevel + ModConfigs.dangerLevelIncrement.get(), 1);
            }
        } else {
            this.darknessLevel = (float) Math.max(this.darknessLevel - 3 * ModConfigs.darknessLevelIncrement.get(), 0);
            this.dangerLevel = 0;
        }

        // Spawn Particles
        if (!player.level.isClientSide() && this.darknessLevel > 0.0) {
            double rx = (-1 + Math.random() * 2) * 0.3;
            double ry = Math.random();
            double rz = (-1 + Math.random() * 2) * 0.3;
            double mx = player.getRandom().nextGaussian() * 0.2;
            double my = player.getRandom().nextGaussian() * 0.2;
            double mz = player.getRandom().nextGaussian() * 0.2;
            ((ServerLevel) player.level).sendParticles(ParticleTypes.SMOKE, player.getX() + rx, player.getY() + ry, player.getZ() + rz, (int) (this.darknessLevel * 10), mx, my, mz, 0.01);
        }

        // Play sound to player
        if (player.level.isClientSide() && this.darknessLevel == 1.0 && player.getRandom().nextDouble() > 0.95) {
            player.playSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 1, 0.1f);
        }

        // Damage player
        if (this.dangerLevel == 1.0 && ModConfigs.darknessDamage.get() != 0) {
            player.hurt(EngulfingDarkness.damageSource, ModConfigs.darknessDamage.get().floatValue());
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
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("Darkness", this.darknessLevel);
        nbt.putFloat("Danger", this.dangerLevel);
        nbt.putBoolean("IsInDarkness", this.isInDarkness);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.darknessLevel = nbt.getFloat("Darkness");
        this.dangerLevel = nbt.getFloat("Danger");
        this.isInDarkness = nbt.getBoolean("IsInDarkness");
    }
}
