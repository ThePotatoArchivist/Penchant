package archives.tater.penchant.compat;

import net.fabricmc.loader.api.FabricLoader;

public class PenchantCompat {
    public static final boolean ENCHIRIDION_INSTALLED = FabricLoader.getInstance().isModLoaded("enchiridion");
    public static final boolean MODEST_MAGIC_INSTALLED = FabricLoader.getInstance().isModLoaded("modestmagic");
    public static final boolean ITEM_DESCRIPTIONS_INSTALLED = FabricLoader.getInstance().isModLoaded("item_descriptions");

    public static void init() {
        PenchantEnchiridionCompat.init();
    }

}
