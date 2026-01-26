package archives.tater.penchant.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;

import java.util.concurrent.CompletableFuture;

public class CurseEnchantmentTagGenerator extends EnchantmentTagsProvider {
    public CurseEnchantmentTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(EnchantmentTags.TRADEABLE)
                .tagex_forceExcludeTag(EnchantmentTags.CURSE);
        tag(EnchantmentTags.ON_RANDOM_LOOT)
                .tagex_forceExcludeTag(EnchantmentTags.CURSE);
        tag(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT)
                .forceAddTag(EnchantmentTags.CURSE);
    }
}
