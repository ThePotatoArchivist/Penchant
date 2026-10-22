package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.loot.DroppedEquipmentCheck;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Optional;

public class PenchantLootData {
    private static <T> ContextKey<T> createKey(String path) {
        return new ContextKey<>(Penchant.id(path));
    }

    private static ContextKeySet register(String path, ContextKeySet.Builder builder) {
        return Registry.register(BuiltInRegistries.CONTEXT_KEY_SET, Penchant.id(path), builder.build());
    }

    public static final ContextKey<ItemInstance> DROPPED_EQUIPMENT = createKey("dropped_equipment");
    public static final ContextKey<EquipmentSlot> DROPPED_EQUIPMENT_SLOT = createKey("dropped_equipment_slot");

    public static final ContextKeySet ENCHANTED_EQUIPMENT_DROP = register("enchanted_equipment_drop", new ContextKeySet.Builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ENCHANTMENT_LEVEL)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.DAMAGE_SOURCE)
            .required(DROPPED_EQUIPMENT)
            .required(DROPPED_EQUIPMENT_SLOT)
            .optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .optional(LootContextParams.ATTACKING_ENTITY)
    );

    public static LootContext equipmentDropContext(ServerLevel serverLevel, int enchantmentLevel, Entity victim, DamageSource source, EquipmentSlot slot, ItemInstance item) {
        return new LootContext.Builder(new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, victim)
                .withParameter(LootContextParams.ENCHANTMENT_LEVEL, enchantmentLevel)
                .withParameter(LootContextParams.ORIGIN, victim.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                .withParameter(DROPPED_EQUIPMENT, item)
                .withParameter(DROPPED_EQUIPMENT_SLOT, slot)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, source.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, source.getDirectEntity())
                .create(LootContextParamSets.ENCHANTED_DAMAGE))
                .create(Optional.empty());
    }

    public static void init() {
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, Penchant.id("dropped_equipment"), DroppedEquipmentCheck.CODEC);
    }
}
