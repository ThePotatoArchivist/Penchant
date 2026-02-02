package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.component.EnchantmentProgress;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;

import org.jspecify.annotations.Nullable;

@Mixin(EnchantRandomlyFunction.class)
public class EnchantRandomlyFunctionMixin {
    @Unique
    private static final ThreadLocal<HolderLookup.@Nullable Provider> registriesLocal = new ThreadLocal<>();

    @Inject(
            method = "run",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/functions/EnchantRandomlyFunction;enchantItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Holder;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/item/ItemStack;")
    )
    private void saveRegistries(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        if (context.getResolver() instanceof HolderLookup.Provider provider)
            registriesLocal.set(provider);
    }

    @ModifyReturnValue(
            method = "enchantItem",
            at = @At("RETURN")
    )
    private static ItemStack addProgress(ItemStack original, @Local(argsOnly = true) RandomSource random) {
        if (EnchantmentHelper.getComponentType(original) == DataComponents.STORED_ENCHANTMENTS) return original;
        var registries = registriesLocal.get();
        if (registries == null) return original;

        EnchantmentProgress.addRandomProgress(original, registries, random);

        return original;
    }
}
