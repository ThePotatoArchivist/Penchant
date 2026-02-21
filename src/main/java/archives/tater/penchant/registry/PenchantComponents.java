package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.component.EnchantmentProgress;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class PenchantComponents {
    private static <T> DataComponentType<T> registerComponent(String path, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, boolean cache, boolean ignoreSwapAnimation) {
        var type = DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec);
        if (cache) type.cacheEncoding();
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Penchant.id(path), type.build());
    }

    public static final DataComponentType<EnchantmentProgress> ENCHANTMENT_PROGRESS = registerComponent(
            "enchantment_progress",
            EnchantmentProgress.CODEC,
            EnchantmentProgress.STREAM_CODEC,
            true,
            true
    );

    public static void init() {

    }
}
