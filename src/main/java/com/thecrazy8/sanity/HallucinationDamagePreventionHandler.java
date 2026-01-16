package com.thecrazy8.sanity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class HallucinationDamagePreventionHandler {
    
    @SubscribeEvent
    public void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        
        // Prevent hallucination entities from dealing damage
        if (attacker != null && attacker.getPersistentData().contains("sanity_hallucination")) {
            event.setCanceled(true);
        }
        
        // Prevent damage to hallucination entities (they're not real)
        if (event.getEntity().getPersistentData().contains("sanity_hallucination")) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Pre event) {
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        
        // Extra safety: prevent hallucination entities from dealing damage at damage calculation stage
        if (attacker != null && attacker.getPersistentData().contains("sanity_hallucination")) {
            event.setNewDamage(0.0f);
        }
        
        // Prevent damage to hallucination entities
        if (event.getEntity().getPersistentData().contains("sanity_hallucination")) {
            event.setNewDamage(0.0f);
        }
    }
}
