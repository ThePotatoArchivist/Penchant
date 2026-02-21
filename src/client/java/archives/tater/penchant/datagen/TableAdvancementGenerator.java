package archives.tater.penchant.datagen;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.advancement.ExtractEnchantmentTrigger;
import archives.tater.penchant.advancement.OpenTableTrigger;
import archives.tater.penchant.registry.PenchantAdvancements;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static archives.tater.penchant.datagen.DatagenUtil.createEmptyAdvancement;
import static archives.tater.penchant.datagen.DatagenUtil.registerAdvancement;

public class TableAdvancementGenerator extends FabricAdvancementProvider {

    public static final ResourceLocation ENCHANTED_BOOKSHELF = Penchant.id("enchanted_bookshelf");
    public static final ResourceLocation FULL_LIBRARY = Penchant.id("full_library");
    public static final ResourceLocation BABEL = Penchant.id("babel");
    public static final ResourceLocation EXTRACT_ENCHANTMENT = Penchant.id("extract_enchantment");

    public TableAdvancementGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer) {
        var enchantments = provider.lookupOrThrow(Registries.ENCHANTMENT);
        var enchanter = createEmptyAdvancement(ResourceLocation.withDefaultNamespace("story/enchant_item"));

        var enchantedBookshelf = registerAdvancement(ENCHANTED_BOOKSHELF, Items.CHISELED_BOOKSHELF, AdvancementType.TASK, consumer, builder -> builder
                .parent(enchanter)
                .addCriterion("has_enchantment", PenchantAdvancements.OPEN_TABLE.createCriterion(
                        new OpenTableTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.ANY, Optional.empty(), true)
                ))
        );

        var fullLibrary = registerAdvancement(FULL_LIBRARY, Items.BOOKSHELF, AdvancementType.GOAL, consumer, builder -> builder
                .parent(enchanter)
                .addCriterion("has_books", PenchantAdvancements.OPEN_TABLE.createCriterion(
                        new OpenTableTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.atLeast(45), Optional.empty(), false)
                ))
        );

        var babel = registerAdvancement(BABEL, Items.BOOKSHELF, AdvancementType.CHALLENGE, consumer, builder -> builder
                .parent(fullLibrary)
                .addCriterion("has_books", PenchantAdvancements.OPEN_TABLE.createCriterion(
                        new OpenTableTrigger.TriggerInstance(Optional.empty(), MinMaxBounds.Ints.atLeast(300), Optional.empty(), false)
                ))
                .rewards(new AdvancementRewards.Builder()
                        .addExperience(100)
                )
        );

        var extractEnchantment = registerAdvancement(EXTRACT_ENCHANTMENT, Items.GRINDSTONE, AdvancementType.TASK, consumer, builder -> builder
                .parent(enchanter)
                .addCriterion("extract", PenchantAdvancements.EXTRACT_ENCHANTMENT.createCriterion(
                        new ExtractEnchantmentTrigger.TriggerInstance(Optional.empty(), ItemPredicate.Builder.item().build(), Optional.empty())
                ))
        );
    }

}
