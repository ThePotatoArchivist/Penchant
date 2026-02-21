package archives.tater.penchant.mixin.effect;

import archives.tater.penchant.enchantment.UnbreakableEffect;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipProvider;

@SuppressWarnings("ConstantValue")
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Definition(id = "UNBREAKABLE", field = "Lnet/minecraft/core/component/DataComponents;UNBREAKABLE:Lnet/minecraft/core/component/DataComponentType;")
    @Definition(id = "has", method = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z")
    @Expression("this.has(UNBREAKABLE)")
    @ModifyExpressionValue(
            method = "isDamageableItem",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean unbreakableEnchantment(boolean original) {
        return original || UnbreakableEffect.isUnbreakable((ItemStack) (Object) this);
    }

    @Definition(id = "tooltipProvider", local = @Local(type = TooltipProvider.class))
    @Expression("tooltipProvider != null")
    @WrapOperation(
            method = "addToTooltip",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean unbreakableEnchantmentTooltip(Object left, Object right, Operation<Boolean> original) {
        return original.call(left, right) || left == DataComponents.UNBREAKABLE && UnbreakableEffect.isUnbreakable((ItemStack) (Object) this);
    }
}
