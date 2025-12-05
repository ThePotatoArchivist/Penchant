package archives.tater.penchant.mixin.disable;

import archives.tater.penchant.PenchantEnchantmentTags;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SingleEnchantment.class)
public class SingleEnchantmentMixin {
    @Shadow
    @Final
    private Holder<Enchantment> enchantment;

    @Inject(
            method = "enchant",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableEnchantment(ItemStack stack, ItemEnchantments.Mutable enchantments, RandomSource random, DifficultyInstance difficulty, CallbackInfo ci) {
        if (enchantment.is(PenchantEnchantmentTags.DISABLED))
            ci.cancel();
    }
}
