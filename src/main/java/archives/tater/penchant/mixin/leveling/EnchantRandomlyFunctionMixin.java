package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.component.EnchantmentProgress;
import archives.tater.penchant.util.PenchantmentHelper;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {
    @ModifyReturnValue(
            method = "enchantItem",
            at = @At("RETURN")
    )
    private static ItemStack addProgress(ItemStack original, @Local(argsOnly = true) RandomSource random) {
        if (PenchantmentHelper.getComponentType(original) == DataComponents.STORED_ENCHANTMENTS) return original;

        EnchantmentProgress.addRandomProgress(original, random);

        return original;
    }
}
