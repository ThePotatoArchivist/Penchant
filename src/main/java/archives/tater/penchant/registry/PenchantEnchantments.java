package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.enchantment.UnbreakableEffect;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.Validatable;

import java.util.List;

public class PenchantEnchantments {
    private static <T> DataComponentType<T> registerEnchantmentEffect(String path, Codec<T> codec) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Penchant.id(path), DataComponentType.<T>builder().persistent(codec).build());
    }

    private static <T extends Validatable> Codec<List<T>> validatedListCodec(final Codec<T> elementCodec, final ContextKeySet paramSet) {
        return elementCodec.listOf().validate(Validatable.listValidatorForContext(paramSet));
    }

    public static final DataComponentType<List<UnbreakableEffect>> UNBREAKABLE = registerEnchantmentEffect("unbreakable", UnbreakableEffect.CODEC.listOf());

    public static final DataComponentType<List<TargetedConditionalEffect<EnchantmentValueEffect>>> EQUIPMENT_DROPS = registerEnchantmentEffect("equipment_drops", validatedListCodec(TargetedConditionalEffect.equipmentDropsCodec(EnchantmentValueEffect.CODEC), PenchantLootData.ENCHANTED_EQUIPMENT_DROP));

    public static void init() {

    }
}
