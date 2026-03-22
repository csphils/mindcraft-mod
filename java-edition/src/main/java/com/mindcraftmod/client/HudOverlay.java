package com.mindcraftmod.client;

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

    /** Faction display name — updated via {@link #setFaction(String)} from the network handler. */
    private static String currentFactionName  = "No faction";
    /** ARGB colour — cornflower blue (Allies), imperial red (Central), neutral gray (None). */
    private static int    currentFactionColor = 0xFFAAAAAA;

    /**
     * Called by the S→C networking handler whenever the player's faction changes.
     * Safe to call from any thread; the render loop reads these fields on the render thread
     * without locking (values are primitives / immutable Strings — benign data race).
     */
    public static void setFaction(String factionEnumName) {
        currentFactionName = switch (factionEnumName) {
            case "ALLIES"         -> "Allies";
            case "CENTRAL_POWERS" -> "Central Powers";
            default               -> "No faction";
        };
        currentFactionColor = switch (factionEnumName) {
            case "ALLIES"         -> 0xFF4488FF;  // cornflower blue
            case "CENTRAL_POWERS" -> 0xFFCC3333;  // imperial red
            default               -> 0xFFAAAAAA;  // neutral gray
        };
    }

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
