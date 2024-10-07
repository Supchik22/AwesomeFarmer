package com.supchikwork;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter; // Ensure this import exists

public class AwesomeFarmerOverlay implements HudRenderCallback {
    AwesomeFarmerClient awClient;

    public AwesomeFarmerOverlay(AwesomeFarmerClient c) {
        awClient = c;
    }

    private TextRenderer textRenderer;

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        textRenderer = client.textRenderer;

        // TextRenderer, text (string, or Text object), x, y, color, shadow
        if (awClient.isFarmerModeEnabled) {
            drawContext.drawText(client.textRenderer, "AwesomeFarmer ON", 10, 200, 0xFFFFFFFF, false);
        }
    }
}
