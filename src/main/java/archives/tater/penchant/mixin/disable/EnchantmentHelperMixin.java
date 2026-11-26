package archives.tater.penchant.mixin.disable;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(
            method = "lambda$getAvailableEnchantmentResults$0",
            at = @At("RETURN")
    )
    private static boolean disableEnchantment(boolean original, @Local(argsOnly = true, name = "enchantment") Holder<Enchantment> enchantment) {
        return original && !enchantment.is(PenchantEnchantmentTags.DISABLED);
    }
}
