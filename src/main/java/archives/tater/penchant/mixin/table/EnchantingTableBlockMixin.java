package archives.tater.penchant.mixin.table;

import archives.tater.penchant.menu.PenchantmentMenu;
import archives.tater.penchant.registry.PenchantFeatureFlags;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
    @ModifyReturnValue(
            method = "method_17467",
            at = @At("RETURN")
    )
    private static AbstractContainerMenu replaceMenu(AbstractContainerMenu original, @Local(argsOnly = true) int syncId, @Local(argsOnly = true) Inventory inventory, @Local(argsOnly = true) Level level, @Local(argsOnly = true) BlockPos pos) {
        return level.enabledFeatures().contains(PenchantFeatureFlags.INSTANCE.reworkEnchantingTable)
                ? new PenchantmentMenu(syncId, inventory, ContainerLevelAccess.create(level, pos))
                : original;
    }

    @Inject(
            method = "useWithoutItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;", shift = At.Shift.AFTER)
    )
    private void sendEnchantments(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(player.containerMenu instanceof PenchantmentMenu penchantmentMenu)) return;
        penchantmentMenu.sendEnchantments();
    }
}
