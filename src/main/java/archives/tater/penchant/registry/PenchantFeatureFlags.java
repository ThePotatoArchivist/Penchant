package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;

import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagRegistry;

public class PenchantFeatureFlags {
    public static PenchantFeatureFlags INSTANCE;

    public final FeatureFlag reworkEnchantingTable;

    public PenchantFeatureFlags(FeatureFlagRegistry.Builder builder) {
        reworkEnchantingTable = builder.create(Penchant.id("rework_enchanting_table"));
    }

    public static void init(FeatureFlagRegistry.Builder builder) {
        INSTANCE = new PenchantFeatureFlags(builder);
    }
}
