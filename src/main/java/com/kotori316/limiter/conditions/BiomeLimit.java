package com.kotori316.limiter.conditions;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;

import com.kotori316.limiter.LimitMobSpawn;
import com.kotori316.limiter.TestSpawn;

public class BiomeLimit implements TestSpawn {
    public static final TestSpawn.Serializer<BiomeLimit> SERIALIZER = new BiomeSerializer();
    @Nonnull
    private final ResourceKey<Biome> biomeResourceKey;

    public BiomeLimit(@Nonnull ResourceKey<Biome> biomeResourceKey) {
        this.biomeResourceKey = biomeResourceKey;
        LimitMobSpawn.LOGGER.debug(TestSpawn.MARKER, getClass().getSimpleName() + " Instance created with {}", biomeResourceKey);
    }

    public BiomeLimit(@Nonnull ResourceLocation biome) {
        this(ResourceKey.create(Registry.BIOME_REGISTRY, biome));
    }

    @Override
    public boolean test(BlockGetter worldIn, BlockPos pos, EntityType<?> entityTypeIn, @Nullable MobSpawnType reason) {
        if (worldIn instanceof LevelReader) {
            LevelReader worldReader = (LevelReader) worldIn;
            Biome biome = worldReader.getBiome(pos);
            return test(biome);
        }
        return false;
    }

    public boolean test(Biome biome) {
        return biomeResourceKey.location().equals(biome.getRegistryName());
    }

    @Override
    public String toString() {
        return "BiomeLimit{" +
            "biome=" + biomeResourceKey +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiomeLimit that = (BiomeLimit) o;
        return Objects.equals(biomeResourceKey, that.biomeResourceKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(biomeResourceKey);
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
            if (provider == null || !property.equals(saveKey()))
                return Collections.emptySet();
            return provider.registryAccess()
                .registryOrThrow(Registry.BIOME_REGISTRY)
                .keySet()
                .stream()
                .map(ResourceLocation::toString)
                .collect(Collectors.toSet());
        }

        @Override
        public ResourceKey<Biome> fromString(String s) {
            return ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(s));
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
