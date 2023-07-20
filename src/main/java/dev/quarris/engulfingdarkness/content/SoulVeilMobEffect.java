package dev.quarris.engulfingdarkness.content;

import dev.quarris.engulfingdarkness.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class SoulVeilMobEffect extends MobEffect {

    public SoulVeilMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xffcd61);
    }

    @Mod.EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void interactionVeil(PlayerInteractEvent.RightClickBlock event) {
            Level level = event.getLevel();
            BlockPos pos = event.getPos();
            BlockState state = level.getBlockState(pos);
            Player player = event.getEntity();
            if (state.is(Blocks.SOUL_LANTERN)) {
                player.addEffect(new MobEffectInstance(ModRegistry.Effects.SOUL_VEIL.get(), 30 * 20));
            }

            if (state.is(Blocks.SOUL_CAMPFIRE)) {
                player.addEffect(new MobEffectInstance(ModRegistry.Effects.SOUL_VEIL.get(), 60 * 20));
                CampfireBlock.dowse(player, level, pos, state);
                level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, false));
            }
        }

        @SubscribeEvent
        public static void interactionVeil(TickEvent.PlayerTickEvent event) {
            if (event.player.getBlockStateOn().is(Blocks.SOUL_SAND)) {
                event.player.addEffect(new MobEffectInstance(ModRegistry.Effects.SOUL_VEIL.get(), 5 * 20));
            }
        }
    }
}
