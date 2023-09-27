package dev.quarris.engulfingdarkness.datagen.server;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.darkness.LightBringer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTagGen extends ItemTagsProvider {
    public ItemTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, TagsProvider<Block> blockTags, ExistingFileHelper helper) {
        super(output, lookupProvider, blockTags, ModRef.ID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
