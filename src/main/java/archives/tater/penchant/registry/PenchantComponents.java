package archives.tater.penchant.registry;

import archives.tater.hcapi.api.HcapiDataComponentInitializers;
import archives.tater.penchant.Penchant;
import archives.tater.penchant.PenchantFallbackConfig;
import archives.tater.penchant.component.EnchantmentProgress;
import archives.tater.penchant.component.RandomEnchantment;
import archives.tater.penchant.util.PenchantmentHelper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
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
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.item.enchantment.Enchantment;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.util.Optional;

import static archives.tater.penchant.util.PenchantUtil.multiply;
import static java.lang.Math.clamp;
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

    private static PenchantFallbackConfig getActiveConfig(ResourceManager resourceManager) {
        return resourceManager.getResource(PenchantFallbackConfig.RESOURCE_PATH)
                .flatMap(resource -> {
                    try (var reader = resource.openAsReader()) {
                        return switch (PenchantFallbackConfig.CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(reader))) {
                            case DataResult.Success<PenchantFallbackConfig>(var value, var _) -> Optional.of(value);
                            case DataResult.Error<PenchantFallbackConfig> error -> {
                                Penchant.LOGGER.error("Error reading pack fallback parameters: {}", error.message());
                                yield Optional.empty();
                            }
                        };
                    } catch (IOException exception) {
                        Penchant.LOGGER.error("Error reading pack fallback parameters:", exception);
                        return Optional.empty();
                    }
                })
                .orElse(Penchant.CONFIG.fallbackParameters);
    }

    public static void init() {
        HcapiDataComponentInitializers.registerInitializer(FALLBACK_PARAMETERS, context -> {
            var config = getActiveConfig(context.resourceManager());

            context.lookupProvider().lookupOrThrow(Registries.ITEM).listElements()
                    .forEach(item -> {
                        var builder = context.builder(item.key());
                        if (!builder.contains(DataComponents.MAX_DAMAGE)) return;
                        setIfAbsent(builder, PENCHANTIBILITY, constantCost(item.is(PenchantItemTags.DEFAULT_PROGRESS_COST_FACTOR_ONE)
                                ? 1
                                : clamp(
                                        multiply(builder.getOrDefault(DataComponents.MAX_DAMAGE, 0), config.itemPenchantibilityMultiplier()),
                                        config.minItemPenchantibility(),
                                        config.maxItemPenchantibility() == 0 ? Integer.MAX_VALUE : config.maxItemPenchantibility()
                                )
                        ));
                    });

            context.lookupProvider().lookupOrThrow(Registries.ENCHANTMENT).listElements().forEach(enchantment -> {
                var builder = context.builder(enchantment.key());

                setIfAbsent(builder, EXPERIENCE_COST, max(1, multiply(enchantment.value().getAnvilCost(), config.xpCostMultiplier())));
                setIfAbsent(builder, BOOK_REQUIREMENT, max(0, multiply(enchantment.value().getMinCost(1), config.bookRequirementMultiplier()) + config.bookRequirementConstant()));
                var maxCost = enchantment.value().definition().maxCost();
                setIfAbsent(builder, PENCHANTIBILITY, new Enchantment.Cost(
                        max(1, maxCost.base() + config.enchantmentPenchantibilityConstant()),
                        max(0, multiply(maxCost.perLevelAboveFirst(), config.enchantmentPenchantibilityMultiplier()))
                ));
            });
        });
//        HcapiDataComponentInitializers.addInitializerOrdering(FALLBACK_PARAMETERS, HcapiDataComponentInitializers.DATA_HOLDER_COMPONENTS);

        BuiltInRegistries.DATA_COMPONENT_TYPE.addAlias(
                Penchant.id("enchantment_progress_cost_factor"),
                Penchant.id("penchantibility")
        );
    }
}
