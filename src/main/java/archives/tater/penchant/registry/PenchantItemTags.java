package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class PenchantItemTags {

    private static TagKey<Item> of(String path) {
        return TagKey.create(Registries.ITEM, Penchant.id(path));
    }

    public static final TagKey<Item> MAX_LEVEL_ENCHANTMENTS = of("max_level_enchantments");
}
