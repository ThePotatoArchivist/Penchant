package archives.tater.penchant;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PenchantmentMenu extends AbstractContainerMenu {
    private static final Identifier EMPTY_SLOT_LAPIS_LAZULI = Identifier.withDefaultNamespace("container/slot/lapis_lazuli");
    private final Container enchantSlots = new SimpleContainer(2) {
        @Override
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
        }
    };
    private final DataSlot power = addDataSlot(DataSlot.standalone());
    private final ContainerLevelAccess access;
    private final Player player;

    public PenchantmentMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(MenuType.ENCHANTMENT, containerId);
        player = playerInventory.player;
        this.access = access;
        addSlot(new Slot(enchantSlots, 0, 15, 47) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        addSlot(new Slot(enchantSlots, 1, 35, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.LAPIS_LAZULI); // TODO: Stripping enchantments onto books
            }

            @Override
            public Identifier getNoItemIcon() {
                return EMPTY_SLOT_LAPIS_LAZULI;
            }
        });
        addStandardInventorySlots(playerInventory, 8, 84);

        access.execute((level, pos) -> {
            power.set(getPower(level, pos));
            ServerPlayNetworking.send((ServerPlayer) player, new AvailableEnchantmentsPayload(getUnlockedEnchantments(level, pos)));
        });
    }

    public static Set<Holder<Enchantment>> getUnlockedEnchantments(Level level, BlockPos pos) {
        return Stream.concat(
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                        .get(EnchantmentTags.IN_ENCHANTING_TABLE)
                        .<HolderSet<Enchantment>>map(Function.identity())
                        .orElse(HolderSet.empty())
                        .stream(),
                EnchantingTableBlock.BOOKSHELF_OFFSETS.stream()
                        .filter(offset -> EnchantingTableBlock.isValidBookShelf(level, pos, offset))
                        .map(pos::offset)
                        .map(level::getBlockEntity)
                        .flatMap(entity -> entity instanceof ChiseledBookShelfBlockEntity bookshelf ? bookshelf.getItems().stream() : Stream.empty())
                        .flatMap(stack -> stack.getEnchantments().keySet().stream())
        ).collect(Collectors.toSet());
    }

    public static int getPower(Level level, BlockPos pos) {
        return EnchantingTableBlock.BOOKSHELF_OFFSETS.stream()
                .filter(offset -> EnchantingTableBlock.isValidBookShelf(level, pos, offset))
                .map(pos::offset)
                .map(level::getBlockState)
                .filter(state -> state.is(BlockTags.ENCHANTMENT_POWER_PROVIDER))
                .mapToInt(state -> state.hasProperty(ChiseledBookShelfBlock.SLOT_0_OCCUPIED)
                        ? (int) ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.stream().filter(state::getValue).count()
                        : 3)
                .sum();
    }

    @Override
    public void slotsChanged(Container container) {
        if (container != this.enchantSlots) return;
        // TODO
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack itemStack2 = slot.getItem();
        var itemStack = itemStack2.copy();
        if (index == 0) {
            if (!moveItemStackTo(itemStack2, 2, Inventory.INVENTORY_SIZE + 2, true))
                return ItemStack.EMPTY;
        } else if (index == 1) {
            if (!moveItemStackTo(itemStack2, 2, Inventory.INVENTORY_SIZE + 2, true))
                return ItemStack.EMPTY;
        } else if (itemStack2.is(Items.LAPIS_LAZULI)) {
            if (!moveItemStackTo(itemStack2, 1, 2, true))
                return ItemStack.EMPTY;
        } else {
            if (slots.getFirst().hasItem() || !slots.getFirst().mayPlace(itemStack2))
                return ItemStack.EMPTY;

            ItemStack itemStack3 = itemStack2.copyWithCount(1);
            itemStack2.shrink(1);
            slots.getFirst().setByPlayer(itemStack3);
        }

        if (itemStack2.isEmpty())
            slot.setByPlayer(ItemStack.EMPTY);
        else
            slot.setChanged();

        if (itemStack2.getCount() == itemStack.getCount())
            return ItemStack.EMPTY;

        slot.onTake(player, itemStack2);

        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, blockPos) -> clearContainer(player, enchantSlots));
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, Blocks.ENCHANTING_TABLE);
    }
}
