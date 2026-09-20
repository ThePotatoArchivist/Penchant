package archives.tater.penchant.mixin.bookshelf;

import archives.tater.penchant.registry.PenchantFlag;
import archives.tater.penchant.util.PenchantmentHelper;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.objectweb.asm.Opcodes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;

import java.util.List;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
    @ModifyReturnValue(
            method = "isValidBookShelf",
            at = @At("RETURN")
    )
    private static boolean checkChiseled(boolean original, Level level, BlockPos pos, BlockPos offset) {
        return original && PenchantmentHelper.getBookCount(level.getBlockState(pos.offset(offset))) > 0;
    }

    @ModifyExpressionValue(
            method = "animateTick",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/EnchantingTableBlock;BOOKSHELF_OFFSETS:Ljava/util/List;", opcode = Opcodes.GETSTATIC)
    )
    private List<BlockPos> lenientBookshelfPlacement(List<BlockPos> original) {
        return PenchantmentHelper.getBookshelfOffsets(original);
    }

    @Definition(id = "is", method = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z")
    @Definition(id = "ENCHANTMENT_POWER_TRANSMITTER", field = "Lnet/minecraft/tags/BlockTags;ENCHANTMENT_POWER_TRANSMITTER:Lnet/minecraft/tags/TagKey;")
    @Expression("?.is(ENCHANTMENT_POWER_TRANSMITTER)")
    @ModifyExpressionValue(
            method = "isValidBookShelf",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static boolean allowObstruction(boolean original) {
        return original || PenchantFlag.LENIENT_BOOKSHELF_PLACEMENT.isEnabled();
    }
}
