package dev.quarris.engulfingdarkness.effect;

import dev.quarris.engulfingdarkness.registry.EffectSetup;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class CasterMobEffect extends MobEffect {

    public CasterMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x40ad87);
    }

    public static int getEffect(Player player) {
        MobEffectInstance effect = player.getEffect(EffectSetup.CASTER.get());
        if (effect == null) {
            return 0;
        }
        int level = effect.getAmplifier();
        return Math.min(level + 1, 5);
    }

    public static float getLureReduction(int casterLevel) {
        return switch (casterLevel) {
            case 0 -> 0;
            case 1 -> 0.1f;
            case 2 -> 0.2f;
            case 3 -> 0.3f;
            case 4 -> 0.38f;
            default -> 0.42f;
        };
    }
}
