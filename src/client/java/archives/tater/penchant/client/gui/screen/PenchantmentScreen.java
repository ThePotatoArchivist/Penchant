package archives.tater.penchant.client.gui.screen;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.client.FontUtils;
import archives.tater.penchant.client.gui.ScrollbarComponent;
import archives.tater.penchant.client.gui.widget.EnchantmentSlotWidget;
import archives.tater.penchant.menu.PenchantmentMenu;
import archives.tater.penchant.util.PenchantmentHelper;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static net.minecraft.util.Mth.*;

public class PenchantmentScreen extends AbstractContainerScreen<PenchantmentMenu> {
    private static final ResourceLocation TEXTURE = Penchant.id("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/enchanting_table_book.png");
    private static final ResourceLocation SCROLLLER_TEXTURE = Penchant.id("container/enchanting_table/scroller");
    public static final ResourceLocation LAPIS_LAZULI_SLOT_TEXTURE = ResourceLocation.withDefaultNamespace("container/slot/lapis_lazuli");
    public static final ResourceLocation BOOK_SLOT_TEXTURE = Penchant.id("container/slot/book");
    private static final List<ResourceLocation> INGREDIENT_SLOT_TEXTURES = List.of(
            LAPIS_LAZULI_SLOT_TEXTURE,
            BOOK_SLOT_TEXTURE
    );
    private static final List<ResourceLocation> INGREDIENT_SLOT_TEXTURES_NO_DISENCHANT = List.of(
            LAPIS_LAZULI_SLOT_TEXTURE
    );
    private static final Component ENCHANTING_SLOT_TOOLTIP = Component.translatable("container.penchant.enchant.slot.enchant");
    private static final Component INGREDIENT_SLOT_TOOLTIP = Component.translatable("container.penchant.enchant.slot.ingredient");
    private static final Component INGREDIENT_SLOT_DISENCHANT_TOOLTIP = Component.translatable("container.penchant.enchant.slot.ingredient.disenchant");
    private static final int TOOLTIP_WIDTH = 115;

    private final ScrollbarComponent scrollbar = new ScrollbarComponent(
            SCROLLLER_TEXTURE,
            6,
            19,
            60,
            EnchantmentSlotWidget.WIDTH + 1,
            60,
            this::rebuildWidgets
    );

    private final CyclingSlotBackground secondSlotBackground = new CyclingSlotBackground(1);

    private final RandomSource random = RandomSource.create();
    private BookModel bookModel;

    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;

    public PenchantmentScreen(PenchantmentMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 206;
        imageHeight = 172;
        inventoryLabelX = 23;
        inventoryLabelY = imageHeight - 94;
        menu.setSlotChangeListener(this::rebuildWidgets);
    }

    @Override
    protected void init() {
        super.init();
        bookModel = new BookModel(requireNonNull(minecraft).getEntityModels().bakeLayer(ModelLayers.BOOK));
        var displayedEnchantments = menu.getDisplayedEnchantments();
        var stack = menu.getEnchantingStack();
        scrollbar.update(
                leftPos + 192,
                topPos + 14,
                leftPos + 60,
                topPos + 14,
                displayedEnchantments.size() - 4
        );

        var creative = requireNonNull(minecraft.player).hasInfiniteMaterials();
        if (!menu.isEnchanting() && !menu.isDisenchanting()) return;
        var disenchanting = menu.isDisenchanting();
        for (var i = 0; i < 5; i++) {
            var index = scrollbar.getPosition() + i;
            if (index >= displayedEnchantments.size()) break;
            var enchantment = displayedEnchantments.get(index);
            if (disenchanting)
                addRenderableWidget(new EnchantmentSlotWidget(
                        leftPos + 60,
                        topPos + 14 + i * EnchantmentSlotWidget.HEIGHT,
                        enchantment,
                        getIncompatible(menu.getIngredientStack(), enchantment),
                        creative || PenchantmentHelper.getBookRequirement(enchantment) <= menu.getBookCount()
                ));
            else
                addRenderableWidget(new EnchantmentSlotWidget(
                        leftPos + 60,
                        topPos + 14 + i * EnchantmentSlotWidget.HEIGHT,
                        enchantment,
                        getIncompatible(stack, enchantment),
                        !PenchantmentHelper.hasEnchantment(stack, enchantment),
                        creative || PenchantmentHelper.getBookRequirement(enchantment) <= menu.getBookCount(),
                        creative || PenchantmentHelper.getXpLevelCost(enchantment) <=  menu.getPlayerXp(),
                        menu.isAvailable(enchantment)
                ));
        }
    }

    private List<Holder<Enchantment>> getIncompatible(ItemStack stack, Holder<Enchantment> enchantment) {
        return PenchantmentHelper.getEnchantments(stack).keySet().stream().filter(other -> !enchantment.equals(other) && !Enchantment.areCompatible(enchantment, other)).toList();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        secondSlotBackground.tick(menu.canDisenchant() ? INGREDIENT_SLOT_TEXTURES : INGREDIENT_SLOT_TEXTURES_NO_DISENCHANT);
        tickBook();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return scrollbar.mouseClicked(mouseX, mouseY) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return scrollbar.mouseDragged(mouseY) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrollbar.mouseReleased();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return scrollbar.mouseScrolled(mouseX, mouseY, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        var x = (width - imageWidth) / 2;
        var y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
        renderBook(guiGraphics, x, y, partialTick);

        var font = Minecraft.getInstance().font;
        var infoText = FontUtils.BOOK_TEXT.copy()
                .append(FontUtils.THIN_SPACE_TEXT)
                .append(Integer.toString(menu.getBookCount()))
                .append(" ")
                .append(FontUtils.GRINDSTONE_TEXT)
                .append(FontUtils.THIN_SPACE_TEXT)
                .append(menu.hasDisenchanter() ? "✔" : "❌");
        guiGraphics.drawString(font, infoText, leftPos + 32 - font.width(infoText) / 2, topPos + 18, 0xFF606060, false);

        secondSlotBackground.render(menu, guiGraphics, partialTick, leftPos, topPos);

        scrollbar.render(guiGraphics);
    }

    private void renderBook(GuiGraphics guiGraphics, int x, int y, float partialTick) {
        var open = lerp(partialTick, this.oOpen, this.open);
        var flip = lerp(partialTick, this.oFlip, this.flip);
        Lighting.setupForEntityInInventory();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 33f, y + 31f, 100f);
        var h = 40f;
        guiGraphics.pose().scale(-h, h, h);
        guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(25f));
        guiGraphics.pose().translate((1.0F - open) * 0.2F, (1.0F - open) * 0.1F, (1.0F - open) * 0.25F);
        var i = -(1.0F - open) * 90.0F - 90.0F;
        guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(i));
        guiGraphics.pose().mulPose(Axis.XP.rotationDegrees(180.0F));
        var j = clamp(frac(flip + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
        var k = clamp(frac(flip + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
        bookModel.setupAnim(0.0F, j, k, open);
        var vertexConsumer = guiGraphics.bufferSource().getBuffer(bookModel.renderType(BOOK_TEXTURE));
        bookModel.renderToBuffer(guiGraphics.pose(), vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY);
        guiGraphics.flush();
        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        var gamePartialTick = requireNonNull(minecraft).getTimer().getGameTimeDeltaPartialTick(false);
        super.render(guiGraphics, mouseX, mouseY, gamePartialTick);

        if (hoveredSlot != null && !hoveredSlot.hasItem())
            guiGraphics.renderTooltip(font, font.split(
                    hoveredSlot.index == 0
                            ? ENCHANTING_SLOT_TOOLTIP
                            : menu.canDisenchant()
                                    ? INGREDIENT_SLOT_DISENCHANT_TOOLTIP
                                    : INGREDIENT_SLOT_TOOLTIP,
                    TOOLTIP_WIDTH), mouseX, mouseY);
        else
            renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public void tickBook() {
        var stack = menu.getEnchantingStack();
        if (!ItemStack.matches(stack, last)) {
            last = stack;
            do {
                flipT = flipT + (random.nextInt(4) - random.nextInt(4));
            } while (flip <= flipT + 1.0F && flip >= flipT - 1.0F);
        }

        oFlip = flip;
        oOpen = open;

        open = clamp(open + (!menu.getDisplayedEnchantments().isEmpty() ? 0.2F : -0.2F), 0.0F, 1.0F);
        var f = clamp((flipT - flip) * 0.4F, -0.2F, 0.2F);
        flipA = flipA + (f - flipA) * 0.9F;
        flip = flip + flipA;
    }
}
