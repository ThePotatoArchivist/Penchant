package archives.tater.penchant;

import archives.tater.penchant.client.FontProgressBar;
import archives.tater.penchant.client.KeyMappingExt;
import archives.tater.penchant.client.gui.screen.PenchantmentScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

import org.jetbrains.annotations.NotNull;

import static net.minecraft.util.Util.makeDescriptionId;


public class PenchantClient implements ClientModInitializer {
    private static KeyMappingExt keybind(Identifier id, int key, KeyMapping.Category category) {
        return (KeyMappingExt) KeyBindingHelper.registerKeyBinding(new KeyMappingExt(makeDescriptionId("key", id), Type.KEYSYM, key, category));
    }

    private static final KeyMapping.Category PENCHANT_CATEGORY = KeyMapping.Category.register(Penchant.id(Penchant.MOD_ID));
    public static final KeyMappingExt SHOW_PROGRESS_KEYBIND = keybind(
            Penchant.id("show_progress"),
            InputConstants.KEY_LCONTROL,
            PENCHANT_CATEGORY
    );

    public static final int BAR_WIDTH = 32;

    public static Component getProgressKeyHint() {
        return Component.translatable("penchant.tooltip.progress.key", Component.keybind(SHOW_PROGRESS_KEYBIND.getName()))
                .withStyle(ChatFormatting.DARK_GRAY);
    }

    public static Component getProgressTooltip(EnchantmentProgress progress, Holder<@NotNull Enchantment> enchantment, int level, int maxDamage) {
        if (level >= enchantment.value().getMaxLevel())
            return Component.literal("  ")
                    .append(FontProgressBar.getBar(BAR_WIDTH, BAR_WIDTH))
                    .append(" ")
                    .append(Component.translatable("penchant.tooltip.progress.max"))
                    .withStyle(ChatFormatting.LIGHT_PURPLE);

        var maxProgress = EnchantmentProgress.getMaxProgress(enchantment, level, maxDamage);

        return Component.literal("  ")
                .append(FontProgressBar.getBar(BAR_WIDTH, BAR_WIDTH * progress.getProgress(enchantment) / maxProgress))
                .append(" ")
                .append(Component.translatable("penchant.tooltip.progress",
                        Component.literal(Integer.toString(progress.getProgress(enchantment))).withStyle(ChatFormatting.LIGHT_PURPLE),
                        maxProgress
                ).withStyle(ChatFormatting.DARK_GRAY))
                ;
    }

    @Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        MenuScreens.register(Penchant.PENCHANTMENT_MENU, PenchantmentScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(UnlockedEnchantmentsPayload.TYPE, (payload, context) -> {
            if (!(context.player().containerMenu instanceof PenchantmentMenu menu)) {
                Penchant.LOGGER.warn("Recieved enchantments payload but enchantment menu was not open");
                return;
            }
            menu.setUnlockedEnchantments(payload.unlocked());
        });
	}
}
