package archives.tater.penchant.datagen;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;

import java.util.concurrent.CompletableFuture;

public class EnchantmentTagGenerator extends EnchantmentTagsProvider {

    public EnchantmentTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        tag(PenchantEnchantmentTags.DISABLED);

        tag(EnchantmentTags.IN_ENCHANTING_TABLE)
                .tagex_excludeTag(PenchantEnchantmentTags.DISABLED);
        tag(EnchantmentTags.TRADEABLE)
                .tagex_excludeTag(PenchantEnchantmentTags.DISABLED);
        tag(EnchantmentTags.ON_RANDOM_LOOT)
                .tagex_excludeTag(PenchantEnchantmentTags.DISABLED);
        tag(EnchantmentTags.ON_TRADED_EQUIPMENT)
                .tagex_excludeTag(PenchantEnchantmentTags.DISABLED);
        tag(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT)
                .tagex_excludeTag(PenchantEnchantmentTags.DISABLED);
    }
}
