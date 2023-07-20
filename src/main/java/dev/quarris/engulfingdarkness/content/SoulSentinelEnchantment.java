package dev.quarris.engulfingdarkness.content;

import dev.quarris.engulfingdarkness.EnchantmentUtils;
import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.ModRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public class SoulSentinelEnchantment extends Enchantment {
    public SoulSentinelEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    public int getDistance(int level) {
        if (level > 4) level = 4;

        return 15 * level;
    }

    @Mod.EventBusSubscriber
    public static class Events {

        // TODO Replace these 2 events into the Darkness Capability
        // Store the current player UUID that is protecting the user for quick access on damage control and applying the visual potion effect
        @SubscribeEvent
        public static void applyProtectionEffectToNearbyPlayers(TickEvent.PlayerTickEvent event) {
            if (!(event.player instanceof ServerPlayer player)) {
                return;
            }

            if (player.hasEffect(ModRegistry.Effects.BUSTED.get())) {
                return;
            }

            int sentinelLevel = Math.min(4, EnchantmentUtils.getEnchantment(player, ModRegistry.Enchantments.SOUL_SENTINEL.get(), EquipmentSlot.CHEST));

            if (sentinelLevel <= 0) return;

            int distance = ModRegistry.Enchantments.SOUL_SENTINEL.get().getDistance(sentinelLevel);
            var nearby = player.level.getNearbyPlayers(TargetingConditions.forNonCombat().selector(p -> p.distanceToSqr(player) <= distance * distance), player, player.getBoundingBox().inflate(distance));
            nearby.forEach(target -> {
                target.addEffect(new MobEffectInstance(ModRegistry.Effects.SENTINEL_PROTECTION.get(), 5, sentinelLevel - 1, true, true));
            });
        }

        @SubscribeEvent
        public static void damageControl(LivingDamageEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getSource() != ModRef.DARKNESS_DAMAGE) {
                return;
            }

            if (player.hasEffect(ModRegistry.Effects.BUSTED.get())) {
                return;
            }

            float damage = event.getAmount();
            ServerLevel level = (ServerLevel) player.level;
            Optional<Pair<ServerPlayer, Integer>> nearestTarget = level.players().stream()
                .filter(target -> !target.getUUID().equals(player.getUUID()))
                .filter(target -> !target.hasEffect(ModRegistry.Effects.BUSTED.get()))
                .sorted((p1, p2) -> Mth.sign(p1.distanceToSqr(player) - p2.distanceToSqr(player)))
                .map(p -> {
                    int soulSentinelLevel = Math.min(EnchantmentUtils.getEnchantment(p, ModRegistry.Enchantments.SOUL_SENTINEL.get(), EquipmentSlot.CHEST), 4);
                    return Pair.of(p, soulSentinelLevel);
                })
                .filter(pair -> pair.getRight() > 0)
                .filter(pair -> {
                    Player target = pair.getLeft();
                    int enchantmentLevel = pair.getRight();
                    int minDistance = ModRegistry.Enchantments.SOUL_SENTINEL.get().getDistance(enchantmentLevel);
                    return target.distanceToSqr(player) <= minDistance * minDistance;
                }).findFirst();

            if (nearestTarget.isPresent()) {
                ServerPlayer target = nearestTarget.get().getLeft();
                damage *= (1 - nearestTarget.get().getRight() * 0.075);
                float interceptedDamage = target.getMaxHealth() * 0.25f;
                if (player.getHealth() < damage && target.getHealth() > interceptedDamage) {
                    player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(cap -> {
                        cap.resetBurnout(player);
                    });
                    target.hurt(ModRef.DARKNESS_DAMAGE_SENTINEL, interceptedDamage);
                    player.addEffect(new MobEffectInstance(ModRegistry.Effects.SOUL_VEIL.get(), 30 * 20));
                    target.addEffect(new MobEffectInstance(ModRegistry.Effects.BUSTED.get(), 60 * 20));
                    level.sendParticles(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY() + 1, target.getZ(), 80, 0.2, 0.3, 0.2, 0.05);
                    event.setCanceled(true);
                    return;
                }
                event.setAmount(damage);
            }
        }
    }

}
