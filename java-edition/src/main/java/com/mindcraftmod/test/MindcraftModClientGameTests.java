package com.mindcraftmod.test;

import com.mindcraftmod.MindcraftMod;
import net.fabricmc.fabric.api.client.gametest.v1.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;

import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

/**
 * Fabric client gametest — verifies the mod loads and renders without crashing.
 *
 * Registered in fabric.mod.json as a "fabric-client-gametest" entrypoint.
 *
 * Run via: ./gradlew runClientGametest
 *
 * Three sequential sub-tests in a single runTest() pass:
 *  1. clientBootsWithoutCrash  — world creation + chunk render with no exception.
 *  2. allMobEntityTypesRender  — summons all 7 mod mob types; any entity renderer
 *                                 crash (NPE, missing model, etc.) fails the test.
 *  3. allBlockModelsRender     — 28 named screenshots: per-block individual shots,
 *                                 sandbag height progression, contextual group shots,
 *                                 and multi-angle close-ups of key blocks.
 */
public class MindcraftModClientGameTests implements FabricClientGameTest {

    private static final String NS = MindcraftMod.MOD_ID;

    /** Entity IDs of every mob type registered in ModEntities (projectiles excluded). */
    private static final String[] MOB_ENTITY_IDS = {
        NS + ":war_horse",
        NS + ":carrier_pigeon",
        NS + ":trench_rat",
        NS + ":guard_dog",
        NS + ":trench_soldier",
        NS + ":sniper",
        NS + ":gas_grenadier",
    };

    @Override
    public void runTest(ClientGameTestContext context) {
        clientBootsWithoutCrash(context);
        allMobEntityTypesRender(context);
        allBlockModelsRender(context);
    }

    /**
     * Creates a flat world, waits for chunks to render, and takes a screenshot.
     * Completing without exception confirms the mod loaded and the client
     * initialised without crashing.
     */
    private void clientBootsWithoutCrash(ClientGameTestContext context) {
        try (var singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientWorld().waitForChunksRender();
            var screenshot = context.takeScreenshot("mindcraft_boot_no_crash");

            if (!Files.exists(screenshot)) {
                throw new AssertionError("clientBootsWithoutCrash: screenshot not created at " + screenshot);
            }
            try {
                long size = Files.size(screenshot);
                if (size < 10_240) {
                    throw new AssertionError(
                        "clientBootsWithoutCrash: screenshot too small (" + size
                        + " B) — renderer may not have produced output");
                }
            } catch (IOException e) {
                throw new RuntimeException("clientBootsWithoutCrash: could not stat screenshot", e);
            }
        }
    }

    /**
     * Summons all 7 mod mob types in a flat world (spaced 3 blocks apart at Y=64)
     * and waits 20 ticks for the renderer to process them.
     * Any entity renderer crash will propagate as an uncaught exception and fail the test.
     */
    private void allMobEntityTypesRender(ClientGameTestContext context) {
        try (var singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientWorld().waitForChunksRender();

            for (int i = 0; i < MOB_ENTITY_IDS.length; i++) {
                // summon at Y=64 to ensure entities are above any superflat preset surface
                singleplayer.getServer().runCommand(
                    "summon " + MOB_ENTITY_IDS[i] + " " + (i * 3) + " 64 5");
            }

            // Entities span x=0..18 at z=5, y=64 — position camera at midpoint looking at them
            singleplayer.getServer().runCommand("tp @a 9 66 -3 facing 9 64 5");
            context.waitTicks(10);
            context.takeScreenshot("mindcraft_entities_rendered");
        }
    }

    /**
     * 28 screenshots covering every mod block:
     *  • 16 individual block shots (one per block type, isolated front view)
     *  •  1 sandbag height progression (all 8 heights side by side)
     *  •  4 contextual group shots (barbed wire run, trench corner, flags, overview)
     *  •  7 multi-angle shots (top-down, side, close-up, corner) for key blocks
     */
    private void allBlockModelsRender(ClientGameTestContext context) {
        try (var singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientWorld().waitForChunksRender();
            var srv = singleplayer.getServer();

            // ── INDIVIDUAL BLOCK SHOTS (shots 01-16) ─────────────────────────
            // All placed at (8, 64, 10). Each setblock replaces the previous —
            // no explicit clear needed between individual shots.

            Consumer<String> cmd = srv::runCommand;

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:barbed_wire",
                "8 66 5", "8 64 10", "block_01_barbed_wire");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:gas_cloud",
                "8 66 5", "8 64 10", "block_02_gas_cloud");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:smoke_screen",
                "8 66 5", "8 64 10", "block_03_smoke_screen");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:mud_pit",
                "8 66 5", "8 64 10", "block_04_mud_pit");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:sandbag[layers=8]",
                "8 66 5", "8 64 10", "block_05_sandbag_h8");

            // Single trench_wall renders as post (no neighbours)
            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:trench_wall",
                "8 66 5", "8 64 10", "block_06_trench_wall_post");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:artillery_platform",
                "8 66 5", "8 64 10", "block_07_artillery_platform");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:field_telephone",
                "8 66 5", "8 64 10", "block_08_field_telephone");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:supply_crate",
                "8 66 5", "8 64 10", "block_09_supply_crate");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:shell_crater",
                "8 66 5", "8 64 10", "block_10_shell_crater");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:barbed_wire_post",
                "8 66 5", "8 64 10", "block_11_barbed_wire_post");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:rusted_iron_bars",
                "8 66 5", "8 64 10", "block_12_rusted_iron_bars");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:flag_block[faction=allied,facing=north]",
                "8 66 5", "8 64 10", "block_13_flag_allied");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:flag_block[faction=neutral,facing=north]",
                "8 66 5", "8 64 10", "block_14_flag_neutral");

            placeAndShoot(cmd, context, "8 64 10", "mindcraftmod:flag_block[faction=central,facing=north]",
                "8 66 5", "8 64 10", "block_15_flag_central");

            // Trench wall connected: 3 in a row shows side/connection model
            cmd.accept("setblock 8 64 10 minecraft:air");
            cmd.accept("setblock 7 64 10 mindcraftmod:trench_wall");
            cmd.accept("setblock 8 64 10 mindcraftmod:trench_wall");
            cmd.accept("setblock 9 64 10 mindcraftmod:trench_wall");
            cmd.accept("tp @a 8 67 5 facing 8 64 10");
            context.waitTicks(5);
            context.takeScreenshot("block_16_trench_wall_connected");

            // ── SANDBAG HEIGHT PROGRESSION (shot 17) ─────────────────────────
            // Clear individual-shot position, then lay out 8 heights at z=25
            cmd.accept("fill 7 64 10 9 64 10 minecraft:air");
            for (int i = 1; i <= 8; i++) {
                cmd.accept("setblock " + ((i - 1) * 2) + " 64 25 mindcraftmod:sandbag[layers=" + i + "]");
            }
            // Side-on view from south, slightly elevated, looking north
            cmd.accept("tp @a 7 67 20 facing 7 64 25");
            context.waitTicks(6);
            context.takeScreenshot("block_17_sandbag_heights_1_to_8");

            // ── CONTEXTUAL GROUP SHOTS (shots 18-21) ─────────────────────────

            // 18. Barbed wire run: post-wire-post-wire-post
            cmd.accept("setblock 4 64 35 mindcraftmod:barbed_wire_post");
            cmd.accept("setblock 6 64 35 mindcraftmod:barbed_wire");
            cmd.accept("setblock 8 64 35 mindcraftmod:barbed_wire_post");
            cmd.accept("setblock 10 64 35 mindcraftmod:barbed_wire");
            cmd.accept("setblock 12 64 35 mindcraftmod:barbed_wire_post");
            cmd.accept("tp @a 8 67 29 facing 8 64 35");
            context.waitTicks(6);
            context.takeScreenshot("context_18_barbed_wire_run");

            // 19. Trench wall L-corner (row east + column south)
            for (int x = 6; x <= 11; x++) {
                cmd.accept("setblock " + x + " 64 45 mindcraftmod:trench_wall");
            }
            for (int z = 46; z <= 50; z++) {
                cmd.accept("setblock 11 64 " + z + " mindcraftmod:trench_wall");
            }
            cmd.accept("tp @a 4 68 40 facing 8 64 46");
            context.waitTicks(6);
            context.takeScreenshot("context_19_trench_wall_corner");

            // 20. All three faction flags side by side
            cmd.accept("setblock 5 64 55 mindcraftmod:flag_block[faction=allied,facing=north]");
            cmd.accept("setblock 8 64 55 mindcraftmod:flag_block[faction=neutral,facing=north]");
            cmd.accept("setblock 11 64 55 mindcraftmod:flag_block[faction=central,facing=north]");
            cmd.accept("tp @a 8 68 49 facing 8 64 55");
            context.waitTicks(6);
            context.takeScreenshot("context_20_flags_all_factions");

            // 21. All-blocks overview: all 15 block types in a row
            String[] allBlocks = {
                "mindcraftmod:barbed_wire",
                "mindcraftmod:gas_cloud",
                "mindcraftmod:smoke_screen",
                "mindcraftmod:mud_pit",
                "mindcraftmod:sandbag[layers=8]",
                "mindcraftmod:trench_wall",
                "mindcraftmod:artillery_platform",
                "mindcraftmod:field_telephone",
                "mindcraftmod:supply_crate",
                "mindcraftmod:shell_crater",
                "mindcraftmod:barbed_wire_post",
                "mindcraftmod:rusted_iron_bars",
                "mindcraftmod:flag_block[faction=allied,facing=north]",
                "mindcraftmod:flag_block[faction=neutral,facing=north]",
                "mindcraftmod:flag_block[faction=central,facing=north]"
            };
            for (int i = 0; i < allBlocks.length; i++) {
                cmd.accept("setblock " + (i * 2) + " 64 65 " + allBlocks[i]);
            }
            // Wide elevated shot — camera high and south, looking north over the row
            cmd.accept("tp @a 14 72 57 facing 14 64 65");
            context.waitTicks(8);
            context.takeScreenshot("context_21_all_blocks_overview");

            // ── MULTI-ANGLE SHOTS (shots 22-28) ──────────────────────────────
            // Isolated position at (8, 64, 75); replaced per-shot

            // 22. Barbed wire — top-down view
            placeAndShoot(cmd, context, "8 64 75", "mindcraftmod:barbed_wire",
                "8 74 75", "8 64 75", "angle_22_barbed_wire_top");

            // 23. Sandbag h8 — side view (camera to the east, looking west)
            placeAndShoot(cmd, context, "8 64 75", "mindcraftmod:sandbag[layers=8]",
                "14 65 75", "8 64 75", "angle_23_sandbag_h8_side");

            // 24. Flag allied — close-up from slightly below flag panel
            placeAndShoot(cmd, context, "8 64 75", "mindcraftmod:flag_block[faction=allied,facing=north]",
                "8 66 71", "8 65 75", "angle_24_flag_allied_closeup");

            // 25. Supply crate — 45° corner view (shows two faces + top)
            placeAndShoot(cmd, context, "8 64 75", "mindcraftmod:supply_crate",
                "4 67 71", "8 64 75", "angle_25_supply_crate_corner");

            // 26. Field telephone — front face close-up
            placeAndShoot(cmd, context, "8 64 75", "mindcraftmod:field_telephone",
                "8 66 72", "8 64 75", "angle_26_field_telephone_front");

            // 27. Trench wall post — overhead shows post footprint
            placeAndShoot(cmd, context, "8 64 75", "mindcraftmod:trench_wall",
                "8 74 75", "8 64 75", "angle_27_trench_wall_overhead");

            // 28. Shell crater — overhead reveals ring gradient
            placeAndShoot(cmd, context, "8 64 75", "mindcraftmod:shell_crater",
                "8 74 75", "8 64 75", "angle_28_shell_crater_overhead");
        }
    }

    /**
     * Place a block, teleport camera, wait 5 ticks, take screenshot.
     *
     * @param cmd       server command runner (e.g. srv::runCommand)
     * @param blockPos  "x y z" string of where to place the block
     * @param blockId   full block id (e.g. "mindcraftmod:supply_crate")
     * @param camPos    "cx cy cz" string of camera position
     * @param facingPos "fx fy fz" string for the look-at target
     * @param shotName  screenshot filename (no extension)
     */
    private void placeAndShoot(
            Consumer<String> cmd,
            ClientGameTestContext context,
            String blockPos, String blockId,
            String camPos, String facingPos,
            String shotName) {
        cmd.accept("setblock " + blockPos + " " + blockId);
        cmd.accept("tp @a " + camPos + " facing " + facingPos);
        context.waitTicks(5);
        context.takeScreenshot(shotName);
    }
}
