package com.kotori316.limiter.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Sets;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import com.kotori316.limiter.Config;
import com.kotori316.limiter.LimitMobSpawn;
import com.kotori316.limiter.SpawnConditionLoader;
import com.kotori316.limiter.TestSpawn;
import com.kotori316.limiter.capability.CapsSaveData;
import com.kotori316.limiter.capability.LMSConditionsHolder;
import com.kotori316.limiter.capability.LMSHandler;
import com.kotori316.limiter.capability.RuleType;

public class LMSCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literal = Commands.literal(LimitMobSpawn.MOD_ID);
        {
            // query
            LiteralArgumentBuilder<CommandSourceStack> query = Commands.literal("query");

            LiteralArgumentBuilder<CommandSourceStack> world = Commands.literal("world");
            registerQuery(world, LMSCommand::getLmsHandler);
            query.then(world);
            LiteralArgumentBuilder<CommandSourceStack> dataPack = Commands.literal("datapack");
            registerQuery(dataPack, c -> SpawnConditionLoader.INSTANCE.getHolder());
            query.then(dataPack);

            literal.then(query
                .executes(context -> {
                    LMSHandler lmsHandler = getLmsHandler(context);
                    if (lmsHandler.getDefaultConditions().isEmpty() && lmsHandler.getForceConditions().isEmpty() && lmsHandler.getDenyConditions().isEmpty())
                        context.getSource().sendFailure(Component.literal("No Rules found."));
                    sendMessage(context, "Defaults", Sets.union(lmsHandler.getDefaultConditions(), SpawnConditionLoader.INSTANCE.getHolder().getDefaultConditions()));
                    sendMessage(context, "Denies", Sets.union(lmsHandler.getDenyConditions(), SpawnConditionLoader.INSTANCE.getHolder().getDenyConditions()));
                    sendMessage(context, "Forces", Sets.union(lmsHandler.getForceConditions(), SpawnConditionLoader.INSTANCE.getHolder().getForceConditions()));
                    return Command.SINGLE_SUCCESS;
                }));
        }
        {
            // add
            LiteralArgumentBuilder<CommandSourceStack> add = Commands.literal("add").requires(s -> s.hasPermission(Config.getInstance().getPermission()));
            for (RuleType ruleType : RuleType.values()) {
                add.then(Commands.literal(ruleType.saveName()).then(Commands.argument("rule", new TestSpawnArgument()).executes(context -> {
                    List<LMSHandler> list = getAllLmsHandlers(context);
                    TestSpawn rule = context.getArgument("rule", TestSpawn.class);
                    list.forEach(lmsHandler -> ruleType.add(lmsHandler, rule));
                    context.getSource().sendSuccess(Component.literal(String.format("Added %s to %s.", rule, ruleType.saveName())), true);
                    return Command.SINGLE_SUCCESS;
                })));
            }
            literal.then(add);
        }
        {
            // remove
            LiteralArgumentBuilder<CommandSourceStack> remove = Commands.literal("remove").requires(s -> s.hasPermission(Config.getInstance().getPermission()));
            for (RuleType ruleType : RuleType.values()) {
                remove.then(Commands.literal(ruleType.saveName()).executes(context -> {
                    getAllLmsHandlers(context).forEach(ruleType::removeAll);
                    context.getSource().sendSuccess(Component.literal("Cleared " + ruleType.getCommandName()), true);
                    return Command.SINGLE_SUCCESS;
                }));
            }
            literal.then(remove);
        }
        {
            // Spawner
            LiteralArgumentBuilder<CommandSourceStack> spawner = Commands.literal("spawner");
            spawner.then(Commands.literal("spawnCount")
                .requires(s -> s.hasPermission(Config.getInstance().getPermission()))
                .then(Commands.argument("spawnCount", IntegerArgumentType.integer(0)).executes(context -> {
                    Integer spawnCount = context.getArgument("spawnCount", Integer.class);
                    getAllLmsHandlers(context).forEach(l -> l.getSpawnerControl().setSpawnCount(spawnCount));
                    context.getSource().sendSuccess(Component.literal("Changed spawnCount to " + spawnCount), true);
                    return Command.SINGLE_SUCCESS;
                })));
            spawner.then(Commands.literal("query").executes(context -> {
                LMSHandler lmsHandler = getLmsHandler(context);
                lmsHandler.getSpawnerControl().getMessages().forEach(s -> context.getSource().sendSuccess(s, true));
                return Command.SINGLE_SUCCESS;
            }));
            literal.then(spawner);
        }
        dispatcher.register(literal);
    }

    private static void registerQuery(LiteralArgumentBuilder<CommandSourceStack> parent, Function<CommandContext<CommandSourceStack>, LMSHandler> getter) {
        for (RuleType ruleType : RuleType.values()) {
            parent.then(Commands.literal(ruleType.saveName()).executes(context -> {
                LMSHandler lmsHandler = getter.apply(context);
                sendMessage(context, ruleType.getCommandName(), ruleType.getRules(lmsHandler));
                return Command.SINGLE_SUCCESS;
            }));
        }
        parent.executes(context -> {
            LMSHandler lmsHandler = getter.apply(context);
            for (RuleType ruleType : RuleType.values()) {
                sendMessage(context, ruleType.getCommandName(), ruleType.getRules(lmsHandler));
            }
            return Command.SINGLE_SUCCESS;
        });
    }

    private static void sendMessage(CommandContext<CommandSourceStack> context, String s, Set<TestSpawn> conditions) {
        context.getSource().sendSuccess(Component.literal(s + "=" + conditions.size()), true);
        conditions.stream().map(Object::toString).map(Component::literal).forEach(c -> context.getSource().sendSuccess(c, true));
    }

    @NotNull
    private static LMSHandler getLmsHandler(CommandContext<CommandSourceStack> context) {
        Level world = context.getSource().getLevel();
        return CapsSaveData.getFromWorld(world).orElseGet(LMSConditionsHolder::new);
    }

    private static List<LMSHandler> getAllLmsHandlers(CommandContext<CommandSourceStack> context) {
        List<LMSHandler> list = new ArrayList<>();
        for (Level world : context.getSource().getServer().getAllLevels()) {
            CapsSaveData.getFromWorld(world).ifPresent(list::add);
        }
        return list;
    }
}
