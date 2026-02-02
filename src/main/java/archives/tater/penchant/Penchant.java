package archives.tater.penchant;

import archives.tater.penchant.loot.LootModification;
import archives.tater.penchant.menu.PenchantmentMenu;
import archives.tater.penchant.network.EnchantPayload;
import archives.tater.penchant.network.UnlockedEnchantmentsPayload;
import archives.tater.penchant.registry.*;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static final Identifier BOOKSHELF_PLACEMENT = Penchant.id("bookshelf_placement");
    public static final Identifier TABLE_REWORK = Penchant.id("table_rework");
    public static final Identifier NO_ANVIL_BOOKS = Penchant.id("no_anvil_books");
    public static final Identifier LOOT_REWORK = Penchant.id("loot_rework");
    public static final Identifier GUARANTEED_DROPS = Penchant.id("guaranteed_drops");
    public static final Identifier REDUCED_CURSES = Penchant.id("reduced_curses");

    private void registerPack(Identifier id) {
        registerPack(id, PackActivationType.DEFAULT_ENABLED);
    }

    private void registerPack(Identifier id, PackActivationType activationType) {
        ResourceLoader.registerBuiltinPack(
                id,
                FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(),
                Component.translatable(id.toLanguageKey("dataPack", "name")),
                activationType
        );
    }

	@Override
	public void onInitialize() {
        PenchantRegistries.init();
        PenchantFlag.init();
        PenchantComponents.init();
        PenchantEnchantments.init();
        PenchantMenus.init();
        PenchantAdvancements.init();
        LootModification.register();

        PayloadTypeRegistry.playS2C().register(UnlockedEnchantmentsPayload.TYPE, UnlockedEnchantmentsPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(EnchantPayload.TYPE, EnchantPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(EnchantPayload.TYPE, (payload, context) -> {
            if (!(context.player().containerMenu instanceof PenchantmentMenu menu)) {
                LOGGER.warn("Received enchant payload but enchantment menu was not open");
                return;
            }
            menu.handleEnchant(payload.enchantment());
        });

        registerPack(DURABILITY_REWORK);
        registerPack(BOOKSHELF_PLACEMENT);
        registerPack(TABLE_REWORK);
        registerPack(NO_ANVIL_BOOKS);
        registerPack(LOOT_REWORK);
        registerPack(GUARANTEED_DROPS);
        registerPack(REDUCED_CURSES, PackActivationType.NORMAL);
	}
}
