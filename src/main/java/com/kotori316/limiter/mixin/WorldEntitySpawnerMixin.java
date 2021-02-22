package com.kotori316.limiter.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.limiter.LimitMobSpawn;

@Mixin(WorldEntitySpawner.class)
public class WorldEntitySpawnerMixin {
    @Inject(method = "canCreatureTypeSpawnAtLocation", at = @At("HEAD"), cancellable = true)
    private static void canCreatureTypeSpawnAtLocation(
        IWorldReader worldIn, BlockPos pos,
        EntityType<?> entityTypeIn, CallbackInfoReturnable<Boolean> cir) {

        LimitMobSpawn.SpawnCheckResult checkResult = LimitMobSpawn.allowSpawning(worldIn, pos, entityTypeIn, null);
        if (checkResult == LimitMobSpawn.SpawnCheckResult.DENY)
            cir.setReturnValue(Boolean.FALSE);
        else if (checkResult == LimitMobSpawn.SpawnCheckResult.FORCE)
            cir.setReturnValue(Boolean.TRUE);
    }
}
