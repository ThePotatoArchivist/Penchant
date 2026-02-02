package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.component.EnchantmentProgress;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(at = @At("HEAD"), method = "processDurabilityChange")
	private void updateProgress(int i, ServerLevel serverLevel, ServerPlayer serverPlayer, CallbackInfoReturnable<Integer> cir) {
        EnchantmentProgress.onDurabilityDamage((ItemStack) (Object) this, serverPlayer);
	}
}
