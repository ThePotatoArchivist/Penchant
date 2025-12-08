package archives.tater.penchant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.Optional;

public record UnbreakableEffect(MinMaxBounds.Ints level, ItemPredicate item) {

    public UnbreakableEffect() { this(MinMaxBounds.Ints.ANY, ANY_ITEM); }
    public UnbreakableEffect(MinMaxBounds.Ints level) { this(level, ANY_ITEM); }
    public UnbreakableEffect(ItemPredicate item) { this(MinMaxBounds.Ints.ANY, item); }

    public boolean test(ItemStack stack, int level) {
        return this.level.matches(level) && item.test(stack);
    }

    public static boolean isUnbreakable(ItemStack stack) {
        var itemEnchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        for (var entry : itemEnchantments.entrySet())
             for (var effect : entry.getKey().value().getEffects(Penchant.UNBREAKABLE))
                 if (effect.test(stack, entry.getIntValue()))
                     return true;
        return false;
    }

    public static final ItemPredicate ANY_ITEM = new ItemPredicate(Optional.empty(), MinMaxBounds.Ints.ANY, DataComponentMatchers.ANY);

    public static final Codec<UnbreakableEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MinMaxBounds.Ints.CODEC.optionalFieldOf("level", MinMaxBounds.Ints.ANY).forGetter(UnbreakableEffect::level),
            ItemPredicate.CODEC.optionalFieldOf("item", ANY_ITEM).forGetter(UnbreakableEffect::item)
    ).apply(instance, UnbreakableEffect::new));
}
