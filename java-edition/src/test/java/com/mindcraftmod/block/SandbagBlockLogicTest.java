package com.mindcraftmod.block;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SandbagBlock pure logic — layer property bounds and shape heights.
 *
 * These tests do NOT require Minecraft to run. They validate the constants and
 * property definitions directly via reflection or package-visible accessors.
 *
 * Run with: ./gradlew test
 *
 * NOTE: VoxelShape is a Minecraft class, so the shape height tests use the
 * package-visible LAYER_TO_SHAPE_HEIGHTS array that mirrors the real shapes.
 * The GameTest suite covers in-game collision verification.
 */
class SandbagBlockLogicTest {

    // ── LAYERS property ──────────────────────────────────────────────────────

    @Test
    void layers_minValueIs1() {
        assertEquals(1, SandbagBlock.LAYERS.getValues().stream()
                .mapToInt(Integer::intValue).min().orElseThrow());
    }

    @Test
    void layers_maxValueIs8() {
        assertEquals(8, SandbagBlock.LAYERS.getValues().stream()
                .mapToInt(Integer::intValue).max().orElseThrow());
    }

    @Test
    void layers_hasExactlyEightValues() {
        assertEquals(8, SandbagBlock.LAYERS.getValues().size());
    }

    @Test
    void layers_containsAllValuesOneToEight() {
        var values = SandbagBlock.LAYERS.getValues();
        for (int i = 1; i <= 8; i++) {
            assertTrue(values.contains(i), "LAYERS should contain value " + i);
        }
    }

    // ── Layer height spec ────────────────────────────────────────────────────
    // Each layer adds 2px (1/8 block). Layer 1 = 2px tall, layer 8 = 16px (full).
    // We verify via the LAYER_HEIGHT_PX array exposed for testing.

    @ParameterizedTest(name = "layer {0} height = {0}*2 px")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    void layerHeight_nonFullLayers_is2PxPerLayer(int layer) {
        int expectedPx = layer * 2;
        assertEquals(expectedPx, SandbagBlock.LAYER_HEIGHT_PX[layer],
                "Layer " + layer + " should be " + expectedPx + "px tall");
    }

    @Test
    void layerHeight_layer8_is16px_fullBlock() {
        assertEquals(16, SandbagBlock.LAYER_HEIGHT_PX[8],
                "Layer 8 should be 16px (full block)");
    }

    // ── Stacking guard ───────────────────────────────────────────────────────

    @Test
    void stackingCap_layer8_isMaxAllowed() {
        // Confirm the cap is 8, not 7 or 9
        int max = SandbagBlock.LAYERS.getValues().stream()
                .mapToInt(Integer::intValue).max().orElseThrow();
        assertEquals(8, max, "Maximum sandbag stack must be exactly 8 layers");
    }
}
