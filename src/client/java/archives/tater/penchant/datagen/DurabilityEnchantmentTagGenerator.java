package archives.tater.penchant.datagen;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.concurrent.CompletableFuture;

public class DurabilityEnchantmentTagGenerator extends FabricTagsProvider<Enchantment> {
    public DurabilityEnchantmentTagGenerator(FabricPackOutput output, CompletableFuture<Provider> lookupProvider) {
        super(output, Registries.ENCHANTMENT, lookupProvider);
    }

    @Override
    protected void addTags(Provider provider) {
         builder(PenchantEnchantmentTags.DISABLED).add(Enchantments.MENDING);
    }
}
