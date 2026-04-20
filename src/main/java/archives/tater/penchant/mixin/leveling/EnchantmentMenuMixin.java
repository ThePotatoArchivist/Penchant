package archives.tater.penchant.mixin.leveling;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.inventory.EnchantmentMenu;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {
//    @WrapOperation(
//            method = "method_17410",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;enchant(Lnet/minecraft/core/Holder;I)V")
//    )
//    private void levelOne(ItemStack instance, Holder<Enchantment> enchantment, int level, Operation<Void> original) {
//        if (enchantment.is(PenchantEnchantmentTags.NO_LEVELING))
//            original.call(instance, enchantment, level);
//        else
//            original.call(instance, enchantment, 1);
//    }
}
