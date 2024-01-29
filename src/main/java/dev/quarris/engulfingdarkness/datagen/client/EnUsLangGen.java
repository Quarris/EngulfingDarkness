package dev.quarris.engulfingdarkness.datagen.client;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.registry.EffectSetup;
import dev.quarris.engulfingdarkness.registry.EnchantmentSetup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class EnUsLangGen extends LanguageProvider {

    public EnUsLangGen(PackOutput output) {
        super(output, ModRef.ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add("death.attack.darkness", "%1$s could not reach the light");
        this.add("death.attack.darkness_sentinel", "%1$s could not reach the light");
        this.add(EffectSetup.BUSTED.get(), "Busted");
        this.add(EffectSetup.SOUL_VEIL.get(), "Soul Veil");
        this.add(EffectSetup.SENTINEL_PROTECTION.get(), "Interception");
        this.add(EffectSetup.DEATH_WARD.get(), "Death Ward");
        this.add(EnchantmentSetup.VALIANCE.get(), "Valiance");
        this.add(EnchantmentSetup.SOUL_SENTINEL.get(), "Soul Sentinel");
        this.add("item.minecraft.potion.effect.soul_veil", "Potion of Veil");
        this.add("engulfingdarkness.lightbringer", "Light Bringer");
    }
}
