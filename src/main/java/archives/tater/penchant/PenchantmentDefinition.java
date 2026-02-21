package archives.tater.penchant;

import archives.tater.penchant.registry.PenchantRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Math.max;

public record PenchantmentDefinition(
        int experienceCost,
        int bookRequirement,
        Enchantment.Cost progressCostFactor
) {
    public static final Codec<PenchantmentDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("experience_cost").forGetter(PenchantmentDefinition::experienceCost),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("book_requirement").forGetter(PenchantmentDefinition::bookRequirement),
            Enchantment.Cost.CODEC.fieldOf("progress_cost_factor").forGetter(PenchantmentDefinition::progressCostFactor)
    ).apply(instance, PenchantmentDefinition::new));

    public static ResourceKey<PenchantmentDefinition> keyOf(ResourceKey<Enchantment> enchantment) {
        return ResourceKey.create(PenchantRegistries.PENCHANTMENT_DEFINITION, enchantment.location());
    }

    public static PenchantmentDefinition createFallback(Holder<Enchantment> enchantment) {
        return new PenchantmentDefinition(
                enchantment.value().getAnvilCost(),
                max(2 * enchantment.value().getMinCost(1) - 5, 0),
                enchantment.value().definition().maxCost()
        );
    }

    private static final Map<Holder<Enchantment>, PenchantmentDefinition> CACHE = new WeakHashMap<>();

    public static void buildCache(HolderLookup.Provider registries) {
        var definitions = registries.lookupOrThrow(PenchantRegistries.PENCHANTMENT_DEFINITION);
        CACHE.putAll(registries.lookupOrThrow(Registries.ENCHANTMENT).listElements()
                .collect(Collectors.toMap(
                        Function.identity(),
                        holder -> definitions.get(keyOf(holder.key()))
                                .map(Holder::value)
                                .orElseGet(() -> createFallback(holder))
                )));
    }

    public static PenchantmentDefinition getDefinition(Holder<Enchantment> enchantment) {
        return CACHE.get(enchantment);
    }
}
