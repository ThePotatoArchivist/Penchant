package archives.tater.penchant.datagen;

import archives.tater.penchant.enchantment.UnbreakableEffect;
import archives.tater.penchant.registry.PenchantEnchantments;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;

import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.RemoveBinomial;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DurabilityEnchantmentGenerator extends FabricDynamicRegistryProvider {

    public DurabilityEnchantmentGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        var items = registries.lookupOrThrow(Registries.ITEM);
        entries.add(Enchantments.UNBREAKING, new Enchantment.Builder(new Enchantment.EnchantmentDefinition(
                items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                Optional.empty(),
                5,
                5,
                new Enchantment.Cost(5, 8),
                new Enchantment.Cost(35, 8),
                1,
                List.of(EquipmentSlotGroup.ANY)
        ))
                .withEffect(EnchantmentEffectComponents.ITEM_DAMAGE,
                        new RemoveBinomial(new LevelBasedValue.Fraction(
                                LevelBasedValue.perLevel(0, 1),
                                LevelBasedValue.constant(4)
                        ))
                )
                .withSpecialEffect(PenchantEnchantments.UNBREAKABLE, List.of(
                        new UnbreakableEffect(MinMaxBounds.Ints.atLeast(5))
                ))
                .build(Enchantments.UNBREAKING.identifier())
        );
        entries.add(Enchantments.MENDING, new Enchantment.Builder(new Enchantment.EnchantmentDefinition(
                HolderSet.empty(),
                Optional.empty(),
                1,
                1,
                new Enchantment.Cost(99, 0),
                new Enchantment.Cost(99, 0),
                1,
                List.of()
        )).build(Enchantments.MENDING.identifier()));
    }

    @Override
    public @NotNull String getName() {
        return "Enchantments";
    }
}
