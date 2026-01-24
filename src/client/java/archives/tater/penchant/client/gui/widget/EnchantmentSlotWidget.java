package archives.tater.penchant.client.gui.widget;

import archives.tater.penchant.Penchant;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentSlotWidget extends AbstractButton {
    public static final int WIDTH = 101;
    public static final int HEIGHT = 12;
    public static final WidgetSprites TEXTURES = new WidgetSprites(
            Penchant.id("container/enchanting_table/slot"),
            Penchant.id("container/enchanting_table/slot_disabled"),
            Penchant.id("container/enchanting_table/slot_highlighted")
    );
    public static final FontDescription.Resource ALT_FONT = new FontDescription.Resource(Minecraft.ALT_FONT);

    private final Holder<Enchantment> enchantment;
    private final MutableComponent text;
    private final boolean isApplied;
    private final boolean hasEnoughBooks;
    private final boolean hasEnoughXp;
    private final boolean isUnlocked;
    private final int cost;

    public EnchantmentSlotWidget(int x, int y, Holder<Enchantment> enchantment, boolean isApplied, boolean hasEnoughBooks, boolean hasEnoughXp, boolean isUnlocked) {
        super(x, y, WIDTH, HEIGHT, enchantment.value().description());
        this.enchantment = enchantment;
        text = enchantment.value().description().copy();
        if (!isUnlocked && !isApplied) text.withStyle(style -> style.withFont(ALT_FONT));
        if (enchantment.is(EnchantmentTags.CURSE)) text.withStyle(ChatFormatting.DARK_RED);
        this.isApplied = isApplied;
        this.hasEnoughBooks = hasEnoughBooks;
        this.hasEnoughXp = hasEnoughXp;
        this.isUnlocked = isUnlocked;
        cost = enchantment.value().getAnvilCost();
        active = hasEnoughBooks && hasEnoughXp && isUnlocked && !isApplied;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURES.get(active, isHovered()), getX(), getY(), getWidth(), getHeight());

        var font = Minecraft.getInstance().font;

        guiGraphics.drawString(font, text, getX() + 2, getY() + 2, 0xFF404040, false);

        if (!isUnlocked) return;

        var costText = Integer.toString(cost);
        var color = isApplied ? 0xFF685E4A :
                hasEnoughXp ? 0xFF80FF20 :
                0xFF7F1010;
        guiGraphics.drawString(font, costText, getX() + width - 2 - font.width(costText), getY() + 2, color, true);

//        if (isHovered)
//            guiGraphics.setTooltipForNextFrame(null, mouseX, mouseY);
    }

    @Override
    public void onPress(InputWithModifiers input) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
