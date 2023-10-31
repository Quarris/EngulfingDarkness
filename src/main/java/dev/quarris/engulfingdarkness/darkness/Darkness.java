package dev.quarris.engulfingdarkness.darkness;

import dev.quarris.engulfingdarkness.EnchantmentUtils;
import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.enchantment.SoulSentinelEnchantment;
import dev.quarris.engulfingdarkness.packets.SetLowLightMessage;
import dev.quarris.engulfingdarkness.packets.FlameDataMessage;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import dev.quarris.engulfingdarkness.registry.EnchantmentSetup;
import dev.quarris.engulfingdarkness.util.PlayerUtil;
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

    public static final int BASE_CONSUMPTION = 2;
    private final Player player;
    private Pair<Player, Integer> sentinel;
    private final Map<LightBringer, FlameData> flameData = new HashMap<>();
    private boolean isInLowLight;
    private float engulfLevel;
    private float dangerLevel;
    private float burnoutModifier;
    private float consumptionAmplifier;
    private int popupTime;
    private int popupColor;

    public Darkness(Player player) {
        this.player = player;
    }

    @Override
    public float getFlameLife() {
        LightBringer light = this.getHeldLight();
        if (light != null) {
            return this.flameData.computeIfAbsent(light, FlameData::new).getLife();
        }

        return 0;
    }

    @Override
    public void setFlame(LightBringer light, int flame) {
        FlameData flameData = this.flameData.computeIfAbsent(light, FlameData::new);
        flameData.set(flame);
    }

    @Override
    public void tick() {
        // Updates the popup timer
        this.updatePopup();
        // Send packet to client to update whether they are in darkness
        // since clients light levels don't get fully updated
        this.updatePlayerInLowLight();
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
    }

    private void updatePopup() {
        if (this.popupTime > 0) {
            this.popupTime--;
        }

        if (this.popupTime == 0) {
            this.popupTime = -1;
            this.popupColor = -1;
        }
    }

    private void updatePlayerInLowLight() {
        if (this.player.level.isClientSide()) {
            return;
        }

        if (this.isInLowLight && this.isResistant()) {
            this.setInLowLight(false);
            return;
        }

        if (this.isResistant()) {
            return;
        }

        int light = this.player.level.getMaxLocalRawBrightness(new BlockPos(this.player.getEyePosition(1)));
        boolean isLowLight = light <= ModRef.configs().darknessLightLevel;
        if (this.isInLowLight && !isLowLight) { // No longer in darkness
            this.setInLowLight(false);
        } else if (!this.isInLowLight && isLowLight) { // Now in darkness
            this.setInLowLight(true);
        }
    }

    private void findSentinel() {
        // Client doesn't need to know about the sentinel
        if (this.player.level.isClientSide()) return;

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

    private void updateFlame() {
        LightBringer light = this.getHeldLight();
        this.burnoutModifier = 1;
        if (light == null || !this.isInLowLight) return;

        ItemStack stack = this.getHeldLightStack();
        FlameData flameData = this.flameData.computeIfAbsent(light, FlameData::new);

        this.consumptionAmplifier = this.calculateConsumptionAmplifier(this.player.level, light);
        float consumedFlame = BASE_CONSUMPTION * this.consumptionAmplifier;

        if (this.engulfLevel > 0.3) {
            this.burnoutModifier = Mth.lerp((this.engulfLevel - 0.3f) / 0.7f, 1, 6);
        }

        consumedFlame *= this.burnoutModifier;
        int burnedFlame = Mth.ceil(consumedFlame);
        int remainingFlame = flameData.burn(burnedFlame);
        // If the item has burned out, reset it and burn it for how much it went under its flame.
        if (remainingFlame <= 0) {
            flameData.reset();
            this.setPopup(10, 0xFFFFFFFF);
            flameData.burn(Mth.abs(remainingFlame));
            // Destroy item once burned
            if (!this.player.level.isClientSide()) {
                if (stack.isDamageableItem()) {
                    stack.hurtAndBreak(1, this.player, p -> {

                    });
                } else {
                    stack.shrink(1);
                }
                // TODO Do burned action here
                this.player.level.playSound(null, this.player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1, 1);
            }
        }
        this.syncToClient(new FlameDataMessage(light, flameData.getFlame()));
    }

    private void updateDarknessLevels() {
        LightBringer light = this.getHeldLight();
        double engulfTimer = ModRef.configs().engulfTimer;
        double dangerTimer = ModRef.configs().dangerTimer;
        int valianceLevel = EnchantmentUtils.getEnchantment(this.player, EnchantmentSetup.VALIANCE.get(), EquipmentSlot.HEAD);
        if (valianceLevel > 0) {
            engulfTimer += 2 * valianceLevel;
        }

        // If not in darkness, reset the levels.
        if (!this.isInDarkness()) {
            this.engulfLevel = (float) Math.max(this.engulfLevel - 1 / (engulfTimer * 20), 0);
            this.dangerLevel = 0;
            return;
        }

        // Update the darkness and danger levels when in darkness
        this.engulfLevel = (float) Math.min(this.engulfLevel + 1 / (engulfTimer * 20), 1);
        if (this.engulfLevel == 1.0) {
            this.dangerLevel = (float) Math.min(this.dangerLevel + 1 / (dangerTimer * 20), 1);
        }
    }

    private void spawnParticles() {
        if (!this.player.level.isClientSide() && this.isInDarkness()) {
            double rx = (-1 + Math.random() * 2) * 0.3;
            double ry = Math.random();
            double rz = (-1 + Math.random() * 2) * 0.3;
            double mx = this.player.getRandom().nextGaussian() * 0.2;
            double my = this.player.getRandom().nextGaussian() * 0.2;
            double mz = this.player.getRandom().nextGaussian() * 0.2;
            ((ServerLevel) this.player.level).sendParticles(ParticleTypes.SMOKE, this.player.getX() + rx, this.player.getY() + ry, this.player.getZ() + rz, (int) (this.engulfLevel * 10), mx, my, mz, 0.01);
        }
    }

    private void playSounds() {
        if (this.player.level.isClientSide() && this.engulfLevel == 1.0 && this.player.getRandom().nextDouble() > 0.95) {
            this.player.playSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD.value(), 1, 0.1f);
        }
    }

    private void dealDarknessDamage() {
        if (this.player.level.isClientSide()) return;
        if (this.dangerLevel != 1.0 || ModRef.configs().darknessDamage == 0) return;

        float healthDamageRatio = (float) ModRef.configs().darknessDamage / 100;
        float damage = this.player.getMaxHealth() * healthDamageRatio;

        if (ModRef.configs().nightmareMode) {
            damage *= 3;
        }

        int ourSentinelLevel = Math.min(EnchantmentUtils.getEnchantment(this.player, EnchantmentSetup.SOUL_SENTINEL.get(), EquipmentSlot.CHEST), 4);

        damage *= (float) (1 - ourSentinelLevel * 0.05);

        if (this.sentinel != null) {
            ServerPlayer sentinelPlayer = (ServerPlayer) this.sentinel.left();
            damage *= (float) (1 - this.sentinel.right() * 0.075);
            float interceptedDamage = sentinelPlayer.getMaxHealth() * 0.25f;
            if (this.player.getHealth() < damage && sentinelPlayer.getHealth() > interceptedDamage) {
                sentinelPlayer.hurt(ModRef.DARKNESS_DAMAGE_SENTINEL, interceptedDamage);
                this.player.addEffect(new MobEffectInstance(EffectSetup.SOUL_VEIL.get(), 30 * 20 / (ModRef.configs().nightmareMode ? 3 : 1)));
                sentinelPlayer.addEffect(new MobEffectInstance(EffectSetup.BUSTED.get(), 60 * 20 * (ModRef.configs().nightmareMode ? 3 : 1)));
                sentinelPlayer.getLevel().sendParticles(ParticleTypes.REVERSE_PORTAL, sentinelPlayer.getX(), sentinelPlayer.getY() + 1, sentinelPlayer.getZ(), 80, 0.2, 0.3, 0.2, 0.05);
                return;
            }
        }

        this.player.addEffect(new MobEffectInstance(EffectSetup.BUSTED.get(), 10 * 20 * (ModRef.configs().nightmareMode ? 3 : 1)));
        this.player.hurt(ModRef.DARKNESS_DAMAGE, damage);
    }

    public void setPopup(int time, int color) {
        this.popupTime = time;
        this.popupColor = color;
    }

    @Override
    public Popup getPopup() {
        return new Popup(this.popupTime, this.popupColor);
    }

    public boolean isInDarkness() {
        return this.isInLowLight && this.getHeldLight() == null;
    }

    private LightBringer getHeldLight() {
        return LightBringer.getLightBringer(this.getHeldLightStack().getItem());
    }

    private ItemStack getHeldLightStack() {
        return PlayerUtil.getHolding(this.player, s -> LightBringer.REGISTRY.containsKey(s.getItem()));
    }

    @Override
    public FlameData getLight(ItemStack stack) {
        return this.flameData.get(LightBringer.getLightBringer(stack.getItem()));
    }

    private float calculateConsumptionAmplifier(Level level, LightBringer light) {
        if (isPlayerInRain(level, this.player)) {
            return 2;
        }
        if (this.player.isUnderWater()) {
            return 4;
        }

        return 1;
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
    public boolean isInLowLight() {
        return this.isInLowLight;
    }

    @Override
    public float getDangerLevel() {
        return this.dangerLevel;
    }

    @Override
    public void setInLowLight(boolean inLowLight) {
        this.isInLowLight = inLowLight;
        this.setPopup(20, 0xFFFFFFFF);
        this.syncToClient(new SetLowLightMessage(inLowLight));
    }

    @Override
    public float getEngulfLevel() {
        return this.engulfLevel;
    }

    @Override
    public boolean isResistant() {
        return !ModRef.configs().isAllowed(this.player.level.dimension().location()) || this.player.isCreative() || this.player.isSpectator() || this.player.hasEffect(EffectSetup.SOUL_VEIL.get()) || this.player.isOnFire();
    }

    @Override
    public float getConsumptionAmplifier() {
        return this.consumptionAmplifier;
    }

    @Override
    public boolean isHoldingFlame() {
        return this.getHeldLight() != null;
    }

    public <T> void syncToClient(T packet) {
        if (!this.player.level.isClientSide()) {
            PacketHandler.sendToClient(packet, this.player);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("Darkness", this.engulfLevel);
        nbt.putFloat("Danger", this.dangerLevel);
        nbt.putBoolean("IsInDarkness", this.isInLowLight);
        nbt.putFloat("BurnoutModifier", this.burnoutModifier);
        ListTag lightBringersTag = new ListTag();
        for (Map.Entry<LightBringer, FlameData> entry : this.flameData.entrySet()) {
            CompoundTag lightTag = new CompoundTag();
            lightTag.putString("Item", ForgeRegistries.ITEMS.getKey(entry.getKey().item()).toString());
            entry.getValue().saveTo(lightTag);
            lightBringersTag.add(lightTag);
        }
        nbt.put("LightBringers", lightBringersTag);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.engulfLevel = nbt.getFloat("Darkness");
        this.dangerLevel = nbt.getFloat("Danger");
        this.isInLowLight = nbt.getBoolean("IsInDarkness");
        this.burnoutModifier = nbt.getFloat("BurnoutModifier");
        if (nbt.contains("LightBringers")) {
            ListTag lightBringersTag = nbt.getList("LightBringers", Tag.TAG_COMPOUND);
            lightBringersTag.stream().map(CompoundTag.class::cast)
                .forEach(lightTag -> {
                    ResourceLocation itemId = new ResourceLocation(lightTag.getString("Item"));
                    Item item = ForgeRegistries.ITEMS.getValue(itemId);
                    LightBringer light = LightBringer.getLightBringer(item);
                    if (light == null) {
                        ModRef.LOGGER.warn(item + " is not longer a Light Bringer. Skipping.");
                        return;
                    }
                    FlameData data = new FlameData(light);
                    data.loadFrom(lightTag);
                    this.flameData.put(light, data);
                });
        }
    }
}
