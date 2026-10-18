package archives.tater.penchant.mixin.compat.modestmagic;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.baisylia.modestmagic.recipe.custom.TabletSmithingRecipe;

@Mixin(TabletSmithingRecipe.class)
public class TabletSmithingRecipeMixin {
    @ModifyExpressionValue(
            method = "assemble(Lnet/minecraft/world/item/crafting/SmithingRecipeInput;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I")
    )
    private int levelOne(int original) {
        return 1;
    }
}
