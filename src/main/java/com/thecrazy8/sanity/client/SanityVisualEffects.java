package com.thecrazy8.sanity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.thecrazy8.sanity.SanityAttachments;
import com.thecrazy8.sanity.SanityData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

public class SanityVisualEffects implements LayeredDraw.Layer {
    
    private final Random random = new Random();
    private float vignetteIntensity = 0.0f;
    private float distortionAmount = 0.0f;
    private float colorShiftAmount = 0.0f;
    private int flashTimer = 0;
    private int hallucinationTimer = 0;
    private int tickCounter = 0; // Limit random updates to every few frames

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        
        if (player == null || minecraft.options.hideGui) {
            return;
        }

        SanityData sanityData = player.getData(SanityAttachments.SANITY_DATA);
        if (sanityData == null) {
            return;
        }

        float sanity = sanityData.getSanity();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        // Update effects based on sanity level
        updateEffectIntensities(sanity);

        // Apply visual effects
        if (sanity < 80.0f) {
            renderVignette(guiGraphics, screenWidth, screenHeight, vignetteIntensity);
        }

        if (sanity < 60.0f) {
            renderScreenDistortion(guiGraphics, screenWidth, screenHeight);
        }

        if (sanity < 40.0f) {
            renderColorShift(guiGraphics, screenWidth, screenHeight);
        }

        if (sanity < 30.0f) {
            renderRandomFlashes(guiGraphics, screenWidth, screenHeight);
        }

        if (sanity < 20.0f) {
            renderHallucinationOverlay(guiGraphics, screenWidth, screenHeight);
        }
    }

    private void updateEffectIntensities(float sanity) {
        tickCounter++;
        
        // Vignette intensity increases as sanity decreases
        if (sanity < 80.0f) {
            vignetteIntensity = 1.0f - (sanity / 80.0f);
        } else {
            vignetteIntensity = 0.0f;
        }

        // Distortion increases at low sanity
        if (sanity < 60.0f) {
            distortionAmount = (60.0f - sanity) / 60.0f;
        } else {
            distortionAmount = 0.0f;
        }

        // Color shift for very low sanity
        if (sanity < 40.0f) {
            colorShiftAmount = (40.0f - sanity) / 40.0f;
        } else {
            colorShiftAmount = 0.0f;
        }

        // Update timers only every 20 ticks (once per second) to reduce random calls
        if (tickCounter % 20 != 0) {
            if (flashTimer > 0) flashTimer--;
            if (hallucinationTimer > 0) hallucinationTimer--;
            return;
        }

        // Timer updates
        if (flashTimer > 0) {
            flashTimer--;
        } else if (sanity < 30.0f && random.nextFloat() < 0.2f) {
            flashTimer = 5 + random.nextInt(10);
        }

        if (hallucinationTimer > 0) {
            hallucinationTimer--;
        } else if (sanity < 20.0f && random.nextFloat() < 0.1f) {
            hallucinationTimer = 20 + random.nextInt(60);
        }
    }

    private void renderVignette(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float intensity) {
        // Draw darkened edges that close in as sanity decreases
        int vignetteSize = (int) (Math.min(screenWidth, screenHeight) * 0.3f * intensity);
        int alpha = (int) (180 * intensity);
        int color = (alpha << 24);

        // Top vignette
        guiGraphics.fillGradient(0, 0, screenWidth, vignetteSize, color, 0x00000000);
        
        // Bottom vignette
        guiGraphics.fillGradient(0, screenHeight - vignetteSize, screenWidth, screenHeight, 0x00000000, color);
        
        // Left vignette
        guiGraphics.fillGradient(0, 0, vignetteSize, screenHeight, color, 0x00000000);
        
        // Right vignette
        guiGraphics.fillGradient(screenWidth - vignetteSize, 0, screenWidth, screenHeight, 0x00000000, color);
    }

    private void renderScreenDistortion(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        // Create wavy lines across the screen
        int alpha = (int) (100 * distortionAmount);
        
        for (int i = 0; i < 5; i++) {
            int y = (int) (screenHeight * random.nextFloat());
            int height = 1 + random.nextInt(3);
            guiGraphics.fill(0, y, screenWidth, y + height, (alpha << 24) | 0x808080);
        }
    }

    private void renderColorShift(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        // Overlay with desaturated/shifted color
        int alpha = (int) (30 * colorShiftAmount);
        int redShift = (int) (50 * colorShiftAmount);
        int color = (alpha << 24) | (redShift << 16) | (0 << 8) | redShift;
        
        guiGraphics.fill(0, 0, screenWidth, screenHeight, color);
    }

    private void renderRandomFlashes(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        if (flashTimer > 0) {
            // Quick white/red flashes
            int alpha = (int) (150 * (flashTimer / 15.0f));
            int color = (alpha << 24) | 0xFFFFFF;
            
            if (random.nextBoolean()) {
                color = (alpha << 24) | 0xFF0000; // Red flash
            }
            
            guiGraphics.fill(0, 0, screenWidth, screenHeight, color);
        }
    }

    private void renderHallucinationOverlay(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        if (hallucinationTimer > 0) {
            // Draw shadow figures or shapes
            int alpha = Math.min(200, hallucinationTimer * 3);
            
            // Random dark shapes appearing in corners
            if (random.nextBoolean()) {
                int x = random.nextBoolean() ? 0 : screenWidth - 100;
                int y = random.nextBoolean() ? 0 : screenHeight - 100;
                int width = 50 + random.nextInt(100);
                int height = 50 + random.nextInt(100);
                
                guiGraphics.fill(x, y, x + width, y + height, (alpha << 24));
            }

            // Floating text hallucinations
            if (hallucinationTimer % 20 < 10) {
                Minecraft minecraft = Minecraft.getInstance();
                String[] hallucinationTexts = {
                    "You are not alone",
                    "Behind you",
                    "Can you see it?",
                    "They're watching",
                    "Something is wrong",
                    "Exit?",
                    "No escape"
                };
                
                String text = hallucinationTexts[random.nextInt(hallucinationTexts.length)];
                int textWidth = minecraft.font.width(text);
                // Ensure text is at least 20 pixels from screen edges
                int minX = 20;
                int maxX = Math.max(minX + 1, screenWidth - textWidth - 20);
                int textX = minX + random.nextInt(Math.max(1, maxX - minX));
                int textY = 20 + random.nextInt(Math.max(1, screenHeight - 40));
                int textAlpha = Math.min(255, alpha);
                int textColor = (textAlpha << 24) | 0xFF0000;
                
                guiGraphics.drawString(minecraft.font, text, textX, textY, textColor);
            }

            // Screen shake effect indicators
            if (hallucinationTimer % 5 == 0) {
                int shakeAlpha = 100;
                int offset = random.nextInt(5) - 2;
                guiGraphics.fill(0, screenHeight / 2 + offset, screenWidth, screenHeight / 2 + offset + 2, 
                    (shakeAlpha << 24) | 0x880000);
            }
        }
    }
}
