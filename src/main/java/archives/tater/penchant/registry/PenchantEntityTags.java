package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class PenchantEntityTags {

    private static TagKey<EntityType<?>> create(String path) {
        return TagKey.create(Registries.ENTITY_TYPE, Penchant.id(path));
    }

    public static final TagKey<EntityType<?>> KILL_LEVELS_UP_EQUIPMENT = create("kill_levels_up_equipment");
}
