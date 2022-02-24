package com.kotori316.limiter.capability;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.kotori316.limiter.TestSpawn;

public class LMSConditionsHolder implements LMSHandler {
    private final Set<TestSpawn> defaultConditions = new HashSet<>(), denyConditions = new HashSet<>(), forceConditions = new HashSet<>();
    private final SpawnerControl spawnerControl = new SpawnerControl();
    private Consumer<LMSHandler> saveCallBack;

    @Override
    public void addDefaultCondition(TestSpawn condition) {
        defaultConditions.add(condition);
        save();
    }

    @Override
    public void addDenyCondition(TestSpawn condition) {
        denyConditions.add(condition);
        save();
    }

    @Override
    public void addForceCondition(TestSpawn condition) {
        forceConditions.add(condition);
        save();
    }

    @Override
    public Set<TestSpawn> getDefaultConditions() {
        return defaultConditions;
    }

    @Override
    public Set<TestSpawn> getDenyConditions() {
        return denyConditions;
    }

    @Override
    public Set<TestSpawn> getForceConditions() {
        return forceConditions;
    }

    @Override
    public void clearDefaultConditions() {
        defaultConditions.clear();
        save();
    }

    @Override
    public void clearDenyConditions() {
        denyConditions.clear();
        save();
    }

    @Override
    public void clearForceConditions() {
        forceConditions.clear();
        save();
    }

    @Override
    public SpawnerControl getSpawnerControl() {
        return spawnerControl;
    }

    @Override
    public String toString() {
        return "LMSConditionsHolder{defaultConditions: " + defaultConditions.size()
            + ", denyConditions: " + denyConditions.size()
            + ", forceConditions: " + forceConditions.size()
            + '}';
    }

    @Override
    public void setSaveCallBack(Consumer<LMSHandler> saveCallBack) {
        LMSHandler.super.setSaveCallBack(saveCallBack);
        this.saveCallBack = saveCallBack;
    }

    void save() {
        if (this.saveCallBack != null) {
            this.saveCallBack.accept(this);
        }
    }
}
