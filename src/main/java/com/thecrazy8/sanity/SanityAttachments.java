package com.thecrazy8.sanity;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SanityAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Sanity.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SanityData>> SANITY_DATA = 
        ATTACHMENT_TYPES.register("sanity_data", () -> 
            AttachmentType.builder(() -> new SanityData()).serialize(SanityData.CODEC).build()
        );

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
