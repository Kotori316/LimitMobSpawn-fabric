package com.kotori316.limiter.conditions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;

import com.kotori316.limiter.LimitMobSpawn;
import com.kotori316.limiter.SpawnConditionLoader;
import com.kotori316.limiter.TestSpawn;

public class Not implements TestSpawn {
    public static final TestSpawn.Serializer<Not> SERIALIZER = new Serializer();
    private final TestSpawn value;

    public Not(TestSpawn value) {
        this.value = value;
        LimitMobSpawn.LOGGER.debug(TestSpawn.MARKER, getClass().getSimpleName() + " Instance created with {}", value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.kotori316.limiter.conditions.Not not = (com.kotori316.limiter.conditions.Not) o;
        return Objects.equals(value, not.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Not{" +
            "value=" + value +
            '}';
    }

    @Override
    public boolean test(BlockGetter worldIn, BlockPos pos, EntityType<?> entityTypeIn, MobSpawnType reason) {
        return !value.test(worldIn, pos, entityTypeIn, reason);
    }

    @Override
    public TestSpawn not() {
        return value;
    }

    @Override
    public TestSpawn.Serializer<? extends TestSpawn> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public String contentShort() {
        return "Not{" + value.contentShort() + '}';
    }

    private static class Serializer extends TestSpawn.Serializer<Not> {

        @Override
        public String getType() {
            return "not";
        }

        @Override
        public <T> TestSpawn from(Dynamic<T> dynamic) {
            TestSpawn t1 = SpawnConditionLoader.INSTANCE.deserialize(dynamic.get("value").orElseEmptyMap());
            return t1.not();
        }

        @Override
        public <T> T to(TestSpawn t, DynamicOps<T> ops) {
            Not not = (Not) t;
            Map<T, T> map = new HashMap<>();
            map.put(ops.createString("value"), not.value.to(ops));
            return ops.createMap(map);
        }

        @Override
        public Set<String> propertyKeys() {
            return Collections.emptySet();
        }

        @Override
        public Set<String> possibleValues(String property, boolean suggesting, SharedSuggestionProvider provider) {
            return Collections.emptySet();
        }
    }
}
