package archives.tater.penchant.datagen;

import archives.tater.penchant.Penchant;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

import com.google.common.collect.Streams;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static archives.tater.penchant.datagen.DatagenUtil.createEmptyAdvancement;
import static archives.tater.penchant.datagen.DatagenUtil.registerAdvancement;

public class LootAdvancementGenerator extends FabricAdvancementProvider {

    public static final ResourceLocation ALL_ENCHANTMENTS = Penchant.id("all_enchantments");

    public LootAdvancementGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        var enchantments = registryLookup.lookupOrThrow(Registries.ENCHANTMENT);
        var enchantedBookshelf = createEmptyAdvancement(TableAdvancementGenerator.ENCHANTED_BOOKSHELF);

        var allEnchantments = registerAdvancement(ALL_ENCHANTMENTS, Items.ENCHANTED_BOOK, AdvancementType.GOAL, consumer, builder -> {
            builder
                    .parent(enchantedBookshelf)
                    .requirements(AdvancementRequirements.Strategy.AND);

            Streams.concat(
                    LootEnchantmentTagGenerator.UNCOMMON.stream(),
                    LootEnchantmentTagGenerator.RARE.stream(),
                    Stream.of(
                            Enchantments.SWIFT_SNEAK,
                            Enchantments.SOUL_SPEED,
                            Enchantments.WIND_BURST
                    )
            )
                    .map(enchantments::getOrThrow)
                    .forEach(enchantment -> builder.addCriterion(
                    enchantment.key().location().toString(),
                    InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item()
                            .withSubPredicate(
                                    ItemSubPredicates.STORED_ENCHANTMENTS,
                                    ItemEnchantmentsPredicate.StoredEnchantments.storedEnchantments(List.of(
                                            new EnchantmentPredicate(enchantment, MinMaxBounds.Ints.ANY)
                                    ))
                            ).build()
                    )
            ));
        });
    }
}
