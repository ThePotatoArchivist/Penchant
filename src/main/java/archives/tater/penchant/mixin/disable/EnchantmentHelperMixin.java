package archives.tater.penchant.mixin.disable;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyVariable(
            method = "getAvailableEnchantmentResults",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static Stream<Holder<Enchantment>> disableEnchantment(Stream<Holder<Enchantment>> original) {
        return original.filter(enchantment -> !enchantment.is(PenchantEnchantmentTags.DISABLED));
    }
}
