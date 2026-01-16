package com.thecrazy8.sanity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.thecrazy8.sanity.SanityAttachments;
import com.thecrazy8.sanity.SanityData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.player.Player;

public class SanityHudOverlay implements LayeredDraw.Layer {
    
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

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        // Draw sanity bar
        renderSanityBar(guiGraphics, sanityData, screenWidth, screenHeight);
    }

    private void renderSanityBar(GuiGraphics guiGraphics, SanityData sanityData, int screenWidth, int screenHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        int barWidth = 81;
        int barHeight = 9;
        int x = screenWidth / 2 - barWidth / 2;
        int y = screenHeight - 49;

        float sanityPercentage = sanityData.getSanityPercentage();
        int filledWidth = (int) (barWidth * (sanityPercentage / 100.0f));

        // Background
        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);

        // Get color based on sanity level
        int color = getSanityColor(sanityPercentage);
        
        // Filled portion
        if (filledWidth > 0) {
            guiGraphics.fill(x, y, x + filledWidth, y + barHeight, color);
        }

        // Sanity text
        String sanityText = String.format("Sanity: %.0f%%", sanityPercentage);
        int textX = screenWidth / 2 - minecraft.font.width(sanityText) / 2;
        int textY = y - 10;
        guiGraphics.drawString(minecraft.font, sanityText, textX, textY, 0xFFFFFFFF);
    }

    private int getSanityColor(float percentage) {
        if (percentage >= 60.0f) {
            return 0xFF00FF00; // Green
        } else if (percentage >= 40.0f) {
            return 0xFFFFFF00; // Yellow
        } else if (percentage >= 20.0f) {
            return 0xFFFF8800; // Orange
        } else {
            return 0xFFFF0000; // Red
        }
    }
}
