package archives.tater.penchant.datagen;

import archives.tater.penchant.Penchant;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.concurrent.CompletableFuture;

public class TableEnchantmentTagGenerator extends FabricTagProvider<Enchantment> {
    public TableEnchantmentTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENCHANTMENT, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        var rare = TagKey.create(Registries.ENCHANTMENT, Penchant.id("rare"));
        var uncommon = TagKey.create(Registries.ENCHANTMENT, Penchant.id("uncommon"));

        //noinspection unchecked
        builder(rare).add(
                Enchantments.FROST_WALKER,
                Enchantments.FIRE_ASPECT,
                Enchantments.FLAME,
                Enchantments.SILK_TOUCH,
                Enchantments.FORTUNE,
                Enchantments.LUNGE,
                Enchantments.CHANNELING,
                Enchantments.RIPTIDE,
                Enchantments.THORNS,
                Enchantments.INFINITY
        );

        //noinspection unchecked
        builder(uncommon).add(
                Enchantments.RESPIRATION,
                Enchantments.AQUA_AFFINITY,
                Enchantments.DEPTH_STRIDER,
                Enchantments.FEATHER_FALLING,
                Enchantments.FIRE_PROTECTION,
                Enchantments.BLAST_PROTECTION,
                Enchantments.PROJECTILE_PROTECTION,
                Enchantments.SMITE,
                Enchantments.BANE_OF_ARTHROPODS,
                Enchantments.SWEEPING_EDGE,
                Enchantments.KNOCKBACK,
                Enchantments.PUNCH,
                Enchantments.MULTISHOT,
                Enchantments.DENSITY,
                Enchantments.BREACH,
                Enchantments.LOOTING,
                Enchantments.LUCK_OF_THE_SEA
        )
                .addOptional(ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("farmersdelight", "backstabbing")));

        builder(EnchantmentTags.TREASURE)
                .addTag(rare);
        builder(EnchantmentTags.NON_TREASURE)
                .tagex_excludeTag(rare);
        builder(EnchantmentTags.IN_ENCHANTING_TABLE)
                .tagex_excludeTag(uncommon)
                .tagex_excludeTag(rare);
        builder(EnchantmentTags.TRADEABLE)
                .tagex_forceExcludeTag(EnchantmentTags.IN_ENCHANTING_TABLE)
                .tagex_forceExcludeTag(EnchantmentTags.CURSE)
                .addTag(uncommon);
        builder(EnchantmentTags.ON_RANDOM_LOOT)
                .tagex_forceExcludeTag(EnchantmentTags.NON_TREASURE)
                .addTag(uncommon)
                .addTag(rare);
        builder(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT)
                .addTag(uncommon)
                .addTag(rare);
    }
}
