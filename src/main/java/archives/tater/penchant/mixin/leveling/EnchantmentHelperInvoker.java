package archives.tater.penchant.mixin.leveling;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

@Mixin(EnchantmentHelper.class)
public interface EnchantmentHelperInvoker {
    @Invoker
    static DataComponentType<ItemEnchantments> invokeGetComponentType(ItemStack stack) {
        throw new AssertionError();
    }
}
