package archives.tater.inchantment.mixin.client;

import archives.tater.inchantment.EnchantmentProgress;
import archives.tater.inchantment.Inchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@Mixin(ItemEnchantments.class)
public class ItemEnchantmentsMixin {
    @Inject(
            method = "addToTooltip",
            at = @At("HEAD")
    )
    private void getProgress(TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter, CallbackInfo ci, @Share("progress") LocalRef<EnchantmentProgress> progress, @Share("isStored") LocalBooleanRef isStored) {
        progress.set(dataComponentGetter.getOrDefault(Inchantment.ENCHANTMENT_PROGRESS, EnchantmentProgress.EMPTY));
        isStored.set(dataComponentGetter.get(DataComponents.STORED_ENCHANTMENTS) != null);
    }

    @WrapOperation(
            method = "addToTooltip",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;")
    )
    private Component addProgress(Holder<@NotNull Enchantment> holder, int i, Operation<Component> original, @Share("progress") LocalRef<EnchantmentProgress> progress, @Share("isStored") LocalBooleanRef isStored) {
        if (isStored.get()) {
            return Inchantment.getName(holder);
        }

        var originalResult = original.call(holder, i);

        if (!EnchantmentProgress.shouldShowTooltip(holder)) return originalResult;

        return originalResult.copy().append(" ").append(progress.get().getTooltip(holder, i));
    }
}
