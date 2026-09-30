package archives.tater.penchant;

import net.fabricmc.loader.api.FabricLoader;

public class PenchantCompat {
    public static final boolean ENCHIRIDION_INSTALLED = FabricLoader.getInstance().isModLoaded("enchiridion");
}
