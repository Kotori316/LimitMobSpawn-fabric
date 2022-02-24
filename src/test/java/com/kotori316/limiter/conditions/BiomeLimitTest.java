package com.kotori316.limiter.conditions;

import java.util.Arrays;
import java.util.stream.Stream;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.kotori316.limiter.BeforeAllTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BiomeLimitTest extends BeforeAllTest {
    static final BiomeLimit NOT_EXIST_BIOME = new BiomeLimit(new ResourceLocation("lms:not_exist_biome"));

    @SuppressWarnings("unchecked")
    static Stream<ResourceKey<Biome>> registeredBiomes() {
        return Arrays.stream(Biomes.class.getFields())
            .filter(f -> f.getType() == ResourceKey.class)
            .map(f -> {
                try {
                    return f.get(null);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            })
            .map(o -> (ResourceKey<Biome>) o);
    }

    @Test
    void dummy() {
        assertTrue(registeredBiomes().findAny().isPresent());
    }

    @ParameterizedTest
    @MethodSource("registeredBiomes")
    void testValid(ResourceKey<Biome> key) {
        BiomeLimit limit = new BiomeLimit(key);
        assertAll(
            () -> assertTrue(limit.test(key.location())),
            () -> assertFalse(NOT_EXIST_BIOME.test(key.location()))
        );
    }

    @ParameterizedTest
    @MethodSource("registeredBiomes")
    void cycle(ResourceKey<Biome> key) {
        BiomeLimit limit = new BiomeLimit(key);
        testCycle(limit);
        assertEquals(limit, new BiomeLimit(key.location()));
    }
}
