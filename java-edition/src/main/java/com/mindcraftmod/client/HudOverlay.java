package com.mindcraftmod.client;

import com.mindcraftmod.world.FactionManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

/**
 * Draws a small HUD in the top-right corner showing:
 *   - The local player's faction name (colour-coded)
 *   - A simple status line ("Allies | Central Powers | No faction")
 *
 * The faction data is cached from the last /faction info response to avoid
 * server queries every frame. It updates when the player sends a chat message
 * that starts with "/faction" (handled via ClientPlayNetworking in a full
 * implementation; here we read a static field set by FactionClientState).
 */
@Environment(EnvType.CLIENT)
public class HudOverlay {

    /** Faction for the local player — updated by FactionClientState. */
    public static String currentFactionName = "No faction";
    /** ARGB colour to render the faction name in. */
    public static int    currentFactionColor = 0xFFAAAAAA;

    public static void register() {
        HudRenderCallback.EVENT.register(HudOverlay::onHudRender);
    }

    private static void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int screenWidth = context.getScaledWindowWidth();

        String label = "Faction: " + currentFactionName;
        int textWidth = client.textRenderer.getWidth(label);
        int x = screenWidth - textWidth - 4;
        int y = 4;

        // Drop shadow then coloured text
        context.drawText(client.textRenderer, Text.literal(label),
                x + 1, y + 1, 0x55000000, false);
        context.drawText(client.textRenderer, Text.literal(label),
                x, y, currentFactionColor, false);
    }
}
