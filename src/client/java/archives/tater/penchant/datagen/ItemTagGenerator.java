package archives.tater.penchant.datagen;

import archives.tater.penchant.registry.PenchantItemTags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends FabricTagsProvider.ItemTagsProvider {
    public ItemTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(PenchantItemTags.MAX_LEVEL_ENCHANTMENTS)
                .forceAddTag(ItemTags.HARNESSES)
                .forceAddTag(ConventionalItemTags.HORSE_ARMORS)
                .forceAddTag(ConventionalItemTags.NAUTILUS_ARMORS)
                .addOptional(createKey("enderscape", "magnia_attractor"));

        builder(PenchantItemTags.PROPORTIONAL_ENCHANTMENT_PROGRESS)
                .forceAddTag(ItemTags.ARMOR_ENCHANTABLE)
                .forceAddTag(ConventionalItemTags.SHIELD_TOOLS);

        builder(PenchantItemTags.LEVEL_UP_BY_KILLING)
                .forceAddTag(ItemTags.ARMOR_ENCHANTABLE)
                .forceAddTag(ConventionalItemTags.SHIELD_TOOLS);

        builder(PenchantItemTags.DEFAULT_PROGRESS_COST_FACTOR_ONE)
                .forceAddTag(ConventionalItemTags.BOW_TOOLS)
                .forceAddTag(ConventionalItemTags.CROSSBOW_TOOLS);

        builder(PenchantItemTags.LEVEL_UP_BY_USE)
                .addOptional(createKey("enderscape", "mirror"));
    }

    private static ResourceKey<Item> createKey(String namespace, String path) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(namespace, path));
    }
}
