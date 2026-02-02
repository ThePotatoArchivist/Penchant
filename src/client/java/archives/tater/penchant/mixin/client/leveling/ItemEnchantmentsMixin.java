package archives.tater.penchant.mixin.client.leveling;

import archives.tater.penchant.*;
import archives.tater.penchant.component.EnchantmentProgress;
import archives.tater.penchant.registry.PenchantComponents;
import archives.tater.penchant.registry.PenchantEnchantmentTags;
import archives.tater.penchant.util.PenchantmentHelper;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@Mixin(ItemEnchantments.class)
public class ItemEnchantmentsMixin {
    @Shadow
    @Final
    Object2IntOpenHashMap<Holder<Enchantment>> enchantments;

    @Inject(
            method = "addToTooltip",
            at = @At("HEAD")
    )
    private void getProgress(TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter, CallbackInfo ci, @Share("progress") LocalRef<EnchantmentProgress> progress) {
        if (dataComponentGetter.get(DataComponents.STORED_ENCHANTMENTS) == null)
            progress.set(dataComponentGetter.getOrDefault(PenchantComponents.ENCHANTMENT_PROGRESS, EnchantmentProgress.EMPTY));
    }

    @WrapOperation(
            method = "addToTooltip",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getFullname(Lnet/minecraft/core/Holder;I)Lnet/minecraft/network/chat/Component;")
    )
    private Component hideLevel(Holder<Enchantment> holder, int i, Operation<Component> original, @Share("progress") LocalRef<@Nullable EnchantmentProgress> progress, @Share("enchantmentShare") LocalRef<@Nullable Holder<Enchantment>> enchantmentShare, @Share("level") LocalIntRef level) {
        if (holder.is(PenchantEnchantmentTags.NO_LEVELING)) return original.call(holder, i);
        if (progress.get() == null) return PenchantmentHelper.getName(holder);
        enchantmentShare.set(holder);
        level.set(i);
        return original.call(holder, i);
    }

    @WrapOperation(
            method = "addToTooltip",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V")
    )
    private <T> void addProgress(Consumer<T> instance, T text, Operation<Void> original, @Share("enchantmentShare") LocalRef<@Nullable Holder<Enchantment>> enchantmentShare, @Share("level") LocalIntRef level, @Share("progress") LocalRef<@Nullable EnchantmentProgress> progress, @Local(argsOnly = true) DataComponentGetter components) {
        original.call(instance, text);

        var enchantment = enchantmentShare.get();
        if (enchantment == null) return;
        if (!PenchantClient.SHOW_PROGRESS_KEYBIND.isDownAnywhere()) return;
        if (!EnchantmentProgress.shouldShowTooltip(enchantment)) return;

        original.call(instance, PenchantClient.getProgressTooltip(
                progress.get(),
                enchantment,
                level.get(),
                components instanceof ItemStack stack ? stack.getMaxDamage() : components.getOrDefault(DataComponents.MAX_DAMAGE, 0)
        ));
    }

    @Inject(
            method = "addToTooltip",
            at = @At("TAIL")
    )
    private void addHint(TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag flag, DataComponentGetter componentGetter, CallbackInfo ci, @Share("progress") LocalRef<@Nullable EnchantmentProgress> progress) {
        if (progress.get() == null || enchantments.isEmpty() || PenchantClient.SHOW_PROGRESS_KEYBIND.isDownAnywhere()) return;

        tooltipAdder.accept(PenchantClient.getProgressKeyHint());
    }
}
