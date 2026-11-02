package archives.tater.penchant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public interface PenchantFallbackConfig {

    Codec<PenchantFallbackConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("xp_cost_multiplier").forGetter(PenchantFallbackConfig::xpCostMultiplier),
            Codec.FLOAT.fieldOf("book_requirement_multiplier").forGetter(PenchantFallbackConfig::bookRequirementMultiplier),
            Codec.INT.fieldOf("book_requirement_constant").forGetter(PenchantFallbackConfig::bookRequirementConstant),
            Codec.FLOAT.fieldOf("enchantment_penchantibility_multiplier").forGetter(PenchantFallbackConfig::enchantmentPenchantibilityMultiplier),
            Codec.INT.fieldOf("enchantment_penchantibility_constant").forGetter(PenchantFallbackConfig::enchantmentPenchantibilityConstant),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("item_penchantibility_multiplier").forGetter(PenchantFallbackConfig::itemPenchantibilityMultiplier),
            ExtraCodecs.POSITIVE_INT.fieldOf("min_item_penchantibility").forGetter(PenchantFallbackConfig::minItemPenchantibility),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_item_penchantibility").forGetter(PenchantFallbackConfig::maxItemPenchantibility)
    ).apply(instance, PenchantFallbackConfig.Immutable::new));

    Identifier RESOURCE_PATH = Penchant.id("fallback_parameters.json");

    float xpCostMultiplier();
    float bookRequirementMultiplier();
    int bookRequirementConstant();
    float enchantmentPenchantibilityMultiplier();
    int enchantmentPenchantibilityConstant();
    float itemPenchantibilityMultiplier();
    int minItemPenchantibility();
    int maxItemPenchantibility();

    record Immutable(
            float xpCostMultiplier,
            float bookRequirementMultiplier,
            int bookRequirementConstant,
            float enchantmentPenchantibilityMultiplier,
            int enchantmentPenchantibilityConstant,
            float itemPenchantibilityMultiplier,
            int minItemPenchantibility,
            int maxItemPenchantibility
    ) implements PenchantFallbackConfig {}
}
