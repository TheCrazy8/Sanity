package com.thecrazy8.sanity.client;

import com.thecrazy8.sanity.Sanity;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@EventBusSubscriber(modid = Sanity.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    
    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(Sanity.MOD_ID, "sanity_visual_effects"), new SanityVisualEffects());
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(Sanity.MOD_ID, "sanity_hud"), new SanityHudOverlay());
    }
}
