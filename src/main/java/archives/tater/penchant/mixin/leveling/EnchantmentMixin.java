package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.util.PenchantmentHelper;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @ModifyExpressionValue(
            method = "getFullname",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I")
    )
    private static int nameWithoutLevel(int original) {
        return PenchantmentHelper.NO_LEVEL_NAME_CONTEXT.isBound() ? 1 : original;
    }
}
