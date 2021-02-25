package com.kotori316.limiter.conditions;

import java.util.Locale;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import com.kotori316.limiter.LimitMobSpawn;
import com.kotori316.limiter.TestSpawn;

public class SpawnReasonLimit implements TestSpawn {
    public static final TestSpawn.Serializer<SpawnReasonLimit> SERIALIZER = StringLimitSerializer.fromFunction(
        SpawnReasonLimit::getReason, SpawnReasonLimit::new, r -> r.toString().toLowerCase(Locale.ROOT),
        s -> SpawnReason.valueOf(s.toUpperCase(Locale.ROOT)), "spawn_reason", "spawn_reason"
    );
    private final SpawnReason reason;

    public SpawnReasonLimit(SpawnReason reason) {
        this.reason = reason;
        LimitMobSpawn.LOGGER.debug(TestSpawn.MARKER, getClass().getSimpleName() + " Instance created with {}", reason);
    }

    public SpawnReason getReason() {
        return reason;
    }

    @Override
    public boolean test(IBlockReader worldIn, BlockPos pos, EntityType<?> entityTypeIn, @Nullable SpawnReason reason) {
        // Pass if reason isn't available.
        return reason == null || reason == this.reason;
    }

    @Override
    public TestSpawn.Serializer<? extends TestSpawn> getSerializer() {
        return SERIALIZER;
    }

}
