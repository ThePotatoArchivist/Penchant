package archives.tater.penchant;

import archives.tater.penchant.loot.LootModification;
import archives.tater.penchant.menu.PenchantmentMenu;
import archives.tater.penchant.network.EnchantPayload;
import archives.tater.penchant.network.UnlockedEnchantmentsPayload;
import archives.tater.penchant.registry.*;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Penchant implements ModInitializer {
	public static final String MOD_ID = "penchant";

    public static ResourceLocation id(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation id(String path) {
        return id(MOD_ID, path);
    }

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final ResourceLocation DURABILITY_REWORK = Penchant.id("durability_rework");
    public static final ResourceLocation BOOKSHELF_PLACEMENT = Penchant.id("bookshelf_placement");
    public static final ResourceLocation TABLE_REWORK = Penchant.id("table_rework");
    public static final ResourceLocation NO_ANVIL_BOOKS = Penchant.id("no_anvil_books");
    public static final ResourceLocation LOOT_REWORK = Penchant.id("loot_rework");
    public static final ResourceLocation GUARANTEED_DROPS = Penchant.id("guaranteed_drops");
    public static final ResourceLocation REDUCED_CURSES = Penchant.id("reduced_curses");

    private void registerPack(ResourceLocation id) {
        registerPack(id, ResourcePackActivationType.DEFAULT_ENABLED);
    }

    private void registerPack(ResourceLocation id, ResourcePackActivationType activationType) {
        ResourceManagerHelper.registerBuiltinResourcePack(
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

        ServerLifecycleEvents.SERVER_STARTED.register(server ->
            PenchantmentDefinition.buildCache(server.registryAccess())
        );

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
        registerPack(REDUCED_CURSES, ResourcePackActivationType.NORMAL);
	}
}
