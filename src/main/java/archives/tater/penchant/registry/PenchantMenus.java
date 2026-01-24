package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.menu.PenchantmentMenu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class PenchantMenus {
    public static final MenuType<PenchantmentMenu> PENCHANTMENT_MENU = Registry.register(
            BuiltInRegistries.MENU,
            Penchant.id("penchantment"),
            new MenuType<>(PenchantmentMenu::new, FeatureFlags.VANILLA_SET) // TODO feature flags
    );

    public static void init() {

    }
}
