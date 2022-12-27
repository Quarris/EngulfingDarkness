package dev.quarris.engulfingdarkness.capability;

import dev.quarris.engulfingdarkness.EnchantmentUtils;
import dev.quarris.engulfingdarkness.ModConfigs;
import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.ModRegistry;
import dev.quarris.engulfingdarkness.packets.EnteredDarknessMessage;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public class Darkness implements IDarkness {

    private float burnout;

    public Darkness() {
        this.burnout = 32;
    }

    @Override
    public float getBurnout() {
        return this.burnout;
    }

    // OLD
    private boolean isInDarkness;
    private float darknessLevel;
    private float dangerLevel;

    @Override
    public void tick(Player player) {
        // Send packet to client to update whether they are in darkness
        // since clients light levels don't get fully updated
        this.updatePlayerInDarkness(player);

        this.updateBurnout(player);
        this.updateDarknessLevels(player);
        this.spawnParticles(player);
        this.playSounds(player);
        this.dealDarknessDamage(player);
    }

    private void updatePlayerInDarkness(Player player) {
        if (!player.level.isClientSide() && ModConfigs.isAllowed(player.level.dimension().location())) {
            if (this.isResistant(player)) {
                this.setInDarkness(false);
                PacketHandler.sendToClient(new EnteredDarknessMessage(false), player);
            } else {
                int light = player.level.getMaxLocalRawBrightness(new BlockPos(player.getEyePosition(1)));
                if (this.isInDarkness && (this.isResistant(player) || light > ModConfigs.darknessLightLevel.get())) {
                    this.setInDarkness(false);
                    PacketHandler.sendToClient(new EnteredDarknessMessage(false), player);
                } else if (!player.isCreative() && !this.isInDarkness && light <= ModConfigs.darknessLightLevel.get()) {
                    this.setInDarkness(true);
                    PacketHandler.sendToClient(new EnteredDarknessMessage(true), player);
                }
            }
        }
    }

    private void updateBurnout(Player player) {
        if (!this.isInDarkness) return;
        ItemStack held = this.getHeldLight(player);
        if (held.isEmpty()) return;

        float consumedBurnout = this.calculateConsumedBurnout(player.level, player, held) / 20f;

        this.burnout = Math.max(this.burnout - consumedBurnout, 0);

        if (this.burnout <= 0) {
            this.burnout = MAX_BURNOUT;

            if (!player.level.isClientSide()) {
                if (held.isDamageableItem()) {
                    held.hurtAndBreak(1, player, p -> {
                    });
                } else {
                    held.shrink(1);
                }
            }
        }
    }

    private void updateDarknessLevels(Player player) {
        // If not in darkness, reset the levels.
        ItemStack heldLight = this.getHeldLight(player);

        double darknessTimer = ModConfigs.darknessTimer.get();
        double dangerTimer = ModConfigs.dangerTimer.get();
        int valianceLevel = EnchantmentUtils.getEnchantment(player, ModRegistry.Enchantments.VALIANCE.get(), EquipmentSlot.HEAD);
        if (valianceLevel > 0) {
            darknessTimer += 2 * valianceLevel;
            dangerTimer += 2 * valianceLevel;
        }

        if (!this.isInDarkness || (!heldLight.isEmpty() && this.burnout > 0)) {
            this.darknessLevel = (float) Math.max(this.darknessLevel - 3 / (darknessTimer * 20), 0);
            this.dangerLevel = 0;
            return;
        }

        // Update the darkness and danger levels when in darkness
        if (heldLight.isEmpty() || this.burnout <= 0) {
            this.darknessLevel = (float) Math.min(this.darknessLevel + 1 / (darknessTimer * 20), 1);
            if (this.darknessLevel == 1.0) {
                this.dangerLevel = (float) Math.min(this.dangerLevel + 1 / (dangerTimer * 20), 1);
            }
        }

    }


    private void spawnParticles(Player player) {
        if (!player.level.isClientSide() && this.isInDarkness && this.getHeldLight(player).isEmpty()) {
            double rx = (-1 + Math.random() * 2) * 0.3;
            double ry = Math.random();
            double rz = (-1 + Math.random() * 2) * 0.3;
            double mx = player.getRandom().nextGaussian() * 0.2;
            double my = player.getRandom().nextGaussian() * 0.2;
            double mz = player.getRandom().nextGaussian() * 0.2;
            ((ServerLevel) player.level).sendParticles(ParticleTypes.SMOKE, player.getX() + rx, player.getY() + ry, player.getZ() + rz, (int) (this.darknessLevel * 10), mx, my, mz, 0.01);
        }
    }

    private void playSounds(Player player) {
        if (player.level.isClientSide() && this.darknessLevel == 1.0 && player.getRandom().nextDouble() > 0.95) {
            player.playSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD.value(), 1, 0.1f);
        }
    }

    private void dealDarknessDamage(Player player) {
        if (this.dangerLevel == 1.0 && ModConfigs.darknessDamage.get() != 0) {
            float perc = ModConfigs.darknessDamage.get().floatValue() / 20;
            player.hurt(ModRef.DARKNESS_DAMAGE, player.getMaxHealth() * perc);
        }
    }

    private ItemStack getHeldLight(Player player) {

        ItemStack held = player.getMainHandItem();
        if (held.is(ModRef.Tags.LIGHT) || held.is(ModRef.Tags.SOUL_LIGHT)) {
            return held;
        }

        held = player.getOffhandItem();
        if (held.is(ModRef.Tags.LIGHT) || held.is(ModRef.Tags.SOUL_LIGHT)) {
            return held;
        }

        return ItemStack.EMPTY;
    }

    private float calculateConsumedBurnout(Level level, Player player, ItemStack light) {
        int consumed = 2;
        if (isPlayerInRain(level, player)) {
            consumed = 4;
        }
        if (player.isUnderWater()) {
            consumed = 16;
        }

        if (light.is(ModRef.Tags.SOUL_LIGHT)) {
            consumed /= 2;
        }
        return consumed;
    }

    private static boolean isPlayerInRain(Level level, Player player) {
        BlockPos pos = player.blockPosition().above();
        if (!level.isRaining()) {
            return false;
        } else if (!level.canSeeSky(pos)) {
            return false;
        } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        } else {
            Biome biome = level.getBiome(pos).value();
            return biome.getPrecipitation() != Biome.Precipitation.NONE;
        }
    }

    @Override
    public boolean isInDarkness() {
        return this.isInDarkness;
    }

    @Override
    public float getDanger() {
        return this.dangerLevel;
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
    public boolean isResistant(Player player) {
        return player.isCreative() || player.hasEffect(ModRegistry.Effects.VEILED.get());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("Burnout", this.burnout);
        nbt.putFloat("Darkness", this.darknessLevel);
        nbt.putFloat("Danger", this.dangerLevel);
        nbt.putBoolean("IsInDarkness", this.isInDarkness);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        //this.burnout = nbt.getFloat("Burnout");
        this.darknessLevel = nbt.getFloat("Darkness");
        this.dangerLevel = nbt.getFloat("Danger");
        this.isInDarkness = nbt.getBoolean("IsInDarkness");
    }
}
