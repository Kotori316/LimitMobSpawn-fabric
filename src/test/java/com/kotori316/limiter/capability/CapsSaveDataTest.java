package com.kotori316.limiter.capability;

import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import com.kotori316.limiter.BeforeAllTest;
import com.kotori316.limiter.conditions.All;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CapsSaveDataTest extends BeforeAllTest {
    @Test
    void createInstance1() {
        var i = new CapsSaveData(new LMSDataPackHolder());
        assertEquals(LMSDataPackHolder.class, i.getHandler().getClass());
    }

    @Test
    void createInstance2() {
        var i = new CapsSaveData(new LMSConditionsHolder());
        assertEquals(LMSConditionsHolder.class, i.getHandler().getClass());
    }

    @Test
    void createInstance3() {
        var i = new CapsSaveData(new CompoundTag());
        assertEquals(LMSConditionsHolder.class, i.getHandler().getClass());
    }

    @Test
    void serialize1() {
        var i = new CapsSaveData(new LMSConditionsHolder());
        var tag = i.save(new CompoundTag());
        assertFalse(tag.isEmpty());
        assertTrue(tag.contains("handler"));
    }

    @Test
    void notDirtyBeforeAdding() {
        var i = new CapsSaveData(new LMSConditionsHolder());
        assertFalse(i.isDirty());
    }

    @Test
    void dirtyAfterAdding() {
        var i = new CapsSaveData(new LMSConditionsHolder());
        i.getHandler().addDefaultCondition(All.getInstance());
        assertTrue(i.isDirty());
    }

    @Test
    void cycle1() {
        var i = new CapsSaveData(new LMSConditionsHolder());
        i.getHandler().addDefaultCondition(All.getInstance());

        var i2 = new CapsSaveData(i.save(new CompoundTag()));
        assertEquals(Set.of(All.getInstance()), i2.getHandler().getDefaultConditions());
    }
}
