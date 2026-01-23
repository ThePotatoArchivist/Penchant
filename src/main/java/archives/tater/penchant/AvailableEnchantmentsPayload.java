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

public record AvailableEnchantmentsPayload(Set<Holder<Enchantment>> available) implements CustomPacketPayload {
    @Override
    public Type<? extends AvailableEnchantmentsPayload> type() {
        return TYPE;
    }

    public static final Type<AvailableEnchantmentsPayload> TYPE = new Type<>(Penchant.id("available_enchantments"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AvailableEnchantmentsPayload> CODEC =
            ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT)
                    .<Set<Holder<Enchantment>>>apply(ByteBufCodecs.collection(HashSet::new))
                    .map(AvailableEnchantmentsPayload::new, AvailableEnchantmentsPayload::available);
}
