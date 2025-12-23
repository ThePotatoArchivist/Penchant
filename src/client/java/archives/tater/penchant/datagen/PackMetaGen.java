package archives.tater.penchant.datagen;

import net.minecraft.SharedConstants;
import net.minecraft.data.DataProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;

import org.jetbrains.annotations.NotNull;

public class PackMetaGen {
    private PackMetaGen() {}

    @SuppressWarnings("deprecation")
    public static DataProvider.Factory<@NotNull PackMetadataGenerator> pack(Identifier id) {
        return output -> new PackMetadataGenerator(output)
                .add(PackMetadataSection.SERVER_TYPE, new PackMetadataSection(
                        Component.translatable(id.toLanguageKey("dataPack", "description")),
                        new InclusiveRange<>(PackFormat.of(SharedConstants.DATA_PACK_FORMAT_MAJOR, SharedConstants.DATA_PACK_FORMAT_MINOR))
                ));
    }
}
