package dev.quarris.engulfingdarkness.content;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.ModRegistry;
import dev.quarris.engulfingdarkness.capability.DarknessCapability;
import dev.quarris.engulfingdarkness.capability.IDarkness;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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

    @Mod.EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void damageControl(LivingDamageEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getSource() != ModRef.DARKNESS_DAMAGE) {
                return;
            }

            float damage = event.getAmount();
            ServerLevel level = (ServerLevel) player.level;
            Optional<ServerPlayer> nearestTarget = level.players().stream()
                .sorted((p1, p2) -> Mth.sign(p1.distanceToSqr(player) - p2.distanceToSqr(player)))
                .map(p -> {
                    int soulSentinelLevel = p.getItemBySlot(EquipmentSlot.CHEST).getEnchantmentLevel(ModRegistry.Enchantments.SOUL_SENTINEL.get());
                    return Pair.of(p, soulSentinelLevel);
                })
                .filter(pair -> pair.getRight() > 0)
                .filter(pair -> {
                    Player target = pair.getLeft();
                    int enchantmentLevel = pair.getRight();
                    int minDistance = 10 + (enchantmentLevel - 1) * 5;
                    return target.distanceToSqr(player) <= minDistance * minDistance;
                }).map(Pair::getLeft).findFirst();

            if (nearestTarget.isPresent()) {
                ServerPlayer target = nearestTarget.get();
                damage *= 0.5;
                if (player.getHealth() < damage && target.getHealth() >= 8) {
                    player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(IDarkness::resetBurnout);
                    target.hurt(ModRef.DARKNESS_DAMAGE, damage);
                    player.addEffect(new MobEffectInstance(ModRegistry.Effects.SOUL_VEIL.get(), 30 * 20));
                    target.addEffect(new MobEffectInstance(ModRegistry.Effects.BUSTED.get(), 10 * 20));
                    level.sendParticles(ParticleTypes.REVERSE_PORTAL, target.getX(), target.getY() + 1, target.getZ(), 80, 0.2, 0.3, 0.2, 0.05);
                    event.setCanceled(true);
                    return;
                }
                event.setAmount(damage);
            }
        }
    }

}
