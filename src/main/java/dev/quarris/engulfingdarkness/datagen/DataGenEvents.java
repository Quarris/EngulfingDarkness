package dev.quarris.engulfingdarkness.datagen;

import dev.quarris.engulfingdarkness.ModRef;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEvents {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
        PackOutput packOutput = gen.getPackOutput();

        gen.addProvider(
            event.includeServer(),
            new ModItemTagProvider(packOutput, lookup, new ForgeBlockTagsProvider(packOutput, lookup, helper), helper)
        );
    }
}
