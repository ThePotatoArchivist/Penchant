package archives.tater.penchant.datagen;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.advancement.ExtractEnchantmentTrigger;
import archives.tater.penchant.advancement.OpenTableTrigger;
import archives.tater.penchant.registry.PenchantAdvancements;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;

import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class AdvancementGenerator extends FabricAdvancementProvider {
    public AdvancementGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    public static final List<ResourceKey<Enchantment>> NON_TABLE_ENCHANTMENTS = List.of(
            Enchantments.SWIFT_SNEAK,
            Enchantments.SOUL_SPEED,
            Enchantments.FROST_WALKER,
            Enchantments.WIND_BURST
    );

    private static DisplayInfo displayInfo(ItemLike icon, Identifier id, AdvancementType type) {
        return new DisplayInfo(
                icon.asItem().getDefaultInstance(),
                Component.translatable(id.toLanguageKey("advancements", "title")),
                Component.translatable(id.toLanguageKey("advancements", "description")),
                Optional.empty(),
                type,
                true,
                true,
                false
        );
    }

    private static AdvancementHolder register(Identifier id, ItemLike icon, AdvancementType type, Consumer<AdvancementHolder> consumer, Consumer<Advancement.Builder> init) {
        var builder = Advancement.Builder.advancement()
                .display(displayInfo(icon, id, type));
        init.accept(builder);
        var advancement = builder.build(id);
        consumer.accept(advancement);
        return advancement;
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer) {
        var enchantments = provider.lookupOrThrow(Registries.ENCHANTMENT);
        var enchanter = Advancement.Builder.advancement().build(Identifier.withDefaultNamespace("story/enchant_item"));

        var enchantedBookshelf = register(Penchant.id("enchanted_bookshelf"), Items.CHISELED_BOOKSHELF, AdvancementType.TASK, consumer, builder -> builder
                .parent(enchanter)
                .addCriterion("has_enchantment", PenchantAdvancements.OPEN_TABLE.createCriterion(
                        new OpenTableTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.ANY, Optional.empty(), true)
                ))
        );

        var allEnchantments = register(Penchant.id("all_enchantments"), Items.ENCHANTED_BOOK, AdvancementType.GOAL, consumer, builder -> {
            builder
                    .parent(enchantedBookshelf)
                    .requirements(AdvancementRequirements.Strategy.AND);

            Stream.concat(
                    LootEnchantmentTagGenerator.UNCOMMON.stream().map(enchantments::getOrThrow),
                    LootEnchantmentTagGenerator.RARE.stream().map(enchantments::getOrThrow)
            ).forEach(enchantment -> builder.addCriterion(
                    enchantment.key().identifier().toString(),
                    InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().withComponents(
                            DataComponentMatchers.Builder.components().partial(
                                    DataComponentPredicates.STORED_ENCHANTMENTS,
                                    EnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(
                                            new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.ANY)
                                    ))
                            ).build()
                    ))
            ));
        });

        var fullLibrary = register(Penchant.id("full_library"), Items.BOOKSHELF, AdvancementType.GOAL, consumer, builder -> builder
                .parent(enchanter)
                .addCriterion("has_books", PenchantAdvancements.OPEN_TABLE.createCriterion(
                        new OpenTableTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.atLeast(45), Optional.empty(), false)
                ))
        );

        var babel = register(Penchant.id("babel"), Items.BOOKSHELF, AdvancementType.CHALLENGE, consumer, builder -> builder
                .parent(fullLibrary)
                .addCriterion("has_books", PenchantAdvancements.OPEN_TABLE.createCriterion(
                        new OpenTableTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.atLeast(300), Optional.empty(), false)
                ))
                .rewards(new AdvancementRewards.Builder()
                        .addExperience(100)
                )
        );

        var extractEnchantment = register(Penchant.id("extract_enchantment"), Items.GRINDSTONE, AdvancementType.TASK, consumer, builder -> builder
                .parent(enchanter)
                .addCriterion("extract", PenchantAdvancements.EXTRACT_ENCHANTMENT.createCriterion(
                        new ExtractEnchantmentTrigger.TriggerInstance(Optional.empty(), ItemPredicate.Builder.item().build(), Optional.empty())
                ))
        );
    }
}
