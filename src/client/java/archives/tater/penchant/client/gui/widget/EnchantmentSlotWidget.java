package archives.tater.penchant.client.gui.widget;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.network.EnchantPayload;
import archives.tater.penchant.util.PenchantmentHelper;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

import org.jspecify.annotations.Nullable;

import java.util.List;

public class EnchantmentSlotWidget extends AbstractButton {
    public static final int WIDTH = 131;
    public static final int HEIGHT = 12;
    public static final WidgetSprites TEXTURES = new WidgetSprites(
            Penchant.id("container/enchanting_table/slot"),
            Penchant.id("container/enchanting_table/slot_disabled"),
            Penchant.id("container/enchanting_table/slot_highlighted")
    );
    public static final FontDescription.Resource ALT_FONT = new FontDescription.Resource(Minecraft.ALT_FONT);

    public static final int DISABLED_COLOR = 0xFF685E4A;
    public static final int INSUFFICIENT_COLOR = 0xFF7F1010;

    private final Holder<Enchantment> enchantment;
    private final Component text;
    private final @Nullable Component costText;

    private EnchantmentSlotWidget(int x, int y, Holder<Enchantment> enchantment, List<Holder<Enchantment>> incompatible, boolean remove, boolean showCosts, boolean canUse, boolean hasEnoughBooks, boolean hasEnoughXp, boolean isUnlocked) {
        super(x, y, WIDTH, HEIGHT, enchantment.value().description());
        this.enchantment = enchantment;

        var text = enchantment.value().description().copy();
        if (!isUnlocked && canUse) text.withStyle(style -> style.withFont(ALT_FONT));
        if (enchantment.is(EnchantmentTags.CURSE)) text.withStyle(ChatFormatting.DARK_RED);
        this.text = text;

        var xpCost = PenchantmentHelper.getXpLevelCost(enchantment);
        var bookRequirement = PenchantmentHelper.getBookRequirement(enchantment);

        costText = !showCosts ? null : Component.literal(Integer.toString(bookRequirement))
                .withColor(!canUse ? DISABLED_COLOR :
                        !hasEnoughBooks ? INSUFFICIENT_COLOR
                                : 0xFF5555FF)
                .append(" ")
                .append(Component.literal(Integer.toString(xpCost))
                        .withColor(!canUse ? DISABLED_COLOR :
                                !hasEnoughXp ? INSUFFICIENT_COLOR
                                        : 0xFF80FF20));

        if (remove && canUse)
            setTooltip(Tooltip.create(Component.translatable("widget.penchant.enchantment_slot.tooltip.remove", enchantment.value().description())));
        else if (remove)
            setTooltip(Tooltip.create(Component.translatable("widget.penchant.enchantment_slot.tooltip.remove.disabled", enchantment.value().description())));
        else if (!canUse)
            setTooltip(Tooltip.create(Component.empty()
                    .append(PenchantmentHelper.getName(enchantment))
                    .append("\n ")
                    .append(Component.translatable("widget.penchant.enchantment_slot.tooltip.added").withStyle(ChatFormatting.GRAY))));
        else if (!isUnlocked)
            setTooltip(Tooltip.create(Component.translatable("widget.penchant.enchantment_slot.tooltip.locked").withStyle(ChatFormatting.RED)));
        else {
            var tooltip = PenchantmentHelper.getName(enchantment).copy()
                    .append("\n ")
                    .append(Component.translatable("widget.penchant.enchantment_slot.tooltip.book_requirement", bookRequirement)
                            .withStyle(hasEnoughBooks ? ChatFormatting.BLUE : ChatFormatting.RED))
                    .append("\n ")
                    .append(Component.translatable("widget.penchant.enchantment_slot.tooltip.xp_cost", xpCost)
                            .withStyle(hasEnoughXp ? ChatFormatting.GREEN : ChatFormatting.RED));
            if (!incompatible.isEmpty()) tooltip
                    .append(Component.literal("\n "))
                    .append(Component.translatable("widget.penchant.enchantment_slot.tooltip.incompatible", ComponentUtils.formatList(incompatible, holder -> holder.value().description()))
                            .withStyle(ChatFormatting.RED));
            setTooltip(Tooltip.create(tooltip));
        }

        active = hasEnoughBooks && hasEnoughXp && isUnlocked && canUse && incompatible.isEmpty();
    }

    public EnchantmentSlotWidget(int x, int y, Holder<Enchantment> enchantment, List<Holder<Enchantment>> incompatible, boolean canAdd, boolean hasEnoughBooks, boolean hasEnoughXp, boolean isUnlocked) {
        this(x, y, enchantment, incompatible, false, isUnlocked, canAdd, hasEnoughBooks, hasEnoughXp, isUnlocked);
    }

    public EnchantmentSlotWidget(int x, int y, Holder<Enchantment> enchantment, boolean canRemove) {
        this(x, y, enchantment, List.of(), true, false, canRemove, true, true, true);
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURES.get(active, isHovered()), getX(), getY(), getWidth(), getHeight());

        var font = Minecraft.getInstance().font;

        guiGraphics.drawString(font, text, getX() + 2, getY() + 2, 0xFF404040, false);

        if (costText != null)
            guiGraphics.drawString(font, costText, getX() + width - 2 - font.width(costText), getY() + 2, 0xFF404040, true);
    }

    @Override
    public void onPress(InputWithModifiers input) {
        ClientPlayNetworking.send(new EnchantPayload(enchantment));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
