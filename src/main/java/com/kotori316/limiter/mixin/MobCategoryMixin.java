package com.kotori316.limiter.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kotori316.limiter.capability.CapsSaveData;
import com.kotori316.limiter.capability.LMSHandler;

@Mixin(MobCategory.class)
public final class MobCategoryMixin {

    @SuppressWarnings("deprecation")
    @Inject(method = "getMaxInstancesPerChunk", at = @At("HEAD"), cancellable = true)
    public void getMax(CallbackInfoReturnable<Integer> cir) {
        var gameInstance = FabricLoader.getInstance().getGameInstance();
        Level level = switch (FabricLoader.getInstance().getEnvironmentType()) {
            case SERVER -> getLevelInServer(gameInstance);
            case CLIENT -> getLevelInClient(gameInstance);
        };
        var mobNumberLimit = CapsSaveData.getFromWorld(level)
            .map(LMSHandler::getMobNumberLimit)
            .orElse(null);
        if (mobNumberLimit != null) {
            var count = mobNumberLimit.getLimit((MobCategory) (Object) this);
            if (count.isPresent()) {
                cir.setReturnValue(count.getAsInt());
            }
        }
    }

    private Level getLevelInServer(Object serverInstance) {
        if (serverInstance instanceof MinecraftServer server) {
            return server.overworld();
        } else {
            return null;
        }
    }

    private Level getLevelInClient(Object clientInstance) {
        if (clientInstance instanceof Minecraft minecraft) {
            var server = minecraft.getSingleplayerServer();
            if (server != null) {
                return server.overworld();
            }
        }
        return null;
    }
}
