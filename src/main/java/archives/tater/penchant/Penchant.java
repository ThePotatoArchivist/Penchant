package archives.tater.penchant;

import archives.tater.penchant.api.CanEnchantCallback;
import archives.tater.penchant.loot.LootModification;
import archives.tater.penchant.menu.PenchantmentMenu;
import archives.tater.penchant.network.EnchantPayload;
import archives.tater.penchant.network.UnlockedEnchantmentsPayload;
import archives.tater.penchant.registry.*;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.util.TriState;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

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

    @Override
	public void onInitialize() {
        PenchantRegistries.init();
        PenchantFlag.init();
        PenchantComponents.init();
        PenchantEnchantments.init();
        PenchantMenus.init();
        PenchantAdvancements.init();
        PenchantLootFunctions.init();
        PenchantModules.init();
        LootModification.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server ->
            PenchantmentDefinition.buildCache(server.registryAccess())
        );

        PayloadTypeRegistry.clientboundPlay().register(UnlockedEnchantmentsPayload.TYPE, UnlockedEnchantmentsPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(EnchantPayload.TYPE, EnchantPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(EnchantPayload.TYPE, (payload, context) -> {
            if (!(context.player().containerMenu instanceof PenchantmentMenu menu)) {
                LOGGER.warn("Received enchant payload but enchantment menu was not open");
                return;
            }
            menu.handleEnchant(payload.enchantment());
        });

        CanEnchantCallback.STACK.register((stack, enchantment) -> enchantment.is(EnchantmentTags.ARMOR_EXCLUSIVE)
                && EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet().stream().filter(enchantment2 -> enchantment2.is(EnchantmentTags.ARMOR_EXCLUSIVE)).count() < 2
                ? TriState.TRUE
                : TriState.DEFAULT);
    }
}
