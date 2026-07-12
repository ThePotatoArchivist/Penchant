package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.component.RandomEnchantment;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class PenchantLootFunctions {
    private static void register(String path, MapCodec<? extends LootItemFunction> codec) {
        Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, Penchant.id(path), codec);
    }

    public static void init() {
        register("random_enchantment", RandomEnchantment.LootFunction.CODEC);
    }
}
