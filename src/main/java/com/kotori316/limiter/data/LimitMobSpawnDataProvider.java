package com.kotori316.limiter.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import org.apache.commons.lang3.tuple.Pair;

import com.kotori316.limiter.LimitMobSpawn;

public final class LimitMobSpawnDataProvider implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(TestSpawnProvider::new);
    }

    private record TestSpawnProvider(DataGenerator dataGenerator) implements DataProvider {

        @Override
        public void run(CachedOutput cachedOutput) throws IOException {
            Path parent = dataGenerator.getOutputFolder().resolve("data/" + LimitMobSpawn.MOD_ID + "/" + LimitMobSpawn.MOD_ID);
            for (Pair<String, JsonElement> pair : getData()) {
                DataProvider.saveStable(cachedOutput, pair.getRight(), parent.resolve(pair.getLeft() + ".json"));
            }
        }

        @Override
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
