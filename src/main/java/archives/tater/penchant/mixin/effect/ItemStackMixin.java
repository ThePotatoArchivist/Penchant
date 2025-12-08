package archives.tater.penchant.mixin.effect;

import archives.tater.penchant.UnbreakableEffect;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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

    @Definition(id = "has", method = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z")
    @Definition(id = "UNBREAKABLE", field = "Lnet/minecraft/core/component/DataComponents;UNBREAKABLE:Lnet/minecraft/core/component/DataComponentType;")
    @Expression("this.has(UNBREAKABLE)")
    @ModifyExpressionValue(
            method = "addDetailsToTooltip",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private boolean unbreakableEnchantmentTooltip(boolean original) {
        return original || UnbreakableEffect.isUnbreakable((ItemStack) (Object) this);
    }
}
