package archives.tater.penchant.mixin.randomenchantment;

import archives.tater.penchant.component.RandomEnchantment;
import archives.tater.penchant.registry.PenchantComponents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {
    @Inject(
            method = "onCraftedBy",
            at = @At("TAIL")
    )
    private void resolveEnchantment(Player player, int craftCount, CallbackInfo ci) {
        if (player.level() instanceof ServerLevel serverLevel && has(PenchantComponents.RANDOM_ENCHANTMENT))
            RandomEnchantment.resolve((ItemStack) (Object) this, serverLevel);
    }

    @Inject(
            method = "onCraftedBySystem",
            at = @At("TAIL")
    )
    private void resolveEnchantment(Level level, CallbackInfo ci) {
        if (level instanceof ServerLevel serverLevel && has(PenchantComponents.RANDOM_ENCHANTMENT))
            RandomEnchantment.resolve((ItemStack) (Object) this, serverLevel);
    }
}
