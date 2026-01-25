package archives.tater.penchant.util;

import archives.tater.penchant.registry.PenchantFlag;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.EnchantingTableBlock;

import java.util.List;
import java.util.function.Consumer;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class PenchantmentHelper {
    private PenchantmentHelper() {}

    public static List<BlockPos> LENIENT_BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-3, -2, -3, 3, 2, 3)
            .filter(blockPos -> abs(blockPos.getX()) >= 2 || abs(blockPos.getZ()) >= 2 || blockPos.getY() >= 2 || blockPos.getY() <= -1)
            .map(BlockPos::immutable)
            .toList();

    public static Component getName(Holder<Enchantment> enchantment) {
        return ComponentUtils.mergeStyles(
                enchantment.value().description().copy(),
                Style.EMPTY.withColor(enchantment.is(EnchantmentTags.CURSE)
                        ? ChatFormatting.RED
                        : ChatFormatting.GRAY
                )
        );
    }

    public static int getBookRequirement(Holder<Enchantment> enchantment) {
        return max(2 * enchantment.value().getMinCost(1) - 5, 0);
    }

    public static int getXpLevelCost(Holder<Enchantment> enchantment) {
        return enchantment.value().getAnvilCost();
    }

    public static boolean canEnchantItem(ItemStack stack, Holder<Enchantment> enchantment) {
        return stack.is(Items.BOOK) || stack.is(Items.ENCHANTED_BOOK) || stack.canBeEnchantedWith(enchantment, EnchantingContext.ACCEPTABLE);
    }

    public static ItemEnchantments getEnchantments(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentsForCrafting(stack);
    }

    public static boolean hasEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return getEnchantments(stack).getLevel(enchantment) > 0;
    }

    public static boolean canEnchant(ItemStack stack, Holder<Enchantment> enchantment) {
        return canEnchantItem(stack, enchantment) && !hasEnchantment(stack, enchantment) && EnchantmentHelper.isEnchantmentCompatible(getEnchantments(stack).keySet(), enchantment);
    }

    public static ItemStack updateEnchantments(ItemStack stack, Consumer<ItemEnchantments.Mutable> updater) {
        var type = stack.is(Items.BOOK) ? DataComponents.STORED_ENCHANTMENTS : EnchantmentHelper.getComponentType(stack);
        var enchantments = stack.getOrDefault(type, ItemEnchantments.EMPTY);
        var mutable = new ItemEnchantments.Mutable(enchantments);
        updater.accept(mutable);
        var newEnchantments = mutable.toImmutable();
        stack.set(type, newEnchantments);
        if (newEnchantments.isEmpty()) {
            if (stack.is(Items.ENCHANTED_BOOK))
                return stack.transmuteCopy(Items.BOOK);
        } else {
            if (stack.is(Items.BOOK))
                return stack.transmuteCopy(Items.ENCHANTED_BOOK);
        }
        return stack;
    }

    public static List<BlockPos> getBookshelfOffsets(List<BlockPos> original) {
        return PenchantFlag.LENIENT_BOOKSHELF_PLACEMENT.isEnabled() ? LENIENT_BOOKSHELF_OFFSETS : original;
    }

    public static List<BlockPos> getBookshelfOffsets() {
        return getBookshelfOffsets(EnchantingTableBlock.BOOKSHELF_OFFSETS);
    }
}
