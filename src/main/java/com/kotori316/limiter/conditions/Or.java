package com.kotori316.limiter.conditions;

import java.util.Objects;

import com.google.gson.JsonObject;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import com.kotori316.limiter.SpawnConditionLoader;
import com.kotori316.limiter.TestSpawn;

public class Or implements TestSpawn {
    public static final TestSpawn.Serializer<Or> SERIALIZER = new Serializer();
    private final TestSpawn t1, t2;

    public Or(TestSpawn t1, TestSpawn t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public boolean test(EntitySpawnPlacementRegistry.PlacementType placeType, IWorldReader worldIn, BlockPos pos, EntityType<?> entityTypeIn) {
        return t1.test(placeType, worldIn, pos, entityTypeIn) || t2.test(placeType, worldIn, pos, entityTypeIn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Or or = (Or) o;
        return Objects.equals(t1, or.t1) && Objects.equals(t2, or.t2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, t2);
    }

    @Override
    public String toString() {
        return "Or{" +
            "t1=" + t1 +
            ", t2=" + t2 +
            '}';
    }

    @Override
    public TestSpawn.Serializer<? extends TestSpawn> getSerializer() {
        return SERIALIZER;
    }

    private static class Serializer extends TestSpawn.Serializer<Or> {

        @Override
        public String getType() {
            return "or";
        }

        @Override
        public Or fromJson(JsonObject object) {
            TestSpawn t1 = SpawnConditionLoader.INSTANCE.deserialize(object.getAsJsonObject("t1"));
            TestSpawn t2 = SpawnConditionLoader.INSTANCE.deserialize(object.getAsJsonObject("t2"));
            return new Or(t1, t2);
        }

        @Override
        public JsonObject toJson(TestSpawn t) {
            Or and = (Or) t;
            JsonObject object = new JsonObject();
            object.add("t1", and.t1.toJson());
            object.add("t2", and.t2.toJson());
            return object;
        }
    }
}
