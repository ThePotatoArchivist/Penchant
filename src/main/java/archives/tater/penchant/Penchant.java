package archives.tater.penchant;

import archives.tater.penchant.component.EnchantmentProgress;
import archives.tater.penchant.loot.LootModification;
import archives.tater.penchant.menu.PenchantmentMenu;
import archives.tater.penchant.network.EnchantPayload;
import archives.tater.penchant.registry.*;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

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

    public static final PenchantServerConfig CONFIG = PenchantServerConfig.createToml(
            FabricLoader.getInstance().getConfigDir(),
            MOD_ID,
            "server",
            PenchantServerConfig.class
    );

    @Override
	public void onInitialize() {
        PenchantCompat.init();
        PenchantRegistries.init();
        PenchantFlag.init();
        PenchantComponents.init();
        PenchantEnchantments.init();
        PenchantMenus.init();
        PenchantAdvancements.init();
        PenchantModules.init();
        LootModification.init();

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof Enemy) && !entity.is(PenchantEntityTags.KILL_LEVELS_UP_EQUIPMENT)) return;
            if (!(damageSource.getEntity() instanceof LivingEntity livingEntity)) return;

            for (var slot : EquipmentSlot.VALUES) {
                var stack = livingEntity.getItemBySlot(slot);
                if (!stack.is(PenchantItemTags.LEVEL_UP_BY_KILLING) || livingEntity.getEquipmentSlotForItem(stack) != slot) continue;
                EnchantmentProgress.addToProgress(stack, 1, livingEntity);
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server ->
            PenchantmentDefinition.buildCache(server.registryAccess())
        );

        PayloadTypeRegistry.serverboundPlay().register(EnchantPayload.TYPE, EnchantPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(EnchantPayload.TYPE, (payload, context) -> {
            if (!(context.player().containerMenu instanceof PenchantmentMenu menu)) {
                LOGGER.warn("Received enchant payload but enchantment menu was not open");
                return;
            }
            menu.handleEnchant(payload.enchantment());
        });
    }
}
