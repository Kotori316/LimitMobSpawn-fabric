package com.kotori316.limiter.command;

import java.util.Set;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import com.kotori316.limiter.LimitMobSpawn;
import com.kotori316.limiter.TestSpawn;
import com.kotori316.limiter.capability.Caps;
import com.kotori316.limiter.capability.LMSHandler;

public class LMSCommand {
    private static final SimpleCommandExceptionType HandlersNotFound = new SimpleCommandExceptionType(new StringTextComponent("No LMS Handlers found."));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> literal = Commands.literal(LimitMobSpawn.MOD_ID);
        {
            // query
            LiteralArgumentBuilder<CommandSource> query = Commands.literal("query");
            query.then(Commands.literal("default").executes(context -> {
                LMSHandler lmsHandler = getLmsHandler(context);
                sendMessage(context, "Defaults", lmsHandler.getDefaultConditions());
                return Command.SINGLE_SUCCESS;
            }));
            query.then(Commands.literal("deny").executes(context -> {
                LMSHandler lmsHandler = getLmsHandler(context);
                sendMessage(context, "Denies", lmsHandler.getDenyConditions());
                return Command.SINGLE_SUCCESS;
            }));
            query.then(Commands.literal("force").executes(context -> {
                LMSHandler lmsHandler = getLmsHandler(context);
                sendMessage(context, "Forces", lmsHandler.getForceConditions());
                return Command.SINGLE_SUCCESS;
            }));
            literal.then(query
                .executes(context -> {
                    LMSHandler lmsHandler = getLmsHandler(context);
                    if (lmsHandler.getDefaultConditions().isEmpty() && lmsHandler.getForceConditions().isEmpty() && lmsHandler.getDenyConditions().isEmpty())
                        context.getSource().sendErrorMessage(new StringTextComponent("No Rules found."));
                    sendMessage(context, "Defaults", lmsHandler.getDefaultConditions());
                    sendMessage(context, "Denies", lmsHandler.getDenyConditions());
                    sendMessage(context, "Forces", lmsHandler.getForceConditions());
                    return Command.SINGLE_SUCCESS;
                }));
        }
        {
            // add
            LiteralArgumentBuilder<CommandSource> add = Commands.literal("add");
            add.then(Commands.literal("default").then(Commands.argument("rule", new TestSpawnArgument()).executes(context -> {
                LMSHandler lmsHandler = getLmsHandler(context);
                TestSpawn rule = context.getArgument("rule", TestSpawn.class);
                lmsHandler.addDefaultCondition(rule);
                context.getSource().sendFeedback(new StringTextComponent("Added " + rule + " to default."), true);
                return Command.SINGLE_SUCCESS;
            })));
            add.then(Commands.literal("deny").then(Commands.argument("rule", new TestSpawnArgument()).executes(context -> {
                LMSHandler lmsHandler = getLmsHandler(context);
                TestSpawn rule = context.getArgument("rule", TestSpawn.class);
                lmsHandler.addDenyCondition(rule);
                context.getSource().sendFeedback(new StringTextComponent("Added " + rule + " to deny."), true);
                return Command.SINGLE_SUCCESS;
            })));
            add.then(Commands.literal("force").then(Commands.argument("rule", new TestSpawnArgument()).executes(context -> {
                LMSHandler lmsHandler = getLmsHandler(context);
                TestSpawn rule = context.getArgument("rule", TestSpawn.class);
                lmsHandler.addForceCondition(rule);
                context.getSource().sendFeedback(new StringTextComponent("Added " + rule + " to force."), true);
                return Command.SINGLE_SUCCESS;
            })));
            literal.then(add);
        }
        dispatcher.register(literal);
    }

    private static void sendMessage(CommandContext<CommandSource> context, String s, Set<TestSpawn> conditions) {
        context.getSource().sendFeedback(new StringTextComponent(s + "=" + conditions.size()), true);
        conditions.stream().map(Object::toString).map(StringTextComponent::new).forEach(c -> context.getSource().sendFeedback(c, true));
    }

    @Nonnull
    private static LMSHandler getLmsHandler(CommandContext<CommandSource> context) throws CommandSyntaxException {
        World world = context.getSource().getWorld();
        return world.getCapability(Caps.getLmsCapability()).orElseThrow(HandlersNotFound::create);
    }
}
