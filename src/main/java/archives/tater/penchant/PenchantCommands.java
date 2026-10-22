package archives.tater.penchant;

import archives.tater.penchant.component.EnchantmentProgress;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.SlotSourceArgument;
import net.minecraft.server.commands.item.ItemAccessor;
import net.minecraft.server.commands.item.ItemCommands;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.*;
import static net.minecraft.commands.arguments.SlotSourceArgument.getSlotSource;
import static net.minecraft.commands.arguments.SlotSourceArgument.slotSource;

public class PenchantCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        var root = literal("penchant")
                .requires(hasPermission(LEVEL_GAMEMASTERS));

        for (var targetProvider : ItemCommands.TARGET_PROVIDERS) {
            root.then(targetProvider.wrap(literal("add_progress"), t -> t.then(argument("slots", slotSource(buildContext))
                    .then(argument("increase", integer())
                            .executes(context ->
                                    addProgress(context.getSource(), targetProvider.access(context), getSlotSource(context, "slots"), getInteger(context, "increase"))
                            )
                    )
                    .executes(context ->
                            addProgress(context.getSource(), targetProvider.access(context), getSlotSource(context, "slots"), 1)
                    )
            )));
        }

        dispatcher.register(root);
    }

    private static int addProgress(CommandSourceStack source, ItemAccessor<?> access, SlotSourceArgument.Result slots, int increase) throws CommandSyntaxException {
        access.getSlots(source, slots.value()).modifySlots(slotAccess -> {
            var stack = slotAccess.get();
            EnchantmentProgress.addToProgress(stack, increase, null);
        });
        return 1;
    }
}
