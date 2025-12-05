package archives.tater.penchant.mixin.disable;

import archives.tater.penchant.PenchantEnchantmentTags;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(
            method = "enchant",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableEnchantment(Holder<Enchantment> enchantment, int level, CallbackInfo ci) {
        if (enchantment.is(PenchantEnchantmentTags.DISABLED))
            ci.cancel();
    }
}
