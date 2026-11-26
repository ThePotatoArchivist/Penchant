package archives.tater.penchant.mixin.disable;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {
    @ModifyReturnValue(
            method = "lambda$run$1",
            at = @At("RETURN")
    )
    private static boolean disableEnchantment(boolean original, @Local(argsOnly = true, name = "candidate") Holder<Enchantment> candidate) {
        return original && !candidate.is(PenchantEnchantmentTags.DISABLED);
    }
}
