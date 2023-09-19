package dev.quarris.engulfingdarkness.darkness;

import dev.quarris.engulfingdarkness.EnchantmentUtils;
import dev.quarris.engulfingdarkness.ModConfigs;
import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.enchantment.SoulSentinelEnchantment;
import dev.quarris.engulfingdarkness.packets.EnteredDarknessMessage;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import dev.quarris.engulfingdarkness.packets.SyncDarknessMessage;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import dev.quarris.engulfingdarkness.registry.EnchantmentSetup;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectIntMutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Darkness implements IDarkness, INBTSerializable<CompoundTag> {

    private final Player player;
    private Pair<Player, Integer> sentinel;
    private final Map<Item, LightBringer> lightbringers = new HashMap<>();
    private boolean isInDarkness;
    private float darknessLevel;
    private float dangerLevel;
    private float burnoutModifier;

    public Darkness(Player player) {
        this.player = player;
    }

    @Override
    public float getFlameLife() {
        LightBringer light =this.getLight(this.getHeldLight());
        if (light != null) {
            return light.getLife();
        }

        return 0;
    }

    @Override
    public void tick() {
        // Send packet to client to update whether they are in darkness
        // since clients light levels don't get fully updated
        this.updatePlayerInDarkness();
        // Tries to find a nearby player to classify as a sentinel.
        this.findSentinel();
        // Updates the burnout meter
        this.updateFlame();
        // Updates the darkness and danger levels
        this.updateDarknessLevels();
        // Spawns particles when in darkness
        this.spawnParticles();
        // Plays sound once darkness fills all the way up
        this.playSounds();
        // Deals the damage (or redirects to sentinel) once the danger levels are all the way up.
        // If redirected to sentinel, applies Soul Veil to the player, and Busted to the sentinel.
        this.dealDarknessDamage();
        // Sync data to client.
        // TODO optimize server/client code to not have to sync the entire capability every tick
        this.syncToClient();
    }

    private void findSentinel() {
        this.sentinel = this.player.level.players().stream()
            .filter(p -> !this.player.getUUID().equals(p.getUUID()))
            .filter(p -> !p.hasEffect(EffectSetup.BUSTED.get()))
            .map(p -> new ObjectIntMutablePair<Player>(p, EnchantmentUtils.getEnchantment(p, EnchantmentSetup.SOUL_SENTINEL.get(), EquipmentSlot.CHEST)))
            .filter(p -> p.rightInt() > 0)
            .filter(p -> this.player.distanceTo(p.left()) <= SoulSentinelEnchantment.getDistance(p.rightInt()))
            .sorted(Comparator.<ObjectIntPair<Player>>comparingInt(ObjectIntPair::rightInt).thenComparing(p -> this.player.distanceTo(p.left())))
            .findFirst().orElse(null);

        if (this.sentinel != null) {
            this.player.addEffect(new MobEffectInstance(new MobEffectInstance(EffectSetup.SENTINEL_PROTECTION.get(), 5, this.sentinel.right() - 1, true, true)));
        }
    }

    private void updatePlayerInDarkness() {
        if (!this.player.level.isClientSide() && ModConfigs.isAllowed(this.player.level.dimension().location())) {
            if (this.isResistant()) {
                this.setInDarkness(false);
                PacketHandler.sendToClient(new EnteredDarknessMessage(false), this.player);
            } else {
                int light = player.level.getMaxLocalRawBrightness(new BlockPos(this.player.getEyePosition(1)));
                if (this.isInDarkness && (this.isResistant() || light > ModConfigs.darknessLightLevel.get())) {
                    this.setInDarkness(false);
                    PacketHandler.sendToClient(new EnteredDarknessMessage(false), this.player);
                } else if (!this.isInDarkness && !this.player.isCreative() && light <= ModConfigs.darknessLightLevel.get()) {
                    this.setInDarkness(true);
                    PacketHandler.sendToClient(new EnteredDarknessMessage(true), this.player);
                }
            }
        }
    }

    private void updateFlame() {
        ItemStack held = this.getHeldLight();
        this.burnoutModifier = 1;
        LightBringer lightBringer = this.getLight(held);
        if (lightBringer == null || !this.isInDarkness) return;

        float consumedFlame = this.calculateConsumedFlame(this.player.level, held);

        if (this.darknessLevel > 0.3) {
            this.burnoutModifier = Mth.lerp((this.darknessLevel - 0.3f) / 0.7f, 1, 6);
        }

        consumedFlame *= this.burnoutModifier;
        int burnedFlame = Mth.ceil(consumedFlame);
        int remainingFlame = lightBringer.burn(burnedFlame);
        // If the item has burned out, reset it and burn it for how much it went under its flame.
        if (remainingFlame <= 0) {
            lightBringer.reset();
            lightBringer.burn(Mth.abs(remainingFlame));
            if (!this.player.level.isClientSide()) {
                if (held.isDamageableItem()) {
                    held.hurtAndBreak(1, this.player, p -> {
                        p.level.playSound(null, p.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1, 1);
                    });
                } else {
                    held.shrink(1);
                    this.player.level.playSound(null, this.player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1, 1);
                }
                this.syncToClient();
            }
        }
    }

    private void updateDarknessLevels() {
        ItemStack heldLight = this.getHeldLight();

        double engulfTime = ModConfigs.darknessTimer.get();
        double dangerTimer = ModConfigs.dangerTimer.get();
        int valianceLevel = EnchantmentUtils.getEnchantment(this.player, EnchantmentSetup.VALIANCE.get(), EquipmentSlot.HEAD);
        if (valianceLevel > 0) {
            engulfTime += 2 * valianceLevel;
        }

        // If not in darkness, reset the levels.
        if (!this.isInDarkness || !heldLight.isEmpty()) {
            this.darknessLevel = (float) Math.max(this.darknessLevel - 1 / (engulfTime * 20), 0);
            this.dangerLevel = 0;
            return;
        }

        // Update the darkness and danger levels when in darkness
        if (heldLight.isEmpty() /*|| this.burnout <= 0*/) {
            this.darknessLevel = (float) Math.min(this.darknessLevel + 1 / (engulfTime * 20), 1);
            if (this.darknessLevel == 1.0) {
                this.dangerLevel = (float) Math.min(this.dangerLevel + 1 / (dangerTimer * 20), 1);
            }
        }
    }

    private void spawnParticles() {
        if (!this.player.level.isClientSide() && this.isInDarkness && this.getHeldLight().isEmpty()) {
            double rx = (-1 + Math.random() * 2) * 0.3;
            double ry = Math.random();
            double rz = (-1 + Math.random() * 2) * 0.3;
            double mx = this.player.getRandom().nextGaussian() * 0.2;
            double my = this.player.getRandom().nextGaussian() * 0.2;
            double mz = this.player.getRandom().nextGaussian() * 0.2;
            ((ServerLevel) this.player.level).sendParticles(ParticleTypes.SMOKE, this.player.getX() + rx, this.player.getY() + ry, this.player.getZ() + rz, (int) (this.darknessLevel * 10), mx, my, mz, 0.01);
        }
    }

    private void playSounds() {
        if (this.player.level.isClientSide() && this.darknessLevel == 1.0 && this.player.getRandom().nextDouble() > 0.95) {
            this.player.playSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD.value(), 1, 0.1f);
        }
    }

    private void dealDarknessDamage() {
        if (this.player.level.isClientSide()) return;
        if (this.dangerLevel != 1.0 || ModConfigs.darknessDamage.get() == 0) return;

        float healthDamageRatio = ModConfigs.darknessDamage.get().floatValue();
        float damage = this.player.getMaxHealth() * healthDamageRatio;

        if (ModConfigs.nightmareMode.get()) {
            damage *= 3;
        }

        int sentinel = Math.min(EnchantmentUtils.getEnchantment(this.player, EnchantmentSetup.SOUL_SENTINEL.get(), EquipmentSlot.CHEST), 4);

        damage *= (float) (1 - sentinel * 0.05);

        if (this.sentinel != null) {
            ServerPlayer sentinelPlayer = (ServerPlayer) this.sentinel.left();
            damage *= (float) (1 - this.sentinel.right() * 0.075);
            float interceptedDamage = sentinelPlayer.getMaxHealth() * 0.25f;
            if (this.player.getHealth() < damage && sentinelPlayer.getHealth() > interceptedDamage) {
                sentinelPlayer.hurt(ModRef.DARKNESS_DAMAGE_SENTINEL, interceptedDamage);
                this.player.addEffect(new MobEffectInstance(EffectSetup.SOUL_VEIL.get(), 30 * 20 / (ModConfigs.nightmareMode.get() ? 3 : 1)));
                sentinelPlayer.addEffect(new MobEffectInstance(EffectSetup.BUSTED.get(), 60 * 20 * (ModConfigs.nightmareMode.get() ? 3 : 1)));
                sentinelPlayer.getLevel().sendParticles(ParticleTypes.REVERSE_PORTAL, sentinelPlayer.getX(), sentinelPlayer.getY() + 1, sentinelPlayer.getZ(), 80, 0.2, 0.3, 0.2, 0.05);
                return;
            }
        }

        this.player.addEffect(new MobEffectInstance(EffectSetup.BUSTED.get(), 10 * 20 * (ModConfigs.nightmareMode.get() ? 3 : 1)));
        this.player.hurt(ModRef.DARKNESS_DAMAGE, damage);
    }

    private ItemStack getHeldLight() {
        ItemStack held = this.player.getMainHandItem();
        if (held.is(ModRef.Tags.LIGHT) || held.is(ModRef.Tags.SOUL_LIGHT)) {
            return held;
        }

        held = this.player.getOffhandItem();
        if (held.is(ModRef.Tags.LIGHT) || held.is(ModRef.Tags.SOUL_LIGHT)) {
            return held;
        }

        return ItemStack.EMPTY;
    }

    private LightBringer getLight(ItemStack stack) {
        if (stack.isEmpty()) return null;

        return this.lightbringers.computeIfAbsent(stack.getItem(), LightBringer::new);
    }

    private float calculateConsumedFlame(Level level, ItemStack held) {
        int consumed = 2;
        if (isPlayerInRain(level, this.player)) {
            consumed = 4;
        }
        if (this.player.isUnderWater()) {
            consumed = 16;
        }

        if (held.is(ModRef.Tags.SOUL_LIGHT)) {
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
    public float getDangerLevel() {
        return this.dangerLevel;
    }

    @Override
    public void setInDarkness(boolean inDarkness) {
        this.isInDarkness = inDarkness;
    }

    @Override
    public float getDarknessLevel() {
        return this.darknessLevel;
    }

    @Override
    public boolean isResistant() {
        return this.player.isCreative() || this.player.isSpectator() || this.player.hasEffect(EffectSetup.SOUL_VEIL.get()) || this.player.isOnFire();
    }

    @Override
    public float getBurnoutModifier() {
        return this.burnoutModifier;
    }

    @Override
    public boolean isHoldingFlame() {
        return this.getLight(this.getHeldLight()) != null;
    }

    public void syncToClient() {
        if (!this.player.level.isClientSide()) {
            PacketHandler.sendToClient(new SyncDarknessMessage(this.serializeNBT()), this.player);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("Darkness", this.darknessLevel);
        nbt.putFloat("Danger", this.dangerLevel);
        nbt.putBoolean("IsInDarkness", this.isInDarkness);
        nbt.putFloat("BurnoutModifier", this.burnoutModifier);
        ListTag lightBringersTag = new ListTag();
        for (Map.Entry<Item, LightBringer> entry : this.lightbringers.entrySet()) {
            CompoundTag lightTag = new CompoundTag();
            lightTag.putString("Item", ForgeRegistries.ITEMS.getKey(entry.getKey()).toString());
            entry.getValue().saveTo(lightTag);
            lightBringersTag.add(lightTag);
        }
        nbt.put("LightBringers", lightBringersTag);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.darknessLevel = nbt.getFloat("Darkness");
        this.dangerLevel = nbt.getFloat("Danger");
        this.isInDarkness = nbt.getBoolean("IsInDarkness");
        this.burnoutModifier = nbt.getFloat("BurnoutModifier");
        if (nbt.contains("LightBringers")) {
            ListTag lightBringersTag = nbt.getList("LightBringers", Tag.TAG_COMPOUND);
            lightBringersTag.stream().map(CompoundTag.class::cast)
                .forEach(lightTag -> {
                    ResourceLocation itemId = new ResourceLocation(lightTag.getString("Item"));
                    Item item = ForgeRegistries.ITEMS.getValue(itemId);
                    LightBringer light = new LightBringer(item);
                    light.loadFrom(lightTag);
                    this.lightbringers.put(item, light);
                });
        }
    }
}
