package dev.quarris.engulfingdarkness;

import dev.quarris.engulfingdarkness.capability.IDarkness;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.tags.ITag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModRef {

    public static final String ID = "engulfingdarkness";

    public static final Logger LOGGER = LogManager.getLogger(EngulfingDarkness.class);

    public static ResourceLocation res(String res) {
        return new ResourceLocation(ID, res);
    }

    public static final DamageSource DARKNESS_DAMAGE = new DamageSource("engulfingDarkness").bypassMagic().bypassEnchantments().bypassArmor().setScalesWithDifficulty();

    public static class Capabilities {
        public static final Capability<IDarkness> DARKNESS = CapabilityManager.get(new CapabilityToken<>() {});
    }

    public static class Tags {
        public static final TagKey<Item> LIGHT = ItemTags.create(ModRef.res("light"));
        public static final TagKey<Item> SOUL_LIGHT = ItemTags.create(ModRef.res("soul_light"));
    }

}
