package archives.tater.penchant.mixin.disable;

import archives.tater.penchant.PenchantEnchantmentTags;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

import static net.minecraft.Util.getRandomSafe;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public class EnchantBookForEmeraldsMixin {
    // I though it was better to use a Redirect here than mixin to HolderSet#getRandomElement which is presumably used in many places
    @SuppressWarnings("unchecked")
    @Redirect(
            method = "getOffer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;getRandomElementOf(Lnet/minecraft/tags/TagKey;Lnet/minecraft/util/RandomSource;)Ljava/util/Optional;")
    )
    private <T> Optional<Holder<T>> disableEnchantment(Registry<T> instance, TagKey<T> tagKey, RandomSource randomSource) {
        return instance.get(tagKey).flatMap(list -> getRandomSafe(list.stream()
                .filter(enchantment -> !((Holder<Enchantment>) enchantment).is(PenchantEnchantmentTags.DISABLED))
                .toList(),
                randomSource
        ));
    }
}
