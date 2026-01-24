package archives.tater.penchant.datagen;

import net.minecraft.SharedConstants;
import net.minecraft.data.DataProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.flag.FeatureFlagSet;

public class PackMetaGen {

    @SuppressWarnings("deprecation")
    public static final InclusiveRange<PackFormat> DATAPACK_RANGE = new InclusiveRange<>(PackFormat.of(SharedConstants.DATA_PACK_FORMAT_MAJOR, SharedConstants.DATA_PACK_FORMAT_MINOR));

    private PackMetaGen() {}

    public static DataProvider.Factory<PackMetadataGenerator> pack(Identifier id) {
        return pack(id, FeatureFlagSet.of());
    }

    public static DataProvider.Factory<PackMetadataGenerator> pack(Identifier id, FeatureFlagSet flags) {
        return output -> {
            var generator = new PackMetadataGenerator(output);
            generator.add(PackMetadataSection.SERVER_TYPE, new PackMetadataSection(
                    Component.translatable(id.toLanguageKey("dataPack", "description")),
                    DATAPACK_RANGE
            ));
            if (!flags.isEmpty())
                generator.add(FeatureFlagsMetadataSection.TYPE, new FeatureFlagsMetadataSection(flags));
            return generator;
        };
    }
}
