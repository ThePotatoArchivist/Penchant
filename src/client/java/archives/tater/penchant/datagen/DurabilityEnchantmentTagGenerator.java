package archives.tater.penchant.datagen;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.concurrent.CompletableFuture;

public class DurabilityEnchantmentTagGenerator extends FabricTagProvider<Enchantment> {
    public DurabilityEnchantmentTagGenerator(FabricDataOutput output, CompletableFuture<Provider> lookupProvider) {
        super(output, Registries.ENCHANTMENT, lookupProvider);
    }

    @Override
    protected void addTags(Provider provider) {
         getOrCreateTagBuilder(PenchantEnchantmentTags.DISABLED).add(Enchantments.MENDING);
    }
}
