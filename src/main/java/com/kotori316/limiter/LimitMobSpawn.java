package com.kotori316.limiter;

import java.util.Optional;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import com.kotori316.limiter.capability.CapsSaveData;
import com.kotori316.limiter.capability.LMSHandler;
import com.kotori316.limiter.command.LMSCommand;
import com.kotori316.limiter.command.TestSpawnArgument;
import com.kotori316.limiter.data.TrueCondition;

public class LimitMobSpawn implements ModInitializer {
    public static final String MOD_ID = "limitmobspawn";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Level LOG_LEVEL = Boolean.getBoolean("limit-mob-spawn") ? Level.DEBUG : Level.TRACE;
    public static final Marker LMS_MARKER = MarkerManager.getMarker("LMS");

    public LimitMobSpawn() {
    }

    @Override
    public void onInitialize() {
        ResourceConditions.register(TrueCondition.getConditionId(), new TrueCondition());
        TestSpawnArgument.registerArgumentType();
        addLister();
        addCommand();
    }

    public void addLister() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(SpawnConditionLoader.INSTANCE);
    }

    public void addCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> LMSCommand.register(dispatcher));
    }

    public static SpawnCheckResult allowSpawning(BlockGetter worldIn, BlockPos pos,
                                                 EntityType<?> entityTypeIn, @Nullable MobSpawnType reason) {
        Optional<LMSHandler> maybeHandler = worldIn instanceof ServerLevel s ? CapsSaveData.getFromWorld(s) : Optional.empty();

        boolean matchForce = LMSHandler.getCombinedForce(SpawnConditionLoader.INSTANCE.getHolder(), maybeHandler).filter(TestSpawn::isDeterministic)
            .anyMatch(spawn -> spawn.test(worldIn, pos, entityTypeIn, reason));
        if (matchForce) return SpawnCheckResult.FORCE;
        boolean matchDefault = LMSHandler.getCombinedDefault(SpawnConditionLoader.INSTANCE.getHolder(), maybeHandler).filter(TestSpawn::isDeterministic)
            .anyMatch(spawn -> spawn.test(worldIn, pos, entityTypeIn, reason));
        if (matchDefault) return SpawnCheckResult.DEFAULT;
        boolean matchDeny = LMSHandler.getCombinedDeny(SpawnConditionLoader.INSTANCE.getHolder(), maybeHandler).filter(TestSpawn::isDeterministic)
            .anyMatch(spawn -> spawn.test(worldIn, pos, entityTypeIn, reason));
        if (matchDeny) return SpawnCheckResult.DENY;
        else return SpawnCheckResult.DEFAULT;
    }

    public enum SpawnCheckResult {
        DENY, DEFAULT, FORCE
    }
}
