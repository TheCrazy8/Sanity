package com.thecrazy8.sanity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SanityData {
    public static final Codec<SanityData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.FLOAT.fieldOf("sanity").forGetter(SanityData::getSanity),
            Codec.LONG.fieldOf("lastUpdate").forGetter(SanityData::getLastUpdate)
        ).apply(instance, SanityData::new)
    );

    public static final float MAX_SANITY = 100.0f;
    public static final float MIN_SANITY = 0.0f;
    
    private float sanity;
    private long lastUpdate;

    public SanityData() {
        this.sanity = MAX_SANITY;
        this.lastUpdate = System.currentTimeMillis();
    }

    public SanityData(float sanity, long lastUpdate) {
        this.sanity = Math.max(MIN_SANITY, Math.min(MAX_SANITY, sanity));
        this.lastUpdate = lastUpdate;
    }

    public float getSanity() {
        return sanity;
    }

    public void setSanity(float sanity) {
        this.sanity = Math.max(MIN_SANITY, Math.min(MAX_SANITY, sanity));
    }

    public void addSanity(float amount) {
        setSanity(this.sanity + amount);
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public float getSanityPercentage() {
        return (sanity / MAX_SANITY) * 100.0f;
    }
}
