package archives.tater.penchant.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.concurrent.CompletableFuture;

public class TableEnchantmentTagGenerator extends FabricTagProvider<Enchantment> {
    public TableEnchantmentTagGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENCHANTMENT, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(EnchantmentTags.IN_ENCHANTING_TABLE)
                .tagex_exclude(Enchantments.FIRE_ASPECT)
                .tagex_exclude(Enchantments.FLAME)
                .tagex_exclude(Enchantments.SILK_TOUCH)
                .tagex_exclude(Enchantments.LUNGE)
                .tagex_exclude(Enchantments.CHANNELING)
                .tagex_exclude(Enchantments.RIPTIDE)
                .tagex_exclude(Enchantments.FORTUNE)
                .tagex_exclude(Enchantments.THORNS)
                .tagex_exclude(Enchantments.INFINITY)
                .tagex_exclude(Enchantments.RESPIRATION)
                ;
    }
}
