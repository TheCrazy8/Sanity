package com.thecrazy8.sanity;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class HallucinationCleanupHandler {
    
    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        
        // Check if this is a hallucination entity
        if (entity.getPersistentData().contains("sanity_hallucination")) {
            int lifetime = entity.getPersistentData().getInt("hallucination_lifetime");
            lifetime--;
            
            if (lifetime <= 0) {
                // Remove the hallucination entity
                entity.discard();
            } else {
                entity.getPersistentData().putInt("hallucination_lifetime", lifetime);
                
                // Make hallucinations more ethereal - they don't attack or interact normally
                entity.setNoAi(true);
                entity.setSilent(true);
                entity.setInvulnerable(true);
            }
        }
    }
}
