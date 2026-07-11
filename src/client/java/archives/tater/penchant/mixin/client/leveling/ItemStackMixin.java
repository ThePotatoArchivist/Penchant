package archives.tater.penchant.mixin.client.leveling;

import archives.tater.penchant.PenchantClient;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @WrapOperation(
            method = "addToTooltip",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/TooltipProvider;addToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;Lnet/minecraft/core/component/DataComponentGetter;)V")
    )
    private void setTooltipItem(TooltipProvider instance, Item.TooltipContext tooltipContext, Consumer<Component> componentConsumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter, Operation<Void> original) {
        ScopedValue.where(PenchantClient.TOOLTIP_ITEM,  (ItemStack) (Object) this).call(() -> original.call(instance, tooltipContext, componentConsumer, tooltipFlag, dataComponentGetter));
    }
}
