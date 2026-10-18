package archives.tater.penchant.mixin.compat.modestmagic;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.baisylia.modestmagic.block.entity.custom.AltarBlockEntity;

@Mixin(AltarBlockEntity.class)
public class AltarBlockEntityMixin {
    @ModifyExpressionValue(
            method = "tryCraft",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I")
    )
    private int levelOne(int original) {
        return 1;
    }
}
