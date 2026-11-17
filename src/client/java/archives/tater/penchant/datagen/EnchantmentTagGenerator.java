package archives.tater.penchant.datagen;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.concurrent.CompletableFuture;

public class EnchantmentTagGenerator extends FabricTagProvider<Enchantment> {

    public EnchantmentTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENCHANTMENT, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(PenchantEnchantmentTags.DISABLED);

        getOrCreateTagBuilder(EnchantmentTags.IN_ENCHANTING_TABLE)
                .removeTag(PenchantEnchantmentTags.DISABLED);
        getOrCreateTagBuilder(EnchantmentTags.TRADEABLE)
                .removeTag(PenchantEnchantmentTags.DISABLED);
        getOrCreateTagBuilder(EnchantmentTags.ON_RANDOM_LOOT)
                .removeTag(PenchantEnchantmentTags.DISABLED);
        getOrCreateTagBuilder(EnchantmentTags.ON_TRADED_EQUIPMENT)
                .removeTag(PenchantEnchantmentTags.DISABLED);
        getOrCreateTagBuilder(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT)
                .removeTag(PenchantEnchantmentTags.DISABLED);
    }
}
