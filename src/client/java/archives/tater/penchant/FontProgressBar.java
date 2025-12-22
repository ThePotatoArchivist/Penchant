package archives.tater.penchant;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class FontProgressBar {

    private FontProgressBar() {}

    public static final String SEGMENT = "!";

    public static final Identifier FONT_TEXTURE = Penchant.id("font/bar.png");
    public static final FontDescription FONT = new FontDescription.Resource(Penchant.id("bar"));

    public static MutableComponent getBar(int width, int progress) {
        return Component.literal(SEGMENT.repeat(progress))
                .append(Component.literal(SEGMENT.repeat(width - progress))
                        .withStyle(ChatFormatting.DARK_GRAY))
                .withStyle(ChatFormatting.LIGHT_PURPLE)
                .withStyle(style -> style.withFont(FONT));
    }

    public static MutableComponent getBar(int width, float progress) {
        return getBar(width, (int) (progress * width));
    }
}
