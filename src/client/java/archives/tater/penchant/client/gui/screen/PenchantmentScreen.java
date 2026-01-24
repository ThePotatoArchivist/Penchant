package archives.tater.penchant.client.gui.screen;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.client.gui.ScrollbarComponent;
import archives.tater.penchant.client.gui.widget.EnchantmentSlotWidget;
import archives.tater.penchant.menu.PenchantmentMenu;
import archives.tater.penchant.util.PenchantmentHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;
import static net.minecraft.util.Mth.clamp;
import static net.minecraft.util.Mth.lerp;

public class PenchantmentScreen extends AbstractContainerScreen<PenchantmentMenu> {
    private static final Identifier TEXTURE = Penchant.id("textures/gui/container/enchanting_table.png");
    private static final Identifier BOOK_TEXTURE = Identifier.withDefaultNamespace("textures/entity/enchanting_table_book.png");
    private static final Identifier SCROLLLER_TEXTURE = Penchant.id("container/enchanting_table/scroller");
    private static final AtlasSprite BOOK_ICON_TEXTURE = new AtlasSprite(AtlasIds.GUI, Penchant.id("container/enchanting_table/book"));

    private final ScrollbarComponent scrollbar = new ScrollbarComponent(
            SCROLLLER_TEXTURE,
            6,
            19,
            60,
            102,
            60,
            this::rebuildWidgets
    );

    private final RandomSource random = RandomSource.create();
    private @Nullable BookModel bookModel;

    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;

    public PenchantmentScreen(PenchantmentMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 169;
        inventoryLabelX = 8;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        bookModel = new BookModel(minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
        var displayedEnchantments = menu.getDisplayedEnchantments();
        var stack = menu.getEnchantingStack();
        scrollbar.update(
                leftPos + 162,
                topPos + 14,
                leftPos + 60,
                topPos + 14,
                displayedEnchantments.size() - 4
        );

        var creative = requireNonNull(minecraft.player).hasInfiniteMaterials();
        for (var i = 0; i < 5; i++) {
            var index = scrollbar.getPosition() + i;
            if (index >= displayedEnchantments.size()) break;
            var enchantment = displayedEnchantments.get(index);
            addRenderableWidget(new EnchantmentSlotWidget(
                    leftPos + 60,
                    topPos + 14 + i * EnchantmentSlotWidget.HEIGHT,
                    enchantment,
                    PenchantmentHelper.canEnchant(stack, enchantment),
                    creative || PenchantmentHelper.getBookRequirement(enchantment) <= menu.getBookCount(),
                    creative || PenchantmentHelper.getXpLevelCost(enchantment) <=  menu.getPlayerXp(),
                    menu.isAvailable(enchantment)
            ));
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        minecraft.player.experienceDisplayStartTick = minecraft.player.tickCount;
        var stack = menu.getEnchantingStack();
        var itemChanged = !ItemStack.matches(stack, last);
        if (itemChanged) last = stack.copy();
        tickBook(itemChanged);
        if (itemChanged) rebuildWidgets();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        return scrollbar.mouseClicked(event) || super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        return scrollbar.mouseDragged(event) || super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        scrollbar.mouseReleased();
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return scrollbar.mouseScrolled(mouseX, mouseY, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
        renderBook(guiGraphics, x, y);

        var font = Minecraft.getInstance().font;
        var bookCountText = Component.object(BOOK_ICON_TEXTURE)
                .append(" " + menu.getBookCount());
        guiGraphics.drawString(font, bookCountText, leftPos + 32 - font.width(bookCountText) / 2, topPos + 66, 0xFF404040, false);

        scrollbar.render(guiGraphics);
    }

    private void renderBook(GuiGraphics guiGraphics, int x, int y) {
        var partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        var open = lerp(partialTick, oOpen, this.open);
        var flip = lerp(partialTick, oFlip, this.flip);
        var x0 = x + 14;
        var y0 = y + 14;
        var x1 = x0 + 38;
        var y1 = y0 + 31;
        guiGraphics.submitBookModelRenderState(requireNonNull(bookModel), BOOK_TEXTURE, 40.0F, open, flip, x0, y0, x1, y1);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        var gamePartialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        super.render(guiGraphics, mouseX, mouseY, gamePartialTick);

        renderTooltip(guiGraphics, mouseX, mouseY);

    }

    public void tickBook(boolean itemChanged) {
        if (itemChanged)
            do {
                flipT = flipT + (random.nextInt(4) - random.nextInt(4));
            } while (flip <= flipT + 1.0F && flip >= flipT - 1.0F);

        oFlip = flip;
        oOpen = open;

        open = clamp(open + (!menu.getDisplayedEnchantments().isEmpty() ? 0.2F : -0.2F), 0.0F, 1.0F);
        var f = clamp((flipT - flip) * 0.4F, -0.2F, 0.2F);
        flipA = flipA + (f - flipA) * 0.9F;
        flip = flip + flipA;
    }
}
