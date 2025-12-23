package archives.tater.penchant.mixin.bookshelf;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {
    @Definition(id = "is", method = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z")
    @Definition(id = "getBlockState", method = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    @Definition(id = "offset", method = "Lnet/minecraft/core/BlockPos;offset(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos;")
    @Expression("?.getBlockState(?.offset(?)).is(?)")
    @WrapOperation(
            method = "isValidBookShelf",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static boolean checkChiseled(BlockState instance, TagKey<Block> tagKey, Operation<Boolean> original) {
        if (!original.call(instance, tagKey)) return false;
        if (!instance.hasProperty(ChiseledBookShelfBlock.SLOT_0_OCCUPIED)) return true;
        for (var property : ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES) {
            if (instance.getValue(property)) return true;
        }
        return false;
    }
}
