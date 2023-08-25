package dev.quarris.engulfingdarkness.registry;

import dev.quarris.engulfingdarkness.ModRef;
import dev.quarris.engulfingdarkness.enchantment.SoulSentinelEnchantment;
import dev.quarris.engulfingdarkness.enchantment.ValianceEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EnchantmentSetup {
    public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ModRef.ID);
    public static final RegistryObject<ValianceEnchantment> VALIANCE = REGISTRY.register("valiance", ValianceEnchantment::new);
    public static final RegistryObject<SoulSentinelEnchantment> SOUL_SENTINEL = REGISTRY.register("soul_sentinel", SoulSentinelEnchantment::new);

    public static void init(IEventBus bus) {
        REGISTRY.register(bus);
    }
}

