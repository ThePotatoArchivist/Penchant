package archives.tater.penchant.api;

import archives.tater.penchant.menu.PenchantmentMenu;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemStack;
import net.fabricmc.fabric.api.util.TriState;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Event to modify when items can be enchanted in a {@link PenchantmentMenu}.
 * <p>
 * Penchant already checks {@link FabricItemStack#canBeEnchantedWith} which checks
 * {@link FabricItem#canBeEnchantedWith} and {@link EnchantmentEvents#ALLOW_ENCHANTING}, so if you've enabled
 * enchanting with one of those APIs you don't need to do it here.
 */
@FunctionalInterface
public interface CanEnchantCallback {
    TriState canEnchant(ItemStack stack, Holder<Enchantment> enchantment);

    private static CanEnchantCallback createInvoker(CanEnchantCallback[] listeners) {
        return (stack, enchantment) -> {
            for (var listener : listeners) {
                var result = listener.canEnchant(stack, enchantment);
                if (result != TriState.DEFAULT)
                    return result;
            }
            return TriState.DEFAULT;
        };
    }

    /**
     * If an item should normally be enchantable with a certain enchantment, ignoring compatibility with other enchantments
     */
    Event<CanEnchantCallback> ITEM = EventFactory.createArrayBacked(CanEnchantCallback.class, CanEnchantCallback::createInvoker);

    /**
     * If a particular stack should be enchantable with a certain enchantment, should only be used to check for more specific cases such as component data
     */
    Event<CanEnchantCallback> STACK = EventFactory.createArrayBacked(CanEnchantCallback.class, CanEnchantCallback::createInvoker);
}
