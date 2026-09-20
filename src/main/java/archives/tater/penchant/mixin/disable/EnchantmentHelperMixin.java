package archives.tater.penchant.mixin.disable;

import archives.tater.penchant.registry.PenchantEnchantmentTags;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyReceiver(
            method = "getAvailableEnchantmentResults",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V")
    )
    private static Stream<Holder<Enchantment>> disableEnchantment(Stream<Holder<Enchantment>> instance, Consumer<? super Holder<Enchantment>> consumer) {
        return instance.filter(enchantment -> !enchantment.is(PenchantEnchantmentTags.DISABLED));
    }
}
