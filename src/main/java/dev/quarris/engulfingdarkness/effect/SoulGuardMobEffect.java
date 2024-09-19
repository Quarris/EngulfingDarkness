package dev.quarris.engulfingdarkness.effect;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import dev.quarris.engulfingdarkness.packets.PlayerMovedMessage;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class SoulGuardMobEffect extends MobEffect {

    public SoulGuardMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xffcd61);
    }

    @Mod.EventBusSubscriber
    public static class Events {

        @SubscribeEvent
        public static void onJoinWorld(EntityJoinLevelEvent event) {
            if (!(event.getEntity() instanceof Player player)) {
                return;
            }

            player.addEffect(new MobEffectInstance(EffectSetup.SOUL_GUARD.get(), 1000000, 0, false, false, true));
        }

        @SubscribeEvent
        public static void onUse(PlayerInteractEvent event) {
            event.getEntity().removeEffect(EffectSetup.SOUL_GUARD.get());
        }

        @SubscribeEvent
        public static void noTarget(LivingChangeTargetEvent event) {
            if (event.getOriginalTarget() != null && event.getOriginalTarget().hasEffect(EffectSetup.SOUL_GUARD.get())) {
                event.setCanceled(true);
            }
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ModRef.ID)
    public static class Client {
        @SubscribeEvent
        public static void onMoved(MovementInputUpdateEvent event) {
            Input input = event.getInput();
            boolean hasMoved = input.getMoveVector().length() > 0.01;
            boolean hasJumped = input.jumping;
            if (hasMoved || hasJumped) {
                PacketHandler.sendTo(new PlayerMovedMessage(), Minecraft.getInstance().player);
            }
        }
    }
}
