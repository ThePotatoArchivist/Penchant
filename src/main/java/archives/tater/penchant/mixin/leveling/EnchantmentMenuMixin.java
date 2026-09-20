package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.List;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {
    @ModifyExpressionValue(
            method = "lambda$clickMenuButton$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/EnchantmentMenu;getEnchantmentList(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/ItemStack;II)Ljava/util/List;")
    )
    private List<EnchantmentInstance> levelOne(List<EnchantmentInstance> original) {
        return original.stream().map(instance ->
            instance.enchantment().is(PenchantEnchantmentTags.NO_LEVELING)
                    ? instance
                    : new EnchantmentInstance(instance.enchantment(), 1)
        ).toList();
    }
}
