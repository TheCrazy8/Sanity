package com.thecrazy8.sanity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class SanityEventHandler {

    private static final int UPDATE_INTERVAL = 20; // Update every second (20 ticks)
    private static final float SANITY_LOSS_DARKNESS = -0.1f; // Per second in darkness
    private static final float SANITY_LOSS_LOW_HUNGER = -0.15f; // Per second when hungry
    private static final float SANITY_RECOVERY_LIGHT = 0.05f; // Per second in light
    private static final float SANITY_RECOVERY_SLEEP = 50.0f; // Restored when sleeping successfully
    
    // Light thresholds
    private static final int DARK_THRESHOLD = 7;
    private static final int BRIGHT_THRESHOLD = 12;
    
    // Cached mod availability checks
    private static Boolean thirstModLoaded = null;
    private static Boolean coldSweatModLoaded = null;

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        
        // Only process on server side
        if (player.level().isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // Update every second
        if (serverPlayer.tickCount % UPDATE_INTERVAL != 0) {
            return;
        }

        SanityData sanityData = serverPlayer.getData(SanityAttachments.SANITY_DATA);
        if (sanityData == null) {
            return;
        }

        float sanityChange = 0.0f;

        // Factor 1: Light Level
        sanityChange += calculateLightEffect(serverPlayer);

        // Factor 2: Hunger
        sanityChange += calculateHungerEffect(serverPlayer);

        // Factor 3: Thirst (ThirstWasTaken integration)
        sanityChange += calculateThirstEffect(serverPlayer);

        // Factor 4: Temperature (Cold Sweat integration)
        sanityChange += calculateTemperatureEffect(serverPlayer);

        // Apply sanity change
        if (sanityChange != 0.0f) {
            sanityData.addSanity(sanityChange);
            sanityData.setLastUpdate(System.currentTimeMillis());
        }

        // Apply effects based on sanity level
        applySanityEffects(serverPlayer, sanityData.getSanity());

        // Sync to client
        serverPlayer.setData(SanityAttachments.SANITY_DATA, sanityData);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // Initialize sanity data if not present
            SanityData sanityData = serverPlayer.getData(SanityAttachments.SANITY_DATA);
            if (sanityData == null) {
                serverPlayer.setData(SanityAttachments.SANITY_DATA, new SanityData());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        
        // Only process on server side
        if (player.level().isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        
        // Only restore sanity if the player actually slept (not interrupted)
        // wakeImmediately() being false means they completed a full sleep cycle
        if (!event.wakeImmediately() && !event.updateLevel()) {
            SanityData sanityData = serverPlayer.getData(SanityAttachments.SANITY_DATA);
            if (sanityData != null) {
                // Restore sanity after sleeping
                sanityData.addSanity(SANITY_RECOVERY_SLEEP);
                sanityData.setLastUpdate(System.currentTimeMillis());
                serverPlayer.setData(SanityAttachments.SANITY_DATA, sanityData);
            }
        }
    }

    private float calculateLightEffect(ServerPlayer player) {
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        int lightLevel = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        int totalLight = Math.max(lightLevel, skyLight);

        if (totalLight < DARK_THRESHOLD) {
            // In darkness - lose sanity
            return SANITY_LOSS_DARKNESS;
        } else if (totalLight >= BRIGHT_THRESHOLD) {
            // In bright light - slowly recover
            return SANITY_RECOVERY_LIGHT;
        }
        
        return 0.0f;
    }

    private float calculateHungerEffect(ServerPlayer player) {
        int foodLevel = player.getFoodData().getFoodLevel();
        
        // Low hunger affects sanity negatively
        if (foodLevel <= 6) {
            return SANITY_LOSS_LOW_HUNGER;
        }
        
        return 0.0f;
    }

    private float calculateThirstEffect(ServerPlayer player) {
        // Check if ThirstWasTaken mod is loaded (cached)
        if (thirstModLoaded == null) {
            try {
                Class.forName("dev.ghen.thirst.foundation.common.capability.IThirst");
                thirstModLoaded = true;
            } catch (ClassNotFoundException e) {
                thirstModLoaded = false;
            }
        }
        
        if (thirstModLoaded) {
            // If the mod is present, integrate with its API
            // For now, return 0 - full integration would require the mod as a dependency
            return 0.0f;
        }
        return 0.0f;
    }

    private float calculateTemperatureEffect(ServerPlayer player) {
        // Check if Cold Sweat mod is loaded (cached)
        if (coldSweatModLoaded == null) {
            try {
                Class.forName("com.momosoftworks.coldsweat.api.temperature.Temperature");
                coldSweatModLoaded = true;
            } catch (ClassNotFoundException e) {
                coldSweatModLoaded = false;
            }
        }
        
        if (coldSweatModLoaded) {
            // If the mod is present, integrate with its API
            // For now, return 0 - full integration would require the mod as a dependency
            return 0.0f;
        }
        return 0.0f;
    }

    private void applySanityEffects(ServerPlayer player, float sanity) {
        // Remove existing sanity effects
        player.removeEffect(MobEffects.CONFUSION);
        player.removeEffect(MobEffects.DARKNESS);
        player.removeEffect(MobEffects.WEAKNESS);

        if (sanity < 20.0f) {
            // Very low sanity - severe effects
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
        } else if (sanity < 40.0f) {
            // Low sanity - moderate effects
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
        } else if (sanity < 60.0f) {
            // Medium sanity - minor effects
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
        }
    }
}
