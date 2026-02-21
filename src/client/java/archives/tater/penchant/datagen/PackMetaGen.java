package archives.tater.penchant.datagen;

import net.minecraft.SharedConstants;
import net.minecraft.data.DataProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.flag.FeatureFlagSet;

import java.util.Optional;

public class PackMetaGen {

    private PackMetaGen() {}

    public static DataProvider.Factory<PackMetadataGenerator> pack(ResourceLocation id) {
        return pack(id, FeatureFlagSet.of());
    }

    public static DataProvider.Factory<PackMetadataGenerator> pack(ResourceLocation id, FeatureFlagSet flags) {
        return output -> {
            var generator = new PackMetadataGenerator(output);
            generator.add(PackMetadataSection.TYPE, new PackMetadataSection(
                    Component.translatable(id.toLanguageKey("dataPack", "description")),
                    SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA),
                    Optional.empty()
            ));
            if (!flags.isEmpty())
                generator.add(FeatureFlagsMetadataSection.TYPE, new FeatureFlagsMetadataSection(flags));
            return generator;
        };
    }
}
