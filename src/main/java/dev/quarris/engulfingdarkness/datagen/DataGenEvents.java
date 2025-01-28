package dev.quarris.engulfingdarkness.datagen;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.datagen.client.EnUsLangGen;
import dev.quarris.engulfingdarkness.datagen.server.DamageTypeGen;
import dev.quarris.engulfingdarkness.datagen.server.DamageTypeTagGen;
import dev.quarris.engulfingdarkness.datagen.server.EnchantmentGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ModRef.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEvents {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
        PackOutput packOutput = gen.getPackOutput();

        // Client
        gen.addProvider(event.includeClient(), new EnUsLangGen(packOutput));

        // Server
        gen.addProvider(
            event.includeServer(),
            new DamageTypeTagGen(packOutput, lookup, helper)
        );

        gen.addProvider(event.includeServer(),
            (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), new RegistrySetBuilder()
                .add(Registries.DAMAGE_TYPE, DamageTypeGen::bootstrap)
                .add(Registries.ENCHANTMENT, EnchantmentGen::bootstrap),
                Set.of(ModRef.ID)));
    }
}
