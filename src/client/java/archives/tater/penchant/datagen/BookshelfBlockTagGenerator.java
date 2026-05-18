package archives.tater.penchant.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class BookshelfBlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public BookshelfBlockTagGenerator(FabricDataOutput output, CompletableFuture<Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(Provider provider) {
        getOrCreateTagBuilder(BlockTags.ENCHANTMENT_POWER_PROVIDER)
                .add(Blocks.CHISELED_BOOKSHELF, Blocks.LECTERN)
                .forceAddTag(ConventionalBlockTags.BOOKSHELVES)
                .addOptionalTag(TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("blueprint", "wooden_chiseled_bookshelves")));
    }
}
