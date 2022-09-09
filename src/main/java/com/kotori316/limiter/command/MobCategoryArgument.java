package com.kotori316.limiter.command;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;

import com.kotori316.limiter.LimitMobSpawn;

public final class MobCategoryArgument implements ArgumentType<MobCategory> {
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType(object -> Component.literal("Bad MobCategory type. %s".formatted(object)));

    public static MobCategoryArgument argument() {
        return new MobCategoryArgument();
    }

    public static void registerArgumentType() {
        ArgumentTypeRegistry.registerArgumentType(new ResourceLocation(LimitMobSpawn.MOD_ID, "mob_category"),
            MobCategoryArgument.class, SingletonArgumentInfo.contextFree(MobCategoryArgument::argument));
    }

    @Override
    public MobCategory parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        try {
            return MobCategory.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ERROR_INVALID_VALUE.create(e.getMessage());
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var values = Stream.of(MobCategory.values()).map(MobCategory::getName).map(String::toLowerCase);
        return SharedSuggestionProvider.suggest(values, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("monster", "creature");
    }
}
