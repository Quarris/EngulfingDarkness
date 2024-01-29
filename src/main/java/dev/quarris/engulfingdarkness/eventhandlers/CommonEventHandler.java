package dev.quarris.engulfingdarkness.eventhandlers;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.capability.DarknessProvider;
import dev.quarris.engulfingdarkness.configs.FlameConfigs;
import dev.quarris.engulfingdarkness.darkness.Darkness;
import dev.quarris.engulfingdarkness.darkness.IDarkness;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import dev.quarris.engulfingdarkness.packets.SyncDarknessMessage;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModRef.ID)
public class CommonEventHandler {

    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(DarknessProvider.KEY, new DarknessProvider(player));
        }
    }

    @SubscribeEvent
    public static void applyEffectOnRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.isEndConquered() || event.getEntity().level.isClientSide) {
            return;
        }

        var time = ModRef.configs().spawnVeiledTimer * 20 / (ModRef.configs().nightmareMode ? 3 : 1);
        if (time > 0) {
            event.getEntity().addEffect(new MobEffectInstance(EffectSetup.SOUL_VEIL.get(), time));
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            event.player.getCapability(ModRef.Capabilities.DARKNESS).ifPresent(IDarkness::tick);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        CompoundTag persistentData = event.getEntity().getPersistentData();
        if (!persistentData.contains("FirstJoin")) {
            persistentData.putBoolean("FirstJoin", true);
            var time = ModRef.configs().spawnVeiledTimer * 20 / (ModRef.configs().nightmareMode ? 3 : 1);
            if (time > 0) {
                event.getEntity().addEffect(new MobEffectInstance(EffectSetup.SOUL_VEIL.get(), time));
            }
            return;
        }

        event.getEntity().getCapability(ModRef.Capabilities.DARKNESS).ifPresent(darkness -> {
            if (darkness instanceof Darkness d) {
                PacketHandler.sendToClient(new SyncDarknessMessage(d.serializeNBT()), event.getEntity());
            }
        });
    }

    @SubscribeEvent
    public static void addHoverText(ItemTooltipEvent event) {
        FlameConfigs.LIGHT_BRINGERS.keySet().forEach(item -> {
            if (event.getItemStack().is(item)) {
                event.getToolTip().add(Component.translatable("engulfingdarkness.lightbringer").withStyle(ChatFormatting.YELLOW));
            }
        });
    }
}
