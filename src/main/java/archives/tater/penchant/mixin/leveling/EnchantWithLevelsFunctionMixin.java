package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.component.EnchantmentProgress;
import archives.tater.penchant.util.PenchantmentHelper;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;

@Mixin(EnchantWithLevelsFunction.class)
public class EnchantWithLevelsFunctionMixin {
    @ModifyExpressionValue(
            method = "run",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/core/RegistryAccess;Ljava/util/Optional;)Lnet/minecraft/world/item/ItemStack;")
    )
    private static ItemStack addProgress(ItemStack original, @Local(argsOnly = true) LootContext context) {
        if (PenchantmentHelper.getComponentType(original) == DataComponents.STORED_ENCHANTMENTS) return original;

        EnchantmentProgress.addRandomProgress(original, context.getRandom());

        return original;
    }
}
