package archives.tater.penchant.mixin.effect;

import archives.tater.penchant.UnbreakableEffect;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

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

    @WrapOperation(
            method = "addUnitComponentToTooltip",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z")
    )
    private boolean unbreakableEnchantmentTooltip(ItemStack instance, DataComponentType<Unit> dataComponentType, Operation<Boolean> original) {
        return original.call(instance, dataComponentType) || dataComponentType == DataComponents.UNBREAKABLE && UnbreakableEffect.isUnbreakable((ItemStack) (Object) this);
    }
}
