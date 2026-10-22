package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.enchantment.EquipmentValueEffect;
import archives.tater.penchant.enchantment.UnbreakableEffect;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;

import java.util.List;

public class PenchantEnchantments {
    private static <T> DataComponentType<T> registerEnchantmentEffect(String path, Codec<T> codec) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Penchant.id(path), DataComponentType.<T>builder().persistent(codec).build());
    }

    public static final DataComponentType<List<UnbreakableEffect>> UNBREAKABLE = registerEnchantmentEffect("unbreakable", UnbreakableEffect.CODEC.listOf());

    public static final DataComponentType<List<TargetedConditionalEffect<EquipmentValueEffect>>> EQUIPMENT_DROPS = registerEnchantmentEffect("equipment_drops", TargetedConditionalEffect.equipmentDropsCodec(EquipmentValueEffect.CODEC).listOf());

    public static void init() {

    }
}
