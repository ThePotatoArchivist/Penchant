package archives.tater.penchant.enchantment;

import archives.tater.penchant.registry.PenchantEnchantments;
import archives.tater.penchant.util.PenchantUtil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record UnbreakableEffect(MinMaxBounds.Ints level, ItemPredicate item) {

    public UnbreakableEffect() { this(MinMaxBounds.Ints.ANY, PenchantUtil.ANY_ITEM); }
    public UnbreakableEffect(MinMaxBounds.Ints level) { this(level, PenchantUtil.ANY_ITEM); }
    public UnbreakableEffect(ItemPredicate item) { this(MinMaxBounds.Ints.ANY, item); }

    public boolean test(ItemStack stack, int level) {
        return this.level.matches(level) && item.test(stack);
    }

    public static boolean isUnbreakable(ItemStack stack) {
        var itemEnchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (var entry : itemEnchantments.entrySet())
             for (var effect : entry.getKey().value().getEffects(PenchantEnchantments.UNBREAKABLE))
                 if (effect.test(stack, entry.getIntValue()))
                     return true;
        return false;
    }

    public static final Codec<UnbreakableEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MinMaxBounds.Ints.CODEC.optionalFieldOf("level", MinMaxBounds.Ints.ANY).forGetter(UnbreakableEffect::level),
            ItemPredicate.CODEC.optionalFieldOf("item", PenchantUtil.ANY_ITEM).forGetter(UnbreakableEffect::item)
    ).apply(instance, UnbreakableEffect::new));
}
