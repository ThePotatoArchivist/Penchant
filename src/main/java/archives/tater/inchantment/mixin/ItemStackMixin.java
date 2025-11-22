package archives.tater.inchantment.mixin;

import archives.tater.inchantment.EnchantmentProgress;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(at = @At("HEAD"), method = "processDurabilityChange")
	private void init(int i, ServerLevel serverLevel, ServerPlayer serverPlayer, CallbackInfoReturnable<Integer> cir) {
        EnchantmentProgress.onDurabilityDamage((ItemStack) (Object) this, serverPlayer);
	}
}