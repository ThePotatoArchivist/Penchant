package archives.tater.penchant.datagen;

import archives.tater.penchant.enchantment.EquipmentValueEffect;
import archives.tater.penchant.registry.PenchantEnchantmentTags;
import archives.tater.penchant.registry.PenchantEnchantments;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityTypePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.advancements.predicates.DataComponentMatchers.Builder.components;
import static net.minecraft.advancements.predicates.ItemPredicate.Builder.item;
import static net.minecraft.advancements.predicates.entity.EntityPredicate.Builder.entity;
import static net.minecraft.core.component.predicates.EnchantmentsPredicate.enchantments;
import static net.minecraft.world.item.enchantment.Enchantment.definition;
import static net.minecraft.world.item.enchantment.Enchantment.enchantment;
import static net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition.hasProperties;

public class DropEnchantmentGenerator extends FabricDynamicRegistryProvider {
    public DropEnchantmentGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        var items = registries.lookupOrThrow(Registries.ITEM);
        var entities = registries.lookupOrThrow(Registries.ENTITY_TYPE);

        entries.add(
                Enchantments.LOOTING,
                enchantment(definition(
                        registries.getOrThrow(ItemTags.MELEE_WEAPON_ENCHANTABLE),
                        2,
                        3,
                        Enchantment.dynamicCost(15, 9),
                        Enchantment.dynamicCost(65, 9),
                        4,
                        EquipmentSlotGroup.MAINHAND
                )).withEffect(
                        PenchantEnchantments.EQUIPMENT_DROPS,
                        EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM,
                        new EquipmentValueEffect(
                                item().withComponents(components().partial(DataComponentPredicates.ENCHANTMENTS,
                                        enchantments(List.of(
                                                new EnchantmentPredicate(
                                                        registries.getOrThrow(PenchantEnchantmentTags.IGNORE_GUARANTEED_DROP),
                                                        MinMaxBounds.Ints.ANY
                                                )
                                        ))
                                ).build()).build(),
                                new AddValue(LevelBasedValue.perLevel(0.35f))
                        ),
                        hasProperties(
                                LootContext.EntityTarget.ATTACKER,
                                entity().entityType(EntityTypePredicate.of(entities, EntityTypes.PLAYER))
                        )
                ).build(Enchantments.LOOTING.identifier())
        );
    }

    @Override
    public String getName() {
        return "Enchantments";
    }
}
