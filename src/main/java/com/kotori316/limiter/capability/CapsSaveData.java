package com.kotori316.limiter.capability;

import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import com.kotori316.limiter.LimitMobSpawn;

public final class CapsSaveData extends SavedData {
    private static final String NBT_KEY = "handler";
    final LMSHandler handler;

    public CapsSaveData(LMSHandler handler) {
        this.handler = handler;
        this.handler.setSaveCallBack(l -> this.setDirty());
    }

    public CapsSaveData(CompoundTag tag) {
        this(new LMSConditionsHolder());
        this.handler.deserializeNBT(tag.getCompound(NBT_KEY));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put(NBT_KEY, this.handler.serializeNBT());
        return compoundTag;
    }

    public LMSHandler getHandler() {
        return handler;
    }

    public static Optional<LMSHandler> getFromWorld(ServerLevel level) {
        return Optional.of(level.getDataStorage()
                .computeIfAbsent(CapsSaveData::new, () -> new CapsSaveData(new LMSConditionsHolder()), LimitMobSpawn.MOD_ID + "_handler"))
            .map(CapsSaveData::getHandler);
    }

    public static Optional<LMSHandler> getFromWorld(Level level) {
        if (level instanceof ServerLevel serverLevel) return getFromWorld(serverLevel);
        else return Optional.empty();
    }
}
