package com.kotori316.limiter.conditions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;

import com.kotori316.limiter.LimitMobSpawn;
import com.kotori316.limiter.TestSpawn;

public class And implements TestSpawn {
    public static final TestSpawn.Serializer<And> SERIALIZER = new Serializer();
    private final TestSpawn t1;
    private final List<TestSpawn> ts;
    private final boolean deterministic;

    public And(TestSpawn t1, TestSpawn... ts) {
        this.t1 = t1;
        this.ts = Arrays.asList(ts);
        LimitMobSpawn.LOGGER.debug(TestSpawn.MARKER, getClass().getSimpleName() + " Instance created with {}, {}", t1, ts);
        deterministic = t1.isDeterministic() && Arrays.stream(ts).allMatch(TestSpawn::isDeterministic);
    }

    public And(List<TestSpawn> list) {
        this.t1 = list.get(0);
        this.ts = list.subList(1, list.size());
        LimitMobSpawn.LOGGER.debug(TestSpawn.MARKER, getClass().getSimpleName() + " Instance created with {}", list);
        deterministic = t1.isDeterministic() && ts.stream().allMatch(TestSpawn::isDeterministic);
    }

    @Override
    public boolean test(BlockGetter worldIn, BlockPos pos, EntityType<?> entityTypeIn, MobSpawnType reason) {
        return t1.test(worldIn, pos, entityTypeIn, reason) && ts.stream().allMatch(t -> t.test(worldIn, pos, entityTypeIn, reason));
    }

    @Override
    public TestSpawn and(TestSpawn other) {
        TestSpawn[] ts = this.ts.toArray(new TestSpawn[this.ts.size() + 1]);
        ts[this.ts.size()] = other;
        return new And(this.t1, ts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        And and = (And) o;
        return Objects.equals(t1, and.t1) && Objects.equals(ts, and.ts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, ts);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("And{t1=" + t1);
        for (int i = 0; i < ts.size(); i++) {
            builder.append(", t").append(i + 2).append('=').append(ts.get(i));
        }
        builder.append('}');
        return builder.toString();
    }

    @Override
    public TestSpawn.Serializer<? extends TestSpawn> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public String contentShort() {
        StringBuilder builder = new StringBuilder("And{" + t1.contentShort());
        for (TestSpawn t : ts) {
            builder.append(", ").append(t.contentShort());
        }
        builder.append('}');
        return builder.toString();
    }

    @Override
    public boolean isDeterministic() {
        return deterministic;
    }

    private static class Serializer extends TestSpawn.Serializer<And> {

        @Override
        public String getType() {
            return "and";
        }

        @Override
        public <T> And from(Dynamic<T> dynamic) {
            return getCombinationFrom(dynamic, And::new);
        }

        @Override
        public <T> T to(TestSpawn t, DynamicOps<T> ops) {
            And and = (And) t;
            return ops.createMap(writeMap(ops, Stream.concat(Stream.of(and.t1), and.ts.stream())));
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
