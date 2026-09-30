package archives.tater.penchant.api;

import archives.tater.penchant.menu.PenchantmentMenu;
import archives.tater.penchant.util.PenchantmentHelper;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Event to modify when a {@link PenchantmentMenu} marks enchantments as incompatible.
 * <p>
 * This behavior falls back to {@link Enchantment#areCompatible} which checks both enchantments' exclusive sets, so if
 * you've set incompatibilities with a mixin there you don't need to do it here.
 * <p>
 * This event does not prevent enchanting, it only shows a message to the player when an enchantment is disallowed
 * according to {@link PenchantmentHelper#canEnchant(ItemStack, Holder)}
 */
@FunctionalInterface
public interface EnchantmentCompatibleCallback {
    /**
     * @param enchantment The enchantment attempted to be added
     * @param existing    The existing enchantment
     * @param stack       The stack under consideration
     * @param registries  Registries
     * @return If {@code enchantment} is incompatible with {@code existing} for the current {@code stack}, or {@link TriState#DEFAULT} to
     * fall back to other handlers.
     */
    TriState areCompatible(Holder<Enchantment> enchantment, Holder<Enchantment> existing, ItemStack stack, HolderLookup.Provider registries);

    Event<EnchantmentCompatibleCallback> EVENT = EventFactory.createArrayBacked(EnchantmentCompatibleCallback.class, listeners -> (enchantment, existing, stack, registries) -> {
        for (var listener : listeners) {
            var result = listener.areCompatible(enchantment, existing, stack, registries);
            if (result != TriState.DEFAULT)
                return result;
        }
        return TriState.DEFAULT;
    });
}
