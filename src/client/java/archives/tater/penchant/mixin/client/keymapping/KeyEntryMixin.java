package archives.tater.penchant.mixin.client.keymapping;

import archives.tater.penchant.client.ScreenKeyMapping;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;

@Mixin(KeyBindsList.KeyEntry.class)
public class KeyEntryMixin {
    @WrapOperation(
            method = "refreshEntry",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;same(Lnet/minecraft/client/KeyMapping;)Z")
    )
    private boolean preventDuplicateScreenKeybind(KeyMapping instance, KeyMapping binding, Operation<Boolean> original) {
        return original.call(instance, binding) && (instance instanceof ScreenKeyMapping == binding instanceof ScreenKeyMapping);
    }
}
