package archives.tater.penchant.loot;

import archives.tater.penchant.registry.PenchantLootData;
import archives.tater.penchant.util.PenchantUtil;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public record DroppedEquipmentCheck(EquipmentSlotGroup slot, ItemPredicate item) implements LootItemCondition {
    public static final MapCodec<DroppedEquipmentCheck> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(DroppedEquipmentCheck::slot),
            ItemPredicate.CODEC.optionalFieldOf("item", PenchantUtil.ANY_ITEM).forGetter(DroppedEquipmentCheck::item)
    ).apply(instance, DroppedEquipmentCheck::new));

    public DroppedEquipmentCheck(EquipmentSlotGroup slot) {
        this(slot, PenchantUtil.ANY_ITEM);
    }

    public DroppedEquipmentCheck(ItemPredicate item) {
        this(EquipmentSlotGroup.ANY, item);
    }

    @Override
    public MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(LootContext lootContext) {
        var slot = lootContext.getOptional(PenchantLootData.DROPPED_EQUIPMENT_SLOT);
        var stack = lootContext.getOptional(PenchantLootData.DROPPED_EQUIPMENT);

        return slot != null && this.slot.test(slot) && stack != null && item.test(stack);
    }

    public static Builder droppedEquipment() {
        return new Builder();
    }

    public static class Builder implements LootItemCondition.Builder {
        private EquipmentSlotGroup slot = EquipmentSlotGroup.ANY;
        private ItemPredicate item = PenchantUtil.ANY_ITEM;

        public Builder slot(EquipmentSlotGroup slot) {
            this.slot = slot;
            return this;
        }

        public Builder item(ItemPredicate item) {
            this.item = item;
            return this;
        }

        public Builder item(ItemPredicate.Builder item) {
            return item(item.build());
        }

        @Override
        public LootItemCondition build() {
            return new DroppedEquipmentCheck(slot, item);
        }
    }
}
