package archives.tater.penchant.component;

import archives.tater.penchant.registry.PenchantComponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record RandomEnchantment(Optional<HolderSet<Enchantment>> options, boolean onlyCompatible) implements TooltipProvider {

    public static final MapCodec<RandomEnchantment> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("options").forGetter(RandomEnchantment::options),
            Codec.BOOL.optionalFieldOf("only_compatible", true).forGetter(RandomEnchantment::onlyCompatible)
    ).apply(instance, RandomEnchantment::new));

    public static final Codec<RandomEnchantment> CODEC = MAP_CODEC.codec();

    public static final StreamCodec<RegistryFriendlyByteBuf, RandomEnchantment> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.ENCHANTMENT)), RandomEnchantment::options,
            ByteBufCodecs.BOOL, RandomEnchantment::onlyCompatible,
            RandomEnchantment::new
    );

    public static ItemStack resolve(ItemStack stack, ServerLevel level) {
        var randomEnchantment = stack.remove(PenchantComponents.RANDOM_ENCHANTMENT);
        if (randomEnchantment == null) return stack;
        var builder = EnchantRandomlyFunction.randomEnchantment()
                .withOptions(randomEnchantment.options);
        if (!randomEnchantment.onlyCompatible) builder.allowingIncompatibleEnchantments();
        var context = new LootContext.Builder(new LootParams.Builder(level).create(LootContextParamSets.EMPTY)).create(Optional.empty());
        return builder.build().apply(stack, context);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("penchant.tooltip.random_enchantment").withStyle(ChatFormatting.GRAY));
    }

    public static class LootFunction extends LootItemConditionalFunction {

        public static final MapCodec<LootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(
                RandomEnchantment.MAP_CODEC.forGetter(function -> function.randomEnchantment)
        ).apply(instance, LootFunction::new));

        private final RandomEnchantment randomEnchantment;

        public LootFunction(RandomEnchantment randomEnchantment) {
            this(List.of(), randomEnchantment);
        }

        public LootFunction(List<LootItemCondition> predicates, RandomEnchantment randomEnchantment) {
            super(predicates);
            this.randomEnchantment = randomEnchantment;
        }

        @Override
        public MapCodec<? extends LootFunction> codec() {
            return CODEC;
        }

        @Override
        protected ItemStack run(ItemStack itemStack, LootContext context) {
            itemStack.set(PenchantComponents.RANDOM_ENCHANTMENT, randomEnchantment);
            return itemStack;
        }
    }
}
