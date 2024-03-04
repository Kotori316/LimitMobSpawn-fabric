package com.kotori316.limiter.data;

import com.google.gson.JsonElement;
import com.kotori316.limiter.LimitMobSpawn;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class LimitMobSpawnDataProvider implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.createPack().addProvider(TestSpawnProvider::new);
    }

    private record TestSpawnProvider(FabricDataOutput dataGenerator) implements DataProvider {

        @Override
        @NotNull
        public CompletableFuture<?> run(CachedOutput cachedOutput) {
            Path parent = dataGenerator.getOutputFolder().resolve("data/" + LimitMobSpawn.MOD_ID + "/" + LimitMobSpawn.MOD_ID);
            return CompletableFuture.allOf(
                getData().stream()
                    .map(pair -> DataProvider.saveStable(cachedOutput, pair.getRight(), parent.resolve(pair.getLeft() + ".json")))
                    .toArray(CompletableFuture[]::new)
            );
        }

        @Override
        @NotNull
        public String getName() {
            return getClass().getName();
        }

        private List<Pair<String, JsonElement>> getData() {
            List<Pair<String, JsonElement>> list = new ArrayList<>();
            Rules.addAll(list);
            return list;
        }
    }
}
