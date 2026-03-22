package com.mindcraftmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-side entrypoint.
 * Handles: entity renderers, block entity renderers, HUD overlays, screen GUIs.
 */
@Environment(EnvType.CLIENT)
public class MindcraftModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Entity renderer registration added in Phase 4
        // HUD overlay registration added in Phase 7
    }
}
