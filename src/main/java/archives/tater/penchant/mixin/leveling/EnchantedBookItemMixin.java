package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.objectweb.asm.Opcodes;

import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {

    @ModifyExpressionValue(
            method = "createForEnchantment",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/enchantment/EnchantmentInstance;level:I", opcode = Opcodes.GETFIELD)
    )
    private static int levelOne(int original, @Local(argsOnly = true) EnchantmentInstance instance) {
        return instance.enchantment.is(PenchantEnchantmentTags.NO_LEVELING) ? original : 1;
    }
}
