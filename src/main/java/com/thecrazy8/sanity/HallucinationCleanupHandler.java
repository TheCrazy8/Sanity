package com.thecrazy8.sanity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class HallucinationCleanupHandler {
    
    @SubscribeEvent
    public void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }
        
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
                if (entity instanceof Mob mob) {
                    mob.setNoAi(true);
                }
                entity.setSilent(true);
                entity.setInvulnerable(true);
            }
        }
    }
}
