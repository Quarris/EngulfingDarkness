package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.darkness.IDarkness;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModRef {

    public static final String ID = "engulfingdarkness";

    public static final Logger LOGGER = LogManager.getLogger(EngulfingDarkness.class);

    static ModConfigs CONFIGS;
    public static ModConfigs configs() {
        return CONFIGS;
    }

    public static ResourceLocation res(String res) {
        return ResourceLocation.fromNamespaceAndPath(ID, res);
    }

    public static class Capabilities {
        public static final Capability<IDarkness> DARKNESS = CapabilityManager.get(new CapabilityToken<>() {});
    }

    public static class Tags {
        public static final TagKey<Item> LIGHT_BRINGERS = ItemTags.create(ModRef.res("light_bringers"));
    }

}
