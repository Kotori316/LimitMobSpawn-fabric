package com.kotori316.limiter;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LimitMobSpawn.MOD_ID)
public class LimitMobSpawn {
    public static final String MOD_ID = "limit-mob-spawn";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static Set<TestSpawn> denySet = new HashSet<>();
    public static Set<TestSpawn> defaultSet = new HashSet<>();
    public static Set<TestSpawn> forceSet = new HashSet<>();
    public static final Level LOG_LEVEL = Level.TRACE;

    public LimitMobSpawn() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void addLister(AddReloadListenerEvent event) {
        event.addListener(SpawnConditionLoader.INSTANCE);
    }

    /**
     * The last guard to prevent mob spawning.
     * This method is for initial spawning of Structures.
     */
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        // MISCs(item frame, ender pearl, potion cloud, etc) should be allowed to be spawned.
        // Boss monsters(Ender Dragon, Wither) should be spawned.
        if (event.getEntity().getType().getClassification() == EntityClassification.MISC || !event.getEntity().isNonBoss())
            return;
        if (forceSet.stream().anyMatch(spawn -> spawn.test(event.getWorld(), event.getEntity().getPosition(), event.getEntity().getType(), null)) ||
            defaultSet.stream().anyMatch(spawn -> spawn.test(event.getWorld(), event.getEntity().getPosition(), event.getEntity().getType(), null)))
            return; // SKIP
        if (denySet.stream().anyMatch(spawn -> spawn.test(event.getWorld(), event.getEntity().getPosition(), event.getEntity().getType(), null))) {
            LOGGER.log(LOG_LEVEL, "onEntityJoinWorld denied spawning of {}({}) at {}.", event.getEntity(), event.getEntity().getType(), event.getEntity().getPosition());
            event.setCanceled(true);
        }
    }

    public static SpawnCheckResult allowSpawning(IBlockReader worldIn, BlockPos pos,
                                                 EntityType<?> entityTypeIn, @Nullable SpawnReason reason) {
        boolean matchForce = forceSet.stream().anyMatch(spawn -> spawn.test(worldIn, pos, entityTypeIn, reason));
        if (matchForce) return SpawnCheckResult.FORCE;
        boolean matchDefault = defaultSet.stream().anyMatch(spawn -> spawn.test(worldIn, pos, entityTypeIn, reason));
        if (matchDefault) return SpawnCheckResult.DEFAULT;
        boolean matchDeny = denySet.stream().anyMatch(spawn -> spawn.test(worldIn, pos, entityTypeIn, reason));
        if (matchDeny) return SpawnCheckResult.DENY;
        else return SpawnCheckResult.DEFAULT;
    }

    public enum SpawnCheckResult {
        DENY, DEFAULT, FORCE
    }
}
