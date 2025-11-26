package archives.tater.penchant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class FontProgressBar {

    private FontProgressBar() {}

    public static final String FILLED = "\"";
    public static final String EMPTY = "!";

    public static final ResourceLocation FONT_TEXTURE = Penchant.id("font/bar.png");
    public static final FontDescription FONT = new FontDescription.Resource(Penchant.id("bar"));

    public static MutableComponent getBar(int width, int progress) {
        return Component.literal(FILLED.repeat(progress) + EMPTY.repeat(width - progress)).withStyle(style -> style
                .withFont(FONT)
        );
    }

    public static MutableComponent getBar(int width, float progress) {
        return getBar(width, (int) (progress * width));
    }
}
