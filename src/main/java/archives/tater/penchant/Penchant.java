package archives.tater.penchant;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.enchantment.Enchantment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Penchant implements ModInitializer {
	public static final String MOD_ID = "penchant";

    public static Identifier id(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier id(String path) {
        return id(MOD_ID, path);
    }

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier DURABILITY_REWORK = Penchant.id("durability_rework");
    public static final Identifier TABLE_REWORK = Penchant.id("table_rework");

    private static <T> DataComponentType<T> registerComponent(String path, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, boolean cache, boolean ignoreSwapAnimation) {
        var type = DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec);
        if (cache) type.cacheEncoding();
        if (ignoreSwapAnimation) type.ignoreSwapAnimation();
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id(path), type.build());
    }

    private static <T> DataComponentType<T> registerEnchantmentEffect(String path, Codec<T> codec) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, id(path), DataComponentType.<T>builder().persistent(codec).build());
    }

    public static final MenuType<PenchantmentMenu> PENCHANTMENT_MENU = Registry.register(
            BuiltInRegistries.MENU,
            id("penchantment"),
            new MenuType<>(PenchantmentMenu::new, FeatureFlags.VANILLA_SET) // TODO feature flags
    );

    public static final DataComponentType<EnchantmentProgress> ENCHANTMENT_PROGRESS = registerComponent(
            "enchantment_progress",
            EnchantmentProgress.CODEC,
            EnchantmentProgress.STREAM_CODEC,
            true,
            true
    );

    public static final DataComponentType<List<UnbreakableEffect>> UNBREAKABLE = registerEnchantmentEffect("unbreakable", UnbreakableEffect.CODEC.listOf());

    public static Component getName(Holder<Enchantment> enchantment) {
        return ComponentUtils.mergeStyles(
                enchantment.value().description().copy(),
                Style.EMPTY.withColor(enchantment.is(EnchantmentTags.CURSE)
                        ? ChatFormatting.RED
                        : ChatFormatting.GRAY
                )
        );
    }

    public static int getBookCost(Holder<Enchantment> enchantment) {
        return enchantment.value().getMinCost(1);
    }

    public static int getXpCost(Holder<Enchantment> enchantment) {
        return enchantment.value().getAnvilCost();
    }

    private void registerPack(Identifier id) {
        ResourceLoader.registerBuiltinPack(
                id,
                FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(),
                Component.translatable(id.toLanguageKey("dataPack", "name")),
                PackActivationType.DEFAULT_ENABLED
        );
    }

	@Override
	public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(UnlockedEnchantmentsPayload.TYPE, UnlockedEnchantmentsPayload.CODEC);

        registerPack(DURABILITY_REWORK);
        registerPack(TABLE_REWORK);
	}
}
