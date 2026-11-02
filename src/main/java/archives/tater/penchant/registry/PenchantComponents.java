package archives.tater.penchant.registry;

import archives.tater.hcapi.api.HcapiDataComponentInitializers;
import archives.tater.penchant.Penchant;
import archives.tater.penchant.component.EnchantmentProgress;
import archives.tater.penchant.component.RandomEnchantment;
import archives.tater.penchant.util.PenchantmentHelper;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.enchantment.Enchantment;

import org.jspecify.annotations.Nullable;

import static java.lang.Math.max;
import static net.minecraft.world.item.enchantment.Enchantment.constantCost;

public class PenchantComponents {
    private static <T> DataComponentType<T> register(String path, @Nullable Codec<T> codec, @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, boolean cache) {
        return register(path, codec, streamCodec, cache, false);
    }

    private static <T> DataComponentType<T> register(String path, @Nullable Codec<T> codec, @Nullable StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, boolean cache, boolean ignoreSwapAnimation) {
        var type = DataComponentType.<T>builder();
        if (codec != null) type.persistent(codec);
        if (streamCodec != null) type.networkSynchronized(streamCodec);
        if (cache) type.cacheEncoding();
        if (ignoreSwapAnimation) type.ignoreSwapAnimation();
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Penchant.id(path), type.build());
    }

    public static final DataComponentType<EnchantmentProgress> ENCHANTMENT_PROGRESS = register(
            "enchantment_progress",
            EnchantmentProgress.CODEC,
            EnchantmentProgress.STREAM_CODEC,
            true,
            true
    );

    public static final DataComponentType<Enchantment.Cost> PENCHANTIBILITY = register(
            "penchantibility",
            Enchantment.Cost.CODEC,
            PenchantmentHelper.COST_STREAM_CODEC,
            false
    );

    public static final DataComponentType<Integer> EXPERIENCE_COST = register(
            "experience_cost",
            ExtraCodecs.NON_NEGATIVE_INT,
            ByteBufCodecs.VAR_INT,
            false
    );

    public static final DataComponentType<Integer> BOOK_REQUIREMENT = register(
            "book_requirement",
            ExtraCodecs.NON_NEGATIVE_INT,
            ByteBufCodecs.VAR_INT,
            false
    );

    public static final DataComponentType<RandomEnchantment> RANDOM_ENCHANTMENT = register(
            "random_enchantment",
            RandomEnchantment.CODEC,
            RandomEnchantment.STREAM_CODEC,
            true
    );

    public static final Identifier FALLBACK_PARAMETERS = Penchant.id("fallback_parameters");

    private static <T> void setIfAbsent(DataComponentMap.Builder builder, DataComponentType<T> type, T value) {
        if (!builder.contains(type))
            builder.set(type, value);
    }

    @SuppressWarnings("deprecation")
    public static void init() {
        DefaultItemComponentEvents.MODIFY.register(context -> {
            context.modify(
                    item -> item.components().has(DataComponents.MAX_DAMAGE),
                    (builder, item) -> setIfAbsent(
                            builder,
                            PENCHANTIBILITY,
                            constantCost(item.builtInRegistryHolder().is(PenchantItemTags.DEFAULT_PROGRESS_COST_FACTOR_ONE)
                                    ? 1
                                    : max(item.components().getOrDefault(DataComponents.MAX_DAMAGE, 0) / 100, 1))
                    )
            );
        });

        HcapiDataComponentInitializers.registerInitializer(FALLBACK_PARAMETERS, context -> {
            context.lookupProvider().lookupOrThrow(Registries.ENCHANTMENT).listElements().forEach(enchantment -> {
                var builder = context.builder(enchantment.key());
                setIfAbsent(builder, EXPERIENCE_COST, enchantment.value().getAnvilCost());
                setIfAbsent(builder, BOOK_REQUIREMENT, max(2 * enchantment.value().getMinCost(1) - 5, 0));
                setIfAbsent(builder, PENCHANTIBILITY, enchantment.value().definition().maxCost());
            });
        });
        HcapiDataComponentInitializers.addInitializerOrdering(FALLBACK_PARAMETERS, HcapiDataComponentInitializers.DATA_HOLDER_COMPONENTS);

        BuiltInRegistries.DATA_COMPONENT_TYPE.addAlias(
                Penchant.id("enchantment_progress_cost_factor"),
                Penchant.id("penchantibility")
        );
    }
}
