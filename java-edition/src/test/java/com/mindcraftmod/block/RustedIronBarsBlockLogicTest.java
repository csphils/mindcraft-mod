package com.mindcraftmod.block;

import net.minecraft.block.PaneBlock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RustedIronBarsBlock pure logic — class hierarchy and property definitions.
 *
 * These tests do NOT require Minecraft to run. They validate that the class exists,
 * extends PaneBlock, and exposes the correct directional properties.
 *
 * Run with: ./gradlew test
 *
 * NOTE: RustedIronBarsBlock does not yet exist. These tests are intentionally in
 * Red state (compile failure) until the production class is created.
 * See Phase 2 plan: C:\Users\phil\.claude\plans\phase-2-blocks.md
 */
class RustedIronBarsBlockLogicTest {

    // ── Class hierarchy ──────────────────────────────────────────────────────

    /**
     * RustedIronBarsBlock must extend PaneBlock so it inherits iron-bars-style
     * multipart connection logic (north/south/east/west state properties).
     */
    @Test
    void rustedIronBarsBlock_isInstanceOfPaneBlock() {
        assertTrue(
                PaneBlock.class.isAssignableFrom(RustedIronBarsBlock.class),
                "RustedIronBarsBlock must extend PaneBlock to get pane connection behaviour"
        );
    }

    // ── Directional connection properties ────────────────────────────────────
    // PaneBlock inherits NORTH/SOUTH/EAST/WEST from ConnectingBlock.
    // Accessing those static fields triggers Minecraft's class initializer which
    // requires a full game bootstrap — not available in pure unit tests.
    // Instead, verify via reflection that the class declares no overriding field
    // that would shadow the inherited properties, and that the class is a subtype
    // of ConnectingBlock (which owns the directional properties).

    @Test
    void rustedIronBarsBlock_inheritsConnectingBlockProperties() {
        // ConnectingBlock is the source of NORTH/SOUTH/EAST/WEST.
        // isAssignableFrom(child) returns true if child IS-A parent.
        Class<?> connectingBlock = PaneBlock.class.getSuperclass().getSuperclass();
        assertTrue(
                connectingBlock.isAssignableFrom(RustedIronBarsBlock.class),
                "RustedIronBarsBlock must inherit from ConnectingBlock for directional properties"
        );
    }

    @Test
    void rustedIronBarsBlock_doesNotDeclareOwnDirectionalFields() throws Exception {
        // Confirm there are no shadowing NORTH/SOUTH/EAST/WEST fields on our class.
        var ownFields = java.util.Arrays.stream(RustedIronBarsBlock.class.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .toList();
        assertFalse(ownFields.contains("NORTH"), "NORTH should be inherited, not redeclared");
        assertFalse(ownFields.contains("SOUTH"), "SOUTH should be inherited, not redeclared");
        assertFalse(ownFields.contains("EAST"),  "EAST should be inherited, not redeclared");
        assertFalse(ownFields.contains("WEST"),  "WEST should be inherited, not redeclared");
    }
}
