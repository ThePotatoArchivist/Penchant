package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.component.EnchantmentProgress;
import archives.tater.penchant.registry.PenchantItemTags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemInstance {
    @Inject(
            at = @At("HEAD"),
            method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/server/level/ServerPlayer;Ljava/util/function/Consumer;)V"
    )
	private void updateProgress(int amount, ServerLevel level, @Nullable ServerPlayer player, Consumer<Item> onBreak, CallbackInfo ci) {
        EnchantmentProgress.onDurabilityDamage((ItemStack) (Object) this, player);
	}

    @Inject(
            at = @At("HEAD"),
            method = "hurtWithoutBreaking"
    )
    private void updateProgress(int amount, Player player, CallbackInfo ci) {
        if (player instanceof ServerPlayer)
            EnchantmentProgress.onDurabilityDamage((ItemStack) (Object) this, player);
    }

    @ModifyVariable(
            at = @At("HEAD"),
            method = "enchant",
            argsOnly = true,
            name = "level"
    )
    private int forceLevel(int level, Holder<Enchantment> enchantment) {
        if (EnchantmentHelper.getComponentType((ItemStack) (Object) this) == DataComponents.STORED_ENCHANTMENTS)
            return 1;
        if (is(PenchantItemTags.MAX_LEVEL_ENCHANTMENTS))
            return enchantment.value().getMaxLevel();

        return level;
    }
}
