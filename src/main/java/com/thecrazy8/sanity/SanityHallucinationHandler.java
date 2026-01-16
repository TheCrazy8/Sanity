package com.thecrazy8.sanity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Random;

public class SanityHallucinationHandler {
    
    private static final Random RANDOM = new Random();
    private static final int HALLUCINATION_CHECK_INTERVAL = 100; // Check every 5 seconds
    
    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // Check for hallucinations periodically
        if (serverPlayer.tickCount % HALLUCINATION_CHECK_INTERVAL != 0) {
            return;
        }

        SanityData sanityData = serverPlayer.getData(SanityAttachments.SANITY_DATA);
        if (sanityData == null) {
            return;
        }

        float sanity = sanityData.getSanity();
        
        // Trigger hallucinations based on sanity level
        if (sanity < 30.0f && RANDOM.nextFloat() < 0.3f) {
            triggerEntityHallucination(serverPlayer);
        }
        
        if (sanity < 50.0f && RANDOM.nextFloat() < 0.2f) {
            triggerSoundHallucination(serverPlayer);
        }
    }

    private void triggerEntityHallucination(ServerPlayer player) {
        // Spawn temporary "shadow" entities that appear and disappear
        ServerLevel level = player.serverLevel();
        Vec3 playerPos = player.position();
        
        // Random offset from player
        double offsetX = (RANDOM.nextDouble() - 0.5) * 20;
        double offsetZ = (RANDOM.nextDouble() - 0.5) * 20;
        BlockPos spawnPos = BlockPos.containing(playerPos.x + offsetX, playerPos.y, playerPos.z + offsetZ);
        
        // Try to find a valid spawn position
        spawnPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnPos);
        
        // Only spawn if within reasonable distance and not too close
        double distance = player.position().distanceTo(Vec3.atCenterOf(spawnPos));
        if (distance > 5 && distance < 20) {
            // Random chance of different entities for hallucinations
            EntityType<?> entityType = getRandomHallucinationEntity();
            
            try {
                var entity = entityType.spawn(level, spawnPos, MobSpawnType.TRIGGERED);
                if (entity != null) {
                    // Mark entity for removal after a short time (hallucination fades)
                    entity.getPersistentData().putBoolean("sanity_hallucination", true);
                    entity.getPersistentData().putInt("hallucination_lifetime", 100 + RANDOM.nextInt(100));
                    
                    // Make hallucinations completely harmless and non-interactive
                    entity.setNoAi(true);
                    entity.setSilent(true);
                    entity.setInvulnerable(true);
                    entity.setNoGravity(true);
                    
                    // Prevent hallucinations from targeting or attacking
                    if (entity instanceof net.minecraft.world.entity.Mob mob) {
                        mob.setNoAi(true);
                        mob.setTarget(null);
                    }
                }
            } catch (IllegalArgumentException | NullPointerException e) {
                Sanity.LOGGER.warn("Failed to spawn hallucination entity at {}: {}", spawnPos, e.getMessage());
            }
        }
    }

    private EntityType<?> getRandomHallucinationEntity() {
        EntityType<?>[] entities = {
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.ENDERMAN,
            EntityType.CREEPER,
            EntityType.PHANTOM
        };
        return entities[RANDOM.nextInt(entities.length)];
    }

    private void triggerSoundHallucination(ServerPlayer player) {
        // Play eerie sounds at random positions around the player
        Vec3 playerPos = player.position();
        double offsetX = (RANDOM.nextDouble() - 0.5) * 10;
        double offsetY = (RANDOM.nextDouble() - 0.5) * 5;
        double offsetZ = (RANDOM.nextDouble() - 0.5) * 10;
        
        // Random unsettling sounds
        var sounds = new net.minecraft.sounds.SoundEvent[]{
            SoundEvents.AMBIENT_CAVE,
            SoundEvents.ENDERMAN_AMBIENT,
            SoundEvents.GHAST_AMBIENT,
            SoundEvents.WARDEN_AMBIENT,
            SoundEvents.SCULK_SHRIEKER_SHRIEK,
            SoundEvents.PORTAL_AMBIENT
        };
        
        var sound = sounds[RANDOM.nextInt(sounds.length)];
        float volume = 0.5f + RANDOM.nextFloat() * 0.5f;
        float pitch = 0.8f + RANDOM.nextFloat() * 0.4f;
        
        player.level().playSound(
            null,
            playerPos.x + offsetX,
            playerPos.y + offsetY,
            playerPos.z + offsetZ,
            sound,
            SoundSource.AMBIENT,
            volume,
            pitch
        );
    }
}
