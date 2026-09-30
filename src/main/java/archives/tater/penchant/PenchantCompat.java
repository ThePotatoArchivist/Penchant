package archives.tater.penchant;

import archives.tater.penchant.api.EnchantmentCompatibleCallback;

import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.enchantment.Enchantment;

import org.vulpixel.enchiridion.world.item.enchantment.category.EnchantmentCategory;
import org.vulpixel.enchiridion.world.item.enchantment.category.EnchantmentCategoryHelper;
import org.vulpixel.enchiridion.world.item.enchantment.category.EnchiridionEnchantmentCategories;

public class PenchantCompat {
    public static final boolean ENCHIRIDION_INSTALLED = FabricLoader.getInstance().isModLoaded("enchiridion");

    public static Holder.Reference<EnchantmentCategory> getCategoryOrUncategorized(Holder<Enchantment> enchantment, HolderLookup.Provider registries) {
        return EnchantmentCategoryHelper.getFirstEnchantmentCategoryForEnchantment(registries, enchantment).orElseGet(() -> registries.getOrThrow(EnchiridionEnchantmentCategories.UNCATEGORIZED));
    }

    @SuppressWarnings("ConstantValue") // intellij bugging I think
    public static void init() {
        if (PenchantCompat.ENCHIRIDION_INSTALLED)
            EnchantmentCompatibleCallback.EVENT.register((enchantment, existing, stack, registries) -> {
                var firstCategory = getCategoryOrUncategorized(enchantment, registries);
                var secondCategory = getCategoryOrUncategorized(existing, registries);
                if (firstCategory.equals(secondCategory) && EnchantmentCategoryHelper.isCategoryLimitReached(firstCategory, stack, enchantment))
                    return TriState.FALSE;

                return TriState.DEFAULT;
            });
    }
}
