package com.kotori316.limiter.capability;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;

public class SpawnerControl {
    public static final String KEY_SPAWN_COUNT = "spawnCount";
    private int spawnCount = 0;

    public SpawnerControl() {
    }

    public Optional<Integer> getSpawnCount() {
        return spawnCount > 0 ? Optional.of(spawnCount) : Optional.empty();
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(KEY_SPAWN_COUNT, spawnCount);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        setSpawnCount(nbt.getInt(KEY_SPAWN_COUNT));
    }

    @Override
    public String toString() {
        return "SpawnerControl{" +
            "spawnCount=" + spawnCount +
            '}';
    }

    public List<TextComponent> getMessages() {
        return Arrays.asList(
            new TextComponent("SpawnerControl"),
            new TextComponent("SpawnCount: " + getSpawnCount())
        );
    }
}
