package archives.tater.penchant.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.concurrent.CompletableFuture;

public class CurseEnchantmentTagGenerator extends FabricTagProvider<Enchantment> {
    public CurseEnchantmentTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ENCHANTMENT, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(EnchantmentTags.TRADEABLE)
                .removeTag(EnchantmentTags.CURSE);
        tag(EnchantmentTags.ON_RANDOM_LOOT)
                .removeTag(EnchantmentTags.CURSE);
        tag(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT)
                .forceAddTag(EnchantmentTags.CURSE);
    }
}
