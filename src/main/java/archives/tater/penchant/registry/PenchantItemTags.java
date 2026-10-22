package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class PenchantItemTags {

    private static TagKey<Item> create(String path) {
        return TagKey.create(Registries.ITEM, Penchant.id(path));
    }

    public static final TagKey<Item> MAX_LEVEL_ENCHANTMENTS = create("max_level_enchantments");
    public static final TagKey<Item> PROPORTIONAL_ENCHANTMENT_PROGRESS = create("proportional_enchantment_progress");
    public static final TagKey<Item> LEVEL_UP_BY_KILLING = create("level_up_by_killing");
    public static final TagKey<Item> DEFAULT_PROGRESS_COST_FACTOR_ONE = create("default_progress_cost_factor_one");
}
