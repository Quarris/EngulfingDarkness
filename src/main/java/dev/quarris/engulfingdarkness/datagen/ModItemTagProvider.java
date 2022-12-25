package dev.quarris.engulfingdarkness.datagen;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, TagsProvider<Block> blockTags, ExistingFileHelper helper) {
        super(output, lookupProvider, blockTags, ModRef.ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Light
        this.tag(ModRef.Tags.LIGHT).add(Items.TORCH);

        // Soul Light
        this.tag(ModRef.Tags.SOUL_LIGHT).add(Items.SOUL_TORCH);
    }
}
