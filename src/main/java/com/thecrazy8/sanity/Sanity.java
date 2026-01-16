package com.thecrazy8.sanity;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Sanity.MOD_ID)
public class Sanity {
    public static final String MOD_ID = "sanity";
    public static final Logger LOGGER = LoggerFactory.getLogger(Sanity.class);

    public Sanity(IEventBus modEventBus) {
        LOGGER.info("Initializing Sanity mod");
        
        modEventBus.addListener(this::commonSetup);
        
        // Register mod components
        SanityAttachments.register(modEventBus);
        
        // Register event handlers
        NeoForge.EVENT_BUS.register(new SanityEventHandler());
        NeoForge.EVENT_BUS.register(new SanityHallucinationHandler());
        NeoForge.EVENT_BUS.register(new HallucinationCleanupHandler());
        NeoForge.EVENT_BUS.register(new HallucinationDamagePreventionHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Sanity mod common setup complete");
    }
}
