package archives.tater.penchant.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;
import java.util.function.Consumer;

public class DatagenUtil {
    private DatagenUtil() {}

    static AdvancementHolder registerAdvancement(Identifier id, ItemLike icon, AdvancementType type, Consumer<AdvancementHolder> consumer, Consumer<Advancement.Builder> init) {
        var builder = Advancement.Builder.advancement()
                .display(new DisplayInfo(
                        icon.asItem().getDefaultInstance(),
                        Component.translatable(id.toLanguageKey("advancements", "title")),
                        Component.translatable(id.toLanguageKey("advancements", "description")),
                        Optional.empty(),
                        type,
                        true,
                        true,
                        false
                ));
        init.accept(builder);
        var advancement = builder.build(id);
        consumer.accept(advancement);
        return advancement;
    }

    static AdvancementHolder createEmptyAdvancement(Identifier id) {
        return Advancement.Builder.advancement().build(id);
    }
}
