package archives.tater.penchant.mixin.client.keymapping;

import archives.tater.penchant.client.ScreenKeyMapping;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.KeyMapping;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    @Definition(id = "MAP", field = "Lnet/minecraft/client/KeyMapping;MAP:Ljava/util/Map;")
    @Definition(id = "put", method = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
    @Expression("MAP.put(?, ?)")
    @WrapOperation(
            method = {
                    "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V",
                    "resetMapping"
            },
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static <K, V> @Nullable V preventPutScreenKey(Map<K, V> instance, K k, V v, Operation<V> original) {
        return v instanceof ScreenKeyMapping ? null : original.call(instance, k, v);
    }
}
