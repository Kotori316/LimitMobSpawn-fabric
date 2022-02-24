package com.kotori316.limiter.data;

import java.util.function.Predicate;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.resources.ResourceLocation;

import com.kotori316.limiter.LimitMobSpawn;

public final class TrueCondition implements Predicate<JsonObject> {
    @Override
    public boolean test(JsonObject object) {
        return true;
    }

    public static ResourceLocation getConditionId() {
        return new ResourceLocation(LimitMobSpawn.MOD_ID, "true_condition");
    }

    public static final class Provider implements ConditionJsonProvider {

        @Override
        public ResourceLocation getConditionId() {
            return TrueCondition.getConditionId();
        }

        @Override
        public void writeParameters(JsonObject object) {
        }
    }
}
