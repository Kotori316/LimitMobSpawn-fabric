package com.kotori316.limiter.conditions;

import com.kotori316.limiter.LimitMobSpawn;
import com.kotori316.limiter.TestSpawn;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public record BiomeLimit(@NotNull ResourceKey<Biome> biomeResourceKey) implements TestSpawn {
    public static final TestSpawn.Serializer<BiomeLimit> SERIALIZER = new BiomeSerializer();

    public BiomeLimit(@NotNull ResourceKey<Biome> biomeResourceKey) {
        this.biomeResourceKey = biomeResourceKey;
        LimitMobSpawn.LOGGER.debug(TestSpawn.MARKER, getClass().getSimpleName() + " Instance created with {}", biomeResourceKey);
    }

    public BiomeLimit(@NotNull ResourceLocation biome) {
        this(ResourceKey.create(Registries.BIOME, biome));
    }

    @Override
    public boolean test(BlockGetter worldIn, BlockPos pos, EntityType<?> entityTypeIn, @Nullable MobSpawnType reason) {
        if (worldIn instanceof ServerLevel serverLevel) {
            var biome = serverLevel.getBiome(pos);
            var name = serverLevel.registryAccess().registryOrThrow(Registries.BIOME)
                .getKey(biome.value());
            return test(name);
        }
        return false;
    }

    boolean test(ResourceLocation biome) {
        return biomeResourceKey.location().equals(biome);
    }

    @Override
    public Serializer<? extends TestSpawn> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public String contentShort() {
        return "biome " + biomeResourceKey.location();
    }

    private static final class BiomeSerializer extends StringLimitSerializer<BiomeLimit, ResourceKey<Biome>> {
        @Override
        public String getType() {
            return "biome";
        }

        @Override
        public Set<String> possibleValues(String property, boolean suggesting, @Nullable SharedSuggestionProvider provider) {
            return suggestions(property, provider).stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.toSet());
        }

        @Override
        public Set<ResourceLocation> suggestions(String property, @Nullable SharedSuggestionProvider provider) {
            if (provider == null || !property.equals(saveKey()))
                return Collections.emptySet();
            return provider.registryAccess()
                .registryOrThrow(Registries.BIOME)
                .keySet();
        }

        @Override
        public ResourceKey<Biome> fromString(String s) {
            return ResourceKey.create(Registries.BIOME, new ResourceLocation(s));
        }

        @Override
        public String valueToString(ResourceKey<Biome> biomeResourceKey) {
            return biomeResourceKey.location().toString();
        }

        @Override
        public String saveKey() {
            return "biome";
        }

        @Override
        public BiomeLimit instance(ResourceKey<Biome> biomeResourceKey) {
            return new BiomeLimit(biomeResourceKey);
        }

        @Override
        public ResourceKey<Biome> getter(BiomeLimit dimensionLimit) {
            return dimensionLimit.biomeResourceKey;
        }
    }
}
