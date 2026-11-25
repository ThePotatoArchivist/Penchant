package archives.tater.penchant.mixin.client.leveling;

import archives.tater.penchant.registry.PenchantEnchantmentTags;
import archives.tater.penchant.util.PenchantmentHelper;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(EnchantmentScreen.class)
public class EnchantmentScreenMixin {
    @WrapOperation(
            method = "getClue",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;")
    )
    private Component noLevel(Holder<Enchantment> enchantment, int level, Operation<Component> original) {
        return enchantment.is(PenchantEnchantmentTags.NO_LEVELING) ? original.call(enchantment, level) : PenchantmentHelper.getName(enchantment);
    }
}
