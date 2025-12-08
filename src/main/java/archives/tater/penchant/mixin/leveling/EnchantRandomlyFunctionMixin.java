package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.EnchantmentProgress;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {
    @ModifyReturnValue(
            method = "enchantItem",
            at = @At("RETURN")
    )
    private static ItemStack addProgress(ItemStack original) {
        if (EnchantmentHelper.getComponentType(original) == DataComponents.STORED_ENCHANTMENTS) return original;

        var damage = original.getDamageValue();
        if (damage == 0) return original;

        EnchantmentProgress.addToProgress(original, damage, null);

        return original;
    }
}
