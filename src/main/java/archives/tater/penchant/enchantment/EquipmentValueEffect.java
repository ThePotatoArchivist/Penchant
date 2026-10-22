package archives.tater.penchant.enchantment;

import archives.tater.penchant.util.PenchantUtil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;

public record EquipmentValueEffect(EquipmentSlotGroup slot, ItemPredicate equipmentCondition, EnchantmentValueEffect effect) {
    public static final Codec<EquipmentValueEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(EquipmentValueEffect::slot),
            ItemPredicate.CODEC.optionalFieldOf("equipment_condition", PenchantUtil.ANY_ITEM).forGetter(EquipmentValueEffect::equipmentCondition),
            EnchantmentValueEffect.CODEC.fieldOf("effect").forGetter(EquipmentValueEffect::effect)
    ).apply(instance, EquipmentValueEffect::new));

    public EquipmentValueEffect(ItemPredicate equipmentCondition, EnchantmentValueEffect effect) {
        this(EquipmentSlotGroup.ANY, equipmentCondition, effect);
    }

    public EquipmentValueEffect(EquipmentSlotGroup slot, EnchantmentValueEffect effect) {
        this(slot, PenchantUtil.ANY_ITEM, effect);
    }

    public EquipmentValueEffect(EnchantmentValueEffect effect) {
        this(EquipmentSlotGroup.ANY, PenchantUtil.ANY_ITEM, effect);
    }

    public float applyIfMatch(float value, int enchantmentLevel, RandomSource random, EquipmentSlot slot, ItemStack equipment) {
        return this.slot.test(slot) && equipmentCondition.test(equipment)
                ? effect.process(enchantmentLevel, random, value)
                : value;
    }
}
