package archives.tater.penchant;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashSet;
import java.util.Set;

public record UnlockedEnchantmentsPayload(Set<Holder<Enchantment>> unlocked) implements CustomPacketPayload {
    @Override
    public Type<? extends UnlockedEnchantmentsPayload> type() {
        return TYPE;
    }

    public static final Type<UnlockedEnchantmentsPayload> TYPE = new Type<>(Penchant.id("unlocked_enchantments"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockedEnchantmentsPayload> CODEC =
            ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT)
                    .<Set<Holder<Enchantment>>>apply(ByteBufCodecs.collection(HashSet::new))
                    .map(UnlockedEnchantmentsPayload::new, UnlockedEnchantmentsPayload::unlocked);
}
