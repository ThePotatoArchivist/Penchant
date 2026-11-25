package archives.tater.penchant.client.gui.screen;

import archives.tater.penchant.Penchant;
import archives.tater.penchant.client.FontUtils;
import archives.tater.penchant.client.gui.widget.EnchantmentSlotWidget;
import archives.tater.penchant.menu.PenchantmentMenu;
import archives.tater.penchant.util.PenchantmentHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.GroupNarration;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import com.google.common.collect.ImmutableList;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.clamp;
import static java.util.Objects.requireNonNull;
import static net.minecraft.util.Mth.lerp;

public class PenchantmentScreen extends AbstractContainerScreen<PenchantmentMenu> {
    private static final Identifier TEXTURE = Penchant.id("textures/gui/container/enchanting_table.png");
    private static final Identifier BOOK_TEXTURE = Identifier.withDefaultNamespace("textures/entity/enchantment/enchanting_table_book.png");
    private static final Identifier SCROLLLER_TEXTURE = Penchant.id("container/enchanting_table/scroller");
    private static final AtlasSprite BOOK_ICON_TEXTURE = new AtlasSprite(AtlasIds.GUI, Penchant.id("container/enchanting_table/book"));
    private static final AtlasSprite GRINDSTONE_ICON_TEXTURE = new AtlasSprite(AtlasIds.GUI, Penchant.id("container/enchanting_table/grindstone"));
    public static final Identifier LAPIS_LAZULI_SLOT_TEXTURE = Identifier.withDefaultNamespace("container/slot/lapis_lazuli");
    public static final Identifier BOOK_SLOT_TEXTURE = Penchant.id("container/slot/book");
    private static final List<Identifier> INGREDIENT_SLOT_TEXTURES = List.of(
            LAPIS_LAZULI_SLOT_TEXTURE,
            BOOK_SLOT_TEXTURE
    );
    private static final List<Identifier> INGREDIENT_SLOT_TEXTURES_NO_DISENCHANT = List.of(
            LAPIS_LAZULI_SLOT_TEXTURE
    );
    private static final Component ENCHANTING_SLOT_TOOLTIP = Component.translatable("container.penchant.enchant.slot.enchant");
    private static final Component INGREDIENT_SLOT_TOOLTIP = Component.translatable("container.penchant.enchant.slot.ingredient");
    private static final Component INGREDIENT_SLOT_DISENCHANT_TOOLTIP = Component.translatable("container.penchant.enchant.slot.ingredient.disenchant");
    private static final int TOOLTIP_WIDTH = 115;

    private List<EnchantmentSlotWidget> enchantmentOptions = List.of();

    private final CyclingSlotBackground secondSlotBackground = new CyclingSlotBackground(1);

    private int lastExperienceLevel;

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
        super(menu, playerInventory, title, 206, 172);
        inventoryLabelX = 23;
        inventoryLabelY = imageHeight - 94;
        menu.setSlotChangeListener(this::rebuildWidgets);
        lastExperienceLevel = minecraft.player.experienceLevel;
    }

    @Override
    protected void init() {
        super.init();
        bookModel = new BookModel(minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
        var displayedEnchantments = menu.getDisplayedEnchantments();
        var stack = menu.getEnchantingStack();

        if (!menu.isEnchanting() && !menu.isDisenchanting()) return;
        var creative = requireNonNull(minecraft.player).hasInfiniteMaterials();
        var registries = minecraft.level.registryAccess();
        var disenchanting = menu.isDisenchanting();
        var layout = LinearLayout.vertical();
        var options = ImmutableList.<EnchantmentSlotWidget>builder();
        for (var enchantment : displayedEnchantments) {
            options.add(layout.addChild(disenchanting
                ? new EnchantmentSlotWidget(
                        0,
                        0,
                        enchantment,
                        getIncompatible(menu.getIngredientStack(), enchantment, registries),
                        !PenchantmentHelper.hasEnchantment(menu.getIngredientStack(), enchantment),
                        creative || PenchantmentHelper.getBookRequirement(enchantment) <= menu.getBookCount()
                )
                : new EnchantmentSlotWidget(
                        0,
                        0,
                        enchantment,
                        getIncompatible(stack, enchantment, registries),
                        PenchantmentHelper.canEnchant(stack, enchantment),
                        PenchantmentHelper.hasEnchantment(stack, enchantment),
                        creative || !menu.getIngredientStack().isEmpty(),
                        creative || PenchantmentHelper.getBookRequirement(enchantment) <= menu.getBookCount(),
                        creative || PenchantmentHelper.getXpLevelCost(enchantment) <=  menu.getPlayerXp(),
                        creative || menu.isAvailable(enchantment)
                )
            ));
        }

        var scrollableLayout = new ScrollableLayout(
                minecraft,
                layout,
                EnchantmentSlotWidget.HEIGHT * 5,
                new AbstractScrollArea.ScrollbarSettings(SCROLLLER_TEXTURE, null, null, 6, 19, 2, false),
                1,
                ScrollableLayout.ReserveStrategy.RIGHT,
                GroupNarration.list("narrator.position.list", "narration.group_navigation.usage.enchantment", ScreenAxis.VERTICAL)
        );
        scrollableLayout.setPosition(leftPos + 60, topPos + 14);
        scrollableLayout.arrangeElements();
        scrollableLayout.visitWidgets(this::addRenderableWidget);
        scrollableLayout.setTabGroup(7); // TODO what
        scrollableLayout.allowTabNavigationInside(false);
    }

    @Override
    protected void updateSlotWidgets() {
        var widgets = new ArrayList<SlotWidget>();

        for (var slot : menu.slots)
            widgets.add(new SlotWidget(slot, minecraft, imageWidth, leftPos, topPos, SlotWidget.defaultTooltipWithCustomEmpty(minecraft, this::createEmptySlotTooltip)));

        this.setSlotWidgets(widgets);
    }

    private @Nullable Tooltip createEmptySlotTooltip(Slot slot) {
        return switch (slot.index) {
            case 0 -> Tooltip.create(ENCHANTING_SLOT_TOOLTIP);
            case 1 -> Tooltip.create(menu.canDisenchant() ? INGREDIENT_SLOT_DISENCHANT_TOOLTIP : INGREDIENT_SLOT_TOOLTIP);
            default -> null;
        };
    }

    private List<Holder<Enchantment>> getIncompatible(ItemStack stack, Holder<Enchantment> enchantment, HolderLookup.Provider registries) {
        if (PenchantmentHelper.hasEnchantment(stack, enchantment)) return List.of();

        return PenchantmentHelper.getEnchantments(stack).keySet().stream().filter(other -> !PenchantmentHelper.areCompatible(enchantment, other, stack, registries)).toList();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        minecraft.player.experienceDisplayStartTick = minecraft.player.tickCount;
        secondSlotBackground.tick(menu.canDisenchant() ? INGREDIENT_SLOT_TEXTURES : INGREDIENT_SLOT_TEXTURES_NO_DISENCHANT);
        if (lastExperienceLevel != minecraft.player.experienceLevel) {
            lastExperienceLevel = minecraft.player.experienceLevel;
            rebuildWidgets();
        }
        tickBook();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics) {
        super.extractBackground(graphics);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        graphics.blit(GuiPipeline.TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
        extractBook(graphics, x, y);

        var font = Minecraft.getInstance().font;
        var infoText = Component.object(BOOK_ICON_TEXTURE)
                .append(FontUtils.THIN_SPACE_TEXT)
                .append(Integer.toString(menu.getBookCount()))
                .append(" ")
                .append(Component.object(GRINDSTONE_ICON_TEXTURE))
                .append(FontUtils.THIN_SPACE_TEXT)
                .append(menu.hasDisenchanter() ? "✔" : "❌");
        graphics.text(font, infoText, leftPos + 32 - font.width(infoText) / 2, topPos + 18, 0xFF606060, false);

        secondSlotBackground.extractRenderState(menu, graphics, leftPos, topPos);
    }

    private void extractBook(GuiGraphicsExtractor guiGraphics, int x, int y) {
        var partialTick = minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        var open = lerp(partialTick, oOpen, this.open);
        var flip = lerp(partialTick, oFlip, this.flip);
        var x0 = x + 14;
        var y0 = y + 25;
        var x1 = x0 + 38;
        var y1 = y0 + 31;
        guiGraphics.book(requireNonNull(bookModel), BOOK_TEXTURE, 40.0F, open, flip, x0, y0, x1, y1);
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
