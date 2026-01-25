package archives.tater.penchant.mixin.misc;

import archives.tater.penchant.registry.PenchantFlag;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
    @Definition(id = "has", method = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z")
    @Definition(id = "STORED_ENCHANTMENTS", field = "Lnet/minecraft/core/component/DataComponents;STORED_ENCHANTMENTS:Lnet/minecraft/core/component/DataComponentType;")
    @Expression("?.has(STORED_ENCHANTMENTS)")
    @WrapOperation(
            method = "createResult",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean preventBookEnchanting(ItemStack instance, DataComponentType<ItemEnchantments> dataComponentType, Operation<Boolean> original) {
        return !PenchantFlag.NO_ANVIL_BOOKS.isEnabled() && original.call(instance, dataComponentType);
    }
}
