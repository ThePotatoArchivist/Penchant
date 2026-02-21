package archives.tater.penchant.mixin.table;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

@Mixin(ChiseledBookShelfBlockEntity.class)
public interface ChiseledBookshelfBlockEntityAccessor {
    @Accessor
    NonNullList<ItemStack> getItems();
}
