package com.kotori316.limiter.conditions;

import com.kotori316.limiter.BeforeAllTest;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DimensionLimitTest extends BeforeAllTest {
    static Stream<ResourceKey<Level>> registryKeys() {
        return Stream.of(
            Level.OVERWORLD,
            Level.NETHER,
            Level.END,
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation("mining_dimension:mining")),
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation("kick:gaia_dimension45"))
        );
    }

    @Test
    void dummy() {
        assertTrue(registryKeys().findAny().isPresent());
    }

    @ParameterizedTest
    @MethodSource("registryKeys")
    void serialize(ResourceKey<Level> worldResourceKey) {
        DimensionLimit limit = new DimensionLimit(worldResourceKey);
        assertAll(
            () -> assertDoesNotThrow(() -> limit.to(JsonOps.INSTANCE)),
            () -> assertDoesNotThrow(() -> limit.to(JsonOps.COMPRESSED)),
            () -> assertDoesNotThrow(() -> limit.to(NbtOps.INSTANCE))
        );
    }

    @ParameterizedTest
    @MethodSource("registryKeys")
    void cycleConsistency(ResourceKey<Level> worldResourceKey) {
        DimensionLimit limit = new DimensionLimit(worldResourceKey);
        testCycle(limit);
    }
}
