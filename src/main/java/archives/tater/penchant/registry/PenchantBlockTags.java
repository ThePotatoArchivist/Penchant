package archives.tater.penchant.registry;

import archives.tater.penchant.Penchant;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class PenchantBlockTags {

    private static TagKey<Block> create(String path) {
        return TagKey.create(Registries.BLOCK, Penchant.id(path));
    }

    public static final TagKey<Block> DISENCHANTER = create("disenchanter");
}
