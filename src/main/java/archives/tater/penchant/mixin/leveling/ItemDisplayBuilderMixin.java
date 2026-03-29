package archives.tater.penchant.mixin.leveling;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(targets = "net.minecraft.world.item.CreativeModeTab$ItemDisplayBuilder")
public class ItemDisplayBuilderMixin {
    @Inject(
            method = "accept",
            at = @At(value = "NEW", target = "(Ljava/lang/String;)Ljava/lang/IllegalStateException;"),
            cancellable = true
    )
    private void ignoreDuplicateBook(ItemStack stack, CreativeModeTab.TabVisibility tabVisibility, CallbackInfo ci) {
        if (stack.is(Items.ENCHANTED_BOOK))
            ci.cancel();
    }
}
