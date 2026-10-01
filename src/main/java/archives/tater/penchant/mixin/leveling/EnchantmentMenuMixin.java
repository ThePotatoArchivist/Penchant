package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.Holder;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {
    @WrapOperation(
            method = "lambda$clickMenuButton$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;enchant(Lnet/minecraft/core/Holder;I)V")
    )
    private void levelOne(ItemStack instance, Holder<Enchantment> enchantment, int level, Operation<Void> original) {
        if (enchantment.is(PenchantEnchantmentTags.NO_LEVELING))
            original.call(instance, enchantment, level);
        else
            original.call(instance, enchantment, 1);
    }
}
