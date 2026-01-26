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

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LootEnchantmentTagGenerator extends FabricTagProvider<Enchantment> {

    public static final List<ResourceKey<Enchantment>> RARE = List.of(
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

    public static final List<ResourceKey<Enchantment>> UNCOMMON = List.of(
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
    );

    public static final List<ResourceKey<Enchantment>> COMMON = List.of(
            Enchantments.EFFICIENCY,
            Enchantments.PROTECTION,
            Enchantments.SHARPNESS,
            Enchantments.UNBREAKING,
            Enchantments.POWER,
            Enchantments.PIERCING,
            Enchantments.QUICK_CHARGE,
            Enchantments.IMPALING,
            Enchantments.LOYALTY,
            Enchantments.LURE
    );

    public LootEnchantmentTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENCHANTMENT, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        var rare = TagKey.create(Registries.ENCHANTMENT, Penchant.id("rare"));
        var uncommon = TagKey.create(Registries.ENCHANTMENT, Penchant.id("uncommon"));
        var common = TagKey.create(Registries.ENCHANTMENT, Penchant.id("common"));

        //noinspection unchecked
        builder(rare)
                .add(RARE.toArray(ResourceKey[]::new));

        //noinspection unchecked
        builder(uncommon)
                .add(UNCOMMON.toArray(ResourceKey[]::new))
                .addOptional(ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("farmersdelight", "backstabbing")));

        //noinspection unchecked
        builder(common)
                .add(COMMON.toArray(ResourceKey[]::new));

        builder(EnchantmentTags.TREASURE)
                .addTag(rare);
        builder(EnchantmentTags.NON_TREASURE)
                .tagex_excludeTag(rare);
        builder(EnchantmentTags.IN_ENCHANTING_TABLE)
                .tagex_excludeTag(uncommon)
                .tagex_excludeTag(rare);
        builder(EnchantmentTags.TRADEABLE)
                .tagex_excludeTag(common)
                .addTag(uncommon);
        builder(EnchantmentTags.ON_RANDOM_LOOT)
                .addTag(uncommon)
                .addTag(rare);
        builder(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT)
                .addTag(uncommon)
                .addTag(rare);
    }
}
