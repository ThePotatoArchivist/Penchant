package archives.tater.inchantment;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.hash;

public class EnchantmentProgress {

    private final Object2IntOpenHashMap<Holder<@NotNull Enchantment>> progress;

    public EnchantmentProgress(Object2IntOpenHashMap<Holder<@NotNull Enchantment>> progress) {
        this.progress = progress;
    }

    public int getProgress(Holder<@NotNull Enchantment> enchantment) {
        return progress.getInt(enchantment);
    }

    public Mutable toMutable() {
        return new Mutable(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (EnchantmentProgress) obj;
        return Objects.equals(this.progress, that.progress);
    }

    @Override
    public int hashCode() {
        return hash(progress);
    }

    public static class Mutable {

        private final Object2IntOpenHashMap<Holder<@NotNull Enchantment>> progress;

        public Mutable(EnchantmentProgress progress) {
            this.progress = progress.progress.clone();
        }

        public int getProgress(Holder<@NotNull Enchantment> enchantment) {
            return progress.getInt(enchantment);
        }

        public void setProgress(Holder<@NotNull Enchantment> enchantment, int progress) {
            this.progress.put(enchantment, progress);
        }

        public void removeProgress(Holder<@NotNull Enchantment> enchantment) {
            progress.removeInt(enchantment);
        }

        public void addProgress(Holder<@NotNull Enchantment> enchantment, int progress) {
            setProgress(enchantment, getProgress(enchantment) + progress);
        }

        public EnchantmentProgress toImmutable() {
            return new EnchantmentProgress(progress);
        }

    }

    public static boolean shouldShowTooltip(Holder<@NotNull Enchantment> enchantment) {
        return enchantment.value().getMaxLevel() != 1;
    }
    public Component getTooltip(Holder<@NotNull Enchantment> enchantment, int level) {
        return (level >= enchantment.value().getMaxLevel()
                ? Component.translatable("inchantment.tooltip.progress.max")
                : Component.translatable("inchantment.tooltip.progress",
                progress.getInt(enchantment),
                getMaxProgress(enchantment, level)
        ))
                .withStyle(ChatFormatting.DARK_GRAY);
    }

    public static final Codec<EnchantmentProgress> CODEC =
            Codec.unboundedMap(Enchantment.CODEC, Codec.intRange(0, Integer.MAX_VALUE)).xmap(
                    map -> new EnchantmentProgress(new Object2IntOpenHashMap<>(map)),
                    component -> component.progress
            );

    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull EnchantmentProgress> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.map(Object2IntOpenHashMap::new, Enchantment.STREAM_CODEC, ByteBufCodecs.VAR_INT),
                    progress -> progress.progress,
                    EnchantmentProgress::new
            );

    public static final EnchantmentProgress EMPTY = new EnchantmentProgress(new Object2IntOpenHashMap<>());

    public static int getMaxProgress(Holder<@NotNull Enchantment> enchantment, int currentLevel) {
        return 50 * currentLevel;
    }

    public static void onDurabilityDamage(ItemStack stack, @Nullable LivingEntity user) {
        var enchantments = stack.getEnchantments();
        if (enchantments.isEmpty()) return;

        var newProgress = stack.getOrDefault(Inchantment.ENCHANTMENT_PROGRESS, EMPTY).toMutable();

        // Increment all enchantments
        for (var enchantment : enchantments.keySet())
            newProgress.addProgress(enchantment, 1);

        updateEnchantments(newProgress, stack.getEnchantments(), stack, user);

        stack.set(Inchantment.ENCHANTMENT_PROGRESS, newProgress.toImmutable());
    }

    public static boolean updateEnchantments(EnchantmentProgress.Mutable progress, ItemEnchantments.Mutable enchantments) {
        boolean changed = false;

        for (var enchantment : enchantments.keySet()) {
            var level = enchantments.getLevel(enchantment);

            while (true) {
                if (level >= enchantment.value().getMaxLevel()) {
                    progress.removeProgress(enchantment);
                    break;
                }

                var maxProgress = getMaxProgress(enchantment, level);

                var progressValue = progress.getProgress(enchantment);

                if (progressValue < maxProgress) break;

                level++;
                progress.setProgress(enchantment, progressValue - maxProgress);
            }

            if (level == enchantments.getLevel(enchantment)) continue;

            enchantments.set(enchantment, level);

            changed = true;
        }

        return changed;
    }

    /**
     * Levels up any necessary enchantments
     *
     * @param progress is mutated
     * @param stack    is mutated
     * @param user
     */
    public static void updateEnchantments(EnchantmentProgress.Mutable progress, ItemEnchantments enchantments, ItemStack stack, @Nullable LivingEntity user) {
        ItemEnchantments.Mutable newEnchantments = new ItemEnchantments.Mutable(enchantments);

        if (updateEnchantments(progress, newEnchantments)) {
            stack.set(DataComponents.ENCHANTMENTS, newEnchantments.toImmutable());
            if (user != null)
                user.level().playSound(null, user, SoundEvents.ENCHANTMENT_TABLE_USE, user.getSoundSource(), 1, user.getRandom().nextFloat() * 0.1F + 0.9F);
        }
    }
}
