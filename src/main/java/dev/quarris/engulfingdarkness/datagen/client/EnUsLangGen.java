package dev.quarris.engulfingdarkness.datagen.client;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.ModRegistry;
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
        this.add(ModRegistry.Effects.BUSTED.get(), "Busted");
        this.add(ModRegistry.Effects.SOUL_VEIL.get(), "Soul Veil");
        this.add(ModRegistry.Effects.SENTINEL_PROTECTION.get(), "Interception");
        this.add(ModRegistry.Enchantments.VALIANCE.get(), "Valiance");
        this.add(ModRegistry.Enchantments.SOUL_SENTINEL.get(), "Soul Sentinel");
        this.add("item.minecraft.potion.effect.soul_veil", "Potion of Veil");
    }
}
