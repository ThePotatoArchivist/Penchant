package archives.tater.penchant.datagen;

import archives.tater.penchant.Penchant;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LootEnchantmentTagGenerator extends EnchantmentTagsProvider {

    public static final List<ResourceKey<Enchantment>> RARE = List.of(
            Enchantments.FROST_WALKER, // igloo
            Enchantments.FIRE_ASPECT, // Nether fortress
            Enchantments.FLAME, // nether fortress
            Enchantments.SILK_TOUCH, // mineshaft, dungeon
            Enchantments.FORTUNE, // mineshaft, dungeon
            Enchantments.LUNGE, // jockeys
            Enchantments.CHANNELING, // ruins, buried treasure
            Enchantments.RIPTIDE, // ruins, buried treasure
            Enchantments.THORNS, // dungeon
            Enchantments.INFINITY // dungeon
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
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        var rare = TagKey.create(Registries.ENCHANTMENT, Penchant.id("rare"));
        var uncommon = TagKey.create(Registries.ENCHANTMENT, Penchant.id("uncommon"));
        var common = TagKey.create(Registries.ENCHANTMENT, Penchant.id("common"));

        //noinspection unchecked
        tag(rare)
                .add(RARE.toArray(ResourceKey[]::new));

        //noinspection unchecked
        tag(uncommon)
                .add(UNCOMMON.toArray(ResourceKey[]::new))
                .addOptional(ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath("farmersdelight", "backstabbing")));

        //noinspection unchecked
        tag(common)
                .add(COMMON.toArray(ResourceKey[]::new));

        tag(EnchantmentTags.TREASURE)
                .addTag(rare);
        tag(EnchantmentTags.NON_TREASURE)
                .tagex_excludeTag(rare);
        tag(EnchantmentTags.IN_ENCHANTING_TABLE)
                .tagex_excludeTag(uncommon)
                .tagex_excludeTag(rare);
        tag(EnchantmentTags.TRADEABLE)
                .tagex_excludeTag(common)
                .addTag(uncommon);
        tag(EnchantmentTags.ON_RANDOM_LOOT)
                .addTag(uncommon)
                .addTag(rare);
        tag(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT)
                .addTag(uncommon)
                .addTag(rare);
    }
}
