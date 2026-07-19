package archives.tater.penchant.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class BookshelfBlockTagGenerator extends FabricTagsProvider.BlockTagsProvider {
    public BookshelfBlockTagGenerator(FabricPackOutput output, CompletableFuture<Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(Provider provider) {
        builder(BlockTags.ENCHANTMENT_POWER_PROVIDER)
                .add(BlockItemIds.CHISELED_BOOKSHELF)
                .add(BlockItemIds.LECTERN)
                .forceAddTag(ConventionalBlockTags.BOOKSHELVES)
                .addOptionalTag(TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, "chiseled_bookshelves")));
    }
}
