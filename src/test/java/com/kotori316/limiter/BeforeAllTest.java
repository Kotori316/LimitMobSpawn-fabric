package com.kotori316.limiter;

import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BeforeAllTest {
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    @BeforeAll
    static void beforeAll() {
        BeforeAllTest.initialize();
    }

    public static synchronized void initialize() {
        if (!INITIALIZED.getAndSet(true)) {
            SharedConstants.tryDetectVersion();
            initLoader();
            changeDist();
            setHandler();
            Bootstrap.bootStrap();
        }
    }

    private static void changeDist() {
    }

    private static void setHandler() {
    }

    private static void initLoader() {
    }

    protected static void testCycle(TestSpawn limit) {
        assertAll(
            () -> assertEquals(limit, SpawnConditionLoader.INSTANCE.deserialize(new Dynamic<>(JsonOps.INSTANCE, limit.to(JsonOps.INSTANCE)))),
            () -> assertEquals(limit, SpawnConditionLoader.INSTANCE.deserialize(new Dynamic<>(JsonOps.COMPRESSED, limit.to(JsonOps.COMPRESSED)))),
            () -> assertEquals(limit, SpawnConditionLoader.INSTANCE.deserialize(new Dynamic<>(NbtOps.INSTANCE, limit.to(NbtOps.INSTANCE))))
        );
    }

}
