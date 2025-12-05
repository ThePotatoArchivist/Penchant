package archives.tater.penchant.mixin.disable;

import archives.tater.penchant.PenchantEnchantmentTags;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {
    @ModifyReturnValue(
            method = "method_60291",
            at = @At("RETURN")
    )
    private static boolean disableEnchantment(boolean original, @Local(argsOnly = true) Holder<Enchantment> enchantment) {
        return original && !enchantment.is(PenchantEnchantmentTags.DISABLED);
    }
}
