package archives.tater.penchant.datagen;

import archives.tater.penchant.registry.PenchantEntityTags;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class EntityTagGenerator extends FabricTagsProvider.EntityTypeTagsProvider {
    public EntityTagGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        builder(PenchantEntityTags.KILL_LEVELS_UP_EQUIPMENT);
    }
}
