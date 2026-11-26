package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.Holder;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(CreativeModeTabs.class)
public class CreativeModeTabsMixin {
    @WrapOperation(
            method = {
                    "lambda$generateEnchantmentBookTypesAllLevels$0",
                    "lambda$generateEnchantmentBookTypesOnlyMaxLevel$0",
            },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I")
    )
    private static int onlyOneBookLevel(Enchantment instance, Operation<Integer> original, @Local(argsOnly = true, name = "enchantment") Holder.Reference<Enchantment> enchantment) {
        return enchantment.is(PenchantEnchantmentTags.NO_LEVELING) ? original.call(instance) : instance.getMinLevel();
    }
}
