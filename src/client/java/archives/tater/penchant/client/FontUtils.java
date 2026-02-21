package archives.tater.penchant.client;

import archives.tater.penchant.Penchant;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class FontUtils {

    private FontUtils() {}

    public static final String BAR_SEGMENT = "!";
    public static final String THIN_SPACE = "\u2009";

    public static final ResourceLocation FONT_TEXTURE = Penchant.id("font/bar.png");
    public static final ResourceLocation FONT = Penchant.id("bar");

    public static final Component THIN_SPACE_TEXT = Component.literal(THIN_SPACE).withStyle(style -> style.withFont(FONT));

    public static MutableComponent getBar(int width, int progress) {
        return Component.literal(BAR_SEGMENT.repeat(progress))
                .append(Component.literal(BAR_SEGMENT.repeat(width - progress))
                        .withStyle(ChatFormatting.DARK_GRAY))
                .withStyle(ChatFormatting.LIGHT_PURPLE)
                .withStyle(style -> style.withFont(FONT));
    }

    public static MutableComponent getBar(int width, float progress) {
        return getBar(width, (int) (progress * width));
    }
}
