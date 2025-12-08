package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.EnchantmentProgress;
import archives.tater.penchant.PenchantEnchantmentTags;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(at = @At("HEAD"), method = "processDurabilityChange")
	private void updateProgress(int i, ServerLevel serverLevel, ServerPlayer serverPlayer, CallbackInfoReturnable<Integer> cir) {
        EnchantmentProgress.onDurabilityDamage((ItemStack) (Object) this, serverPlayer);
	}

    @WrapOperation(
            method = "method_57356",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/ItemEnchantments$Mutable;upgrade(Lnet/minecraft/core/Holder;I)V")
    )
    private static void levelOne(Mutable instance, Holder<Enchantment> enchantment, int level, Operation<Void> original) {
        if (enchantment.is(PenchantEnchantmentTags.NO_LEVELING))
            original.call(instance, enchantment, level);
        else
            instance.set(enchantment, 1);
    }

//    @ModifyExpressionValue(
//            method = "isEnchantable",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/ItemEnchantments;isEmpty()Z")
//    )
//    private boolean allowReenchant(boolean original) {
//        return true;
//    }
}
