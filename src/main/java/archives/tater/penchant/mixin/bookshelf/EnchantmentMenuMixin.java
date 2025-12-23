package archives.tater.penchant.mixin.bookshelf;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

@Mixin(EnchantmentMenu.class)
@Debug(export = true)
public class EnchantmentMenuMixin {
    @WrapOperation(
            method = "method_17411",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/EnchantingTableBlock;isValidBookShelf(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean getBookCount(Level level, BlockPos enchantingTablePos, BlockPos bookshelfPos, Operation<Boolean> original, @Share("chiseledBookCount") LocalIntRef chiseledBookCount, @Share("isChiseledBookshelf") LocalBooleanRef isChiseledBookshelf) {
        isChiseledBookshelf.set(false);
        if (!original.call(level, enchantingTablePos, bookshelfPos)) return false;
        var entity = level.getBlockEntity(enchantingTablePos.offset(bookshelfPos));
        if (!(entity instanceof ChiseledBookShelfBlockEntity bookshelf)) return true;

        isChiseledBookshelf.set(true);
        chiseledBookCount.set(chiseledBookCount.get() + bookshelf.count());
        return true;
    }

    @Definition(id = "i", local = @Local(type = int.class, ordinal = 0))
    @Expression("i = i + @(1)")
    @ModifyExpressionValue(
            method = "method_17411",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private int modifyCount(int original, @Share("chiseledBookCount") LocalIntRef chiseledBookCount, @Share("isChiseledBookshelf") LocalBooleanRef isChiseledBookshelf) {
        if (!isChiseledBookshelf.get()) return original;
        var scoreIncrease = chiseledBookCount.get() / 3;
        chiseledBookCount.set(chiseledBookCount.get() % 3);
        return scoreIncrease;
    }
}
