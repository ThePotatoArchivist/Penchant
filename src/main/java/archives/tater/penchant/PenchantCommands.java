package archives.tater.penchant;

import archives.tater.penchant.component.EnchantmentProgress;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.*;
import static net.minecraft.commands.arguments.EntityArgument.entity;
import static net.minecraft.commands.arguments.EntityArgument.getEntity;
import static net.minecraft.commands.arguments.SlotArgument.getSlot;
import static net.minecraft.commands.arguments.SlotArgument.slot;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.blockPos;
import static net.minecraft.commands.arguments.coordinates.BlockPosArgument.getLoadedBlockPos;

public class PenchantCommands {

    static final Dynamic3CommandExceptionType ERROR_TARGET_NOT_A_CONTAINER = new Dynamic3CommandExceptionType(
            (x, y, z) -> Component.translatableEscape("commands.item.target.not_a_container", x, y, z)
    );
    static final DynamicCommandExceptionType ERROR_TARGET_INAPPLICABLE_SLOT = new DynamicCommandExceptionType(
            slot -> Component.translatableEscape("commands.item.target.no_such_slot", slot)
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(literal("penchant")
                .requires(hasPermission(LEVEL_GAMEMASTERS))
                .then(literal("add_progress")
                        .then(literal("block")
                                .then(argument("pos", blockPos())
                                        .then(argument("slot", slot())
                                                .then(argument("increase", integer())
                                                        .executes(context ->
                                                                addProgress(context.getSource(), getLoadedBlockPos(context, "pos"), getSlot(context, "slot"), getInteger(context, "increase"))
                                                        )
                                                )
                                                .executes(context ->
                                                        addProgress(context.getSource(), getLoadedBlockPos(context, "pos"), getSlot(context, "slot"), 1)
                                                )
                                        )
                                )
                        )
                        .then(literal("entity")
                                .then(argument("entity", entity())
                                        .then(argument("slot", slot())
                                                .then(argument("increase", integer())
                                                        .executes(context ->
                                                                addProgress(context.getSource(), getEntity(context, "entity"), getSlot(context, "slot"), getInteger(context, "increase"))
                                                        )
                                                )
                                                .executes(context ->
                                                        addProgress(context.getSource(), getEntity(context, "entity"), getSlot(context, "slot"), 1)
                                                )
                                        )
                                )
                        )
                )
        );

    }

    private static int addProgress(CommandSourceStack source, Entity entity, int slot, int increase) throws CommandSyntaxException {
        SlotAccess slotAccess = entity.getSlot(slot);
        if (slotAccess == null) throw ERROR_TARGET_INAPPLICABLE_SLOT.create(slot);

        var stack = slotAccess.get();
        EnchantmentProgress.addToProgress(stack, increase, null);
        slotAccess.set(stack);

        if (entity instanceof ServerPlayer serverPlayer)
            serverPlayer.containerMenu.broadcastChanges();

        source.sendSuccess(() -> Component.translatable("commands.penchant.add_progress.success", increase), true);
        return 1;

    }

    private static int addProgress(CommandSourceStack source, BlockPos pos, int slot, int increase) throws CommandSyntaxException {
        var container = getContainer(source, pos, ERROR_TARGET_NOT_A_CONTAINER);
        if (slot < 0 || slot >= container.getContainerSize())
            throw ERROR_TARGET_INAPPLICABLE_SLOT.create(slot);

        var stack = container.getItem(slot);
        EnchantmentProgress.addToProgress(stack, increase, null);
        container.setItem(slot, stack);

        source.sendSuccess(() -> Component.translatable("commands.penchant.add_progress.success", increase), true);
        return 1;
    }

    private static Container getContainer(final CommandSourceStack source, final BlockPos pos, final Dynamic3CommandExceptionType exceptionType) throws CommandSyntaxException {
        if (source.getLevel().getBlockEntity(pos) instanceof Container container) {
            return container;
        } else {
            throw exceptionType.create(pos.getX(), pos.getY(), pos.getZ());
        }
    }

}
