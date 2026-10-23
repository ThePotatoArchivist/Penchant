package archives.tater.penchant.mixin.leveling;

import archives.tater.penchant.component.EnchantmentProgress;
import archives.tater.penchant.registry.PenchantItemTags;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @WrapOperation(
            method = "useItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;")
    )
    private InteractionResult levelUpUse(ItemStack instance, Level level, Player player, InteractionHand hand, Operation<InteractionResult> original) {
        var result = original.call(instance, level, player, hand);
        if (!instance.is(PenchantItemTags.LEVEL_UP_BY_USE) || !result.consumesAction()) return result;
        EnchantmentProgress.addToProgress(instance, 1, player);
        return result;
    }

    @WrapOperation(
            method = "useItemOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;")
    )
    private InteractionResult levelUpUse(ItemStack instance, UseOnContext context, Operation<InteractionResult> original) {
        var result = original.call(instance, context);
        if (!instance.is(PenchantItemTags.LEVEL_UP_BY_USE) || !result.consumesAction()) return result;
        EnchantmentProgress.addToProgress(instance, 1, context.getPlayer());
        return result;
    }
}
