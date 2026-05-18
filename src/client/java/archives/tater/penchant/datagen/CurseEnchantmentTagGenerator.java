package archives.tater.penchant.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.concurrent.CompletableFuture;

public class CurseEnchantmentTagGenerator extends FabricTagsProvider<Enchantment> {
    public CurseEnchantmentTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ENCHANTMENT, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        builder(EnchantmentTags.TRADEABLE)
                .removeTag(EnchantmentTags.CURSE);
        builder(EnchantmentTags.ON_RANDOM_LOOT)
                .removeTag(EnchantmentTags.CURSE);
        builder(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT)
                .forceAddTag(EnchantmentTags.CURSE);
    }
}
