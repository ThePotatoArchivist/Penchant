import archives.tater.penchant.PenchantCompat;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

public class PenchantMixinPlugin implements IMixinConfigPlugin {
    private static final String PREFIX = "archives.tater.penchant.mixin";
    public static final String COMPAT_PREFIX = "compat";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var relativeName = mixinClassName.substring(PREFIX.length() + 1);
        if (!relativeName.startsWith(COMPAT_PREFIX)) return true;

        var compatName = relativeName.substring(COMPAT_PREFIX.length() + 1, relativeName.indexOf('.', COMPAT_PREFIX.length() + 1));
        return switch (compatName) {
            case "modestmagic" -> PenchantCompat.MODEST_MAGIC_INSTALLED;
            default -> true;
        };
    }
}
