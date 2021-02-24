package com.kotori316.limiter.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import org.apache.commons.lang3.tuple.Pair;

import com.kotori316.limiter.LimitMobSpawn;
import com.kotori316.limiter.conditions.All;
import com.kotori316.limiter.conditions.Creator;
import com.kotori316.limiter.conditions.DimensionLimit;
import com.kotori316.limiter.conditions.EntityClassificationLimit;
import com.kotori316.limiter.conditions.EntityLimit;
import com.kotori316.limiter.conditions.SpawnReasonLimit;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = LimitMobSpawn.MOD_ID)
public class LimitMobSpawnDataProvider {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        if (event.includeServer()) {
            event.getGenerator().addProvider(new TestSpawnProvider(event.getGenerator()));
        }
    }

    private static class TestSpawnProvider implements IDataProvider {
        private final DataGenerator dataGenerator;

        private TestSpawnProvider(DataGenerator dataGenerator) {
            this.dataGenerator = dataGenerator;
        }

        @Override
        public void act(DirectoryCache cache) throws IOException {
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            Path parent = dataGenerator.getOutputFolder().resolve("data/" + LimitMobSpawn.MOD_ID + "/" + LimitMobSpawn.MOD_ID);
            for (Pair<String, JsonElement> pair : getData()) {
                IDataProvider.save(gson, cache, pair.getRight(), parent.resolve(pair.getLeft() + ".json"));
            }
        }

        @Override
        public String getName() {
            return getClass().getName();
        }

        private List<Pair<String, JsonElement>> getData() {
            List<Pair<String, JsonElement>> list = new ArrayList<>();
            {
                String name = "test1";
                JsonObject object = new JsonObject();
                object.addProperty("_comment", "Conditions in each category are combined by OR.");
                {
                    JsonArray defaults = new JsonArray();
                    defaults.add(Creator.entityAtDimension(World.THE_NETHER, EntityType.PIGLIN).toJson());
                    defaults.add(new EntityClassificationLimit(EntityClassification.CREATURE).or(new EntityClassificationLimit(EntityClassification.MISC)).toJson());
                    defaults.add(Creator.posAtDimension(World.THE_END, -500, 500, -500, 500).toJson());
                    object.add("default", defaults);
                }
                {
                    JsonArray denies = new JsonArray();
                    denies.add(new DimensionLimit(World.OVERWORLD).toJson());
                    denies.add(new DimensionLimit(World.THE_NETHER).toJson());
                    denies.add(new DimensionLimit(World.THE_END).toJson());
                    denies.add(new EntityLimit(EntityType.BAT).toJson());
                    object.add("deny", denies);
                }
                {
                    JsonArray conditions = new JsonArray();
                    conditions.add(TrueCondition.Serializer.INSTANCE.getJson(TrueCondition.INSTANCE));
                    object.add("conditions", conditions);
                }
                list.add(Pair.of(name, object));
            }
            {
                String name = "test3";
                JsonObject object = new JsonObject();
                object.addProperty("_comment", "Conditions in each category are combined by OR.");
                {
                    JsonArray defaults = new JsonArray();
                    defaults.add(new SpawnReasonLimit(SpawnReason.SPAWNER).toJson());
                    defaults.add(new SpawnReasonLimit(SpawnReason.SPAWN_EGG).toJson());
                    object.add("default", defaults);
                }
                {
                    JsonArray denies = new JsonArray();
                    denies.add(All.getInstance().toJson());
                    object.add("deny", denies);
                }
                {
                    JsonArray force = new JsonArray();
                    force.add(Creator.posAtDimension(World.OVERWORLD, -64, 64, -64, 64)
                        .and(new EntityLimit(EntityType.ENDERMAN)).toJson());
                    object.add("force", force);
                }
                {
                    JsonArray conditions = new JsonArray();
                    conditions.add(TrueCondition.Serializer.INSTANCE.getJson(TrueCondition.INSTANCE));
                    object.add("conditions", conditions);
                }
                list.add(Pair.of(name, object));
            }
            {
                String name = "peaceful";
                JsonObject object = new JsonObject();
                {
                    JsonArray denies = new JsonArray();
                    denies.add(new EntityClassificationLimit(EntityClassification.MONSTER).toJson());
                    object.add("deny", denies);
                }
                {
                    JsonArray conditions = new JsonArray();
                    conditions.add(TrueCondition.Serializer.INSTANCE.getJson(TrueCondition.INSTANCE));
                    object.add("conditions", conditions);
                }
                list.add(Pair.of(name, object));
            }
            return list;
        }
    }
}
