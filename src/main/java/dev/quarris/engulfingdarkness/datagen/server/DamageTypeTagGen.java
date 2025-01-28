package dev.quarris.engulfingdarkness.datagen.server;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.registry.DamageSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTagGen extends DamageTypeTagsProvider {
    public DamageTypeTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper) {
        super(output, lookupProvider, ModRef.ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR)
            .add(DamageSetup.DARKNESS_DAMAGE, DamageSetup.DARKNESS_DAMAGE_SENTINEL);

        this.tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
            .add(DamageSetup.DARKNESS_DAMAGE, DamageSetup.DARKNESS_DAMAGE_SENTINEL);

        this.tag(DamageTypeTags.BYPASSES_EFFECTS)
            .add(DamageSetup.DARKNESS_DAMAGE, DamageSetup.DARKNESS_DAMAGE_SENTINEL);

        this.tag(DamageTypeTags.BYPASSES_RESISTANCE)
            .add(DamageSetup.DARKNESS_DAMAGE, DamageSetup.DARKNESS_DAMAGE_SENTINEL);

        this.tag(DamageTypeTags.BYPASSES_SHIELD)
            .add(DamageSetup.DARKNESS_DAMAGE, DamageSetup.DARKNESS_DAMAGE_SENTINEL);

        this.tag(DamageTypeTags.NO_KNOCKBACK)
            .add(DamageSetup.DARKNESS_DAMAGE, DamageSetup.DARKNESS_DAMAGE_SENTINEL);
    }
}
