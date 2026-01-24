package archives.tater.penchant.mixin;

import archives.tater.penchant.registry.PenchantFeatureFlags;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.flag.FeatureFlags;

@Mixin(FeatureFlags.class)
public class FeatureFlagsMixin {
    @ModifyReceiver(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/flag/FeatureFlagRegistry$Builder;build()Lnet/minecraft/world/flag/FeatureFlagRegistry;")
    )
    private static FeatureFlagRegistry.Builder initFlags(FeatureFlagRegistry.Builder instance) {
        PenchantFeatureFlags.init(instance);
        return instance;
    }
}
