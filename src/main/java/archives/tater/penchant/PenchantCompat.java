package archives.tater.penchant;

import archives.tater.penchant.api.EnchantmentCompatibleCallback;

import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.loader.api.FabricLoader;

import org.vulpixel.enchiridion.world.item.enchantment.category.EnchantmentCategoryHelper;

public class PenchantCompat {
    public static final boolean ENCHIRIDION_INSTALLED = FabricLoader.getInstance().isModLoaded("enchiridion");

    public static void init() {
        if (PenchantCompat.ENCHIRIDION_INSTALLED)
            EnchantmentCompatibleCallback.EVENT.register((enchantment, existing, stack, registries) -> {
                var firstCategory = EnchantmentCategoryHelper.getFirstEnchantmentCategoryForEnchantment(registries, enchantment).orElse(null);
                var secondCategory = EnchantmentCategoryHelper.getFirstEnchantmentCategoryForEnchantment(registries, existing).orElse(null);
                if (firstCategory != null && firstCategory.equals(secondCategory)) {
                    if (EnchantmentCategoryHelper.isCategoryLimitReached(firstCategory, stack, enchantment))
                        return TriState.FALSE;
                }
                return TriState.DEFAULT;
            });
    }
}
