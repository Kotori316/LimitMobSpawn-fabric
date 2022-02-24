package com.kotori316.limiter.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.limiter.LimitMobSpawn;

import static com.kotori316.limiter.LimitMobSpawn.LMS_MARKER;

@Mixin(ServerLevel.class)
public final class ServerLevelMixin {

    /**
     * The last guard to prevent mob spawning.
     * This method is for initial spawning of Structures and other spawning such as Slime.
     */
    @Inject(method = "addEntity", cancellable = true, at = @At("HEAD"))
    private void addEntityMixin(Entity entity, CallbackInfoReturnable<Boolean> cir) {

        // MISCs(item frame, ender pearl, potion cloud, etc.) should be allowed to be spawned.
        // Boss monsters(Ender Dragon, Wither) should be spawned.
        if (entity.getType().getCategory() == MobCategory.MISC || !entity.canChangeDimensions()) {
            return;
        }
        var world = (ServerLevel) (Object) this;
        LimitMobSpawn.SpawnCheckResult checkResult = LimitMobSpawn.allowSpawning(world, entity.blockPosition(), entity.getType(), null);
        if (checkResult == LimitMobSpawn.SpawnCheckResult.DENY) {
            LimitMobSpawn.LOGGER.log(LimitMobSpawn.LOG_LEVEL, LMS_MARKER, "ServerLevelMixin denied spawning of {}.", entity);
            cir.setReturnValue(Boolean.FALSE);
        }
    }
}
