package com.mindcraftmod.test;

import com.mindcraftmod.MindcraftMod;
import com.mindcraftmod.block.ModBlocks;
import com.mindcraftmod.block.WarPosterBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Fabric GameTest suite — Phase 2 block behaviour tests.
 *
 * Covers: RustedIronBarsBlock, WarPosterBlock, GraveMarkerBlock.
 *
 * All three target classes (RustedIronBarsBlock, WarPosterBlock, GraveMarkerBlock)
 * and the two new ModBlocks fields (WAR_POSTER, GRAVE_MARKER) do NOT exist yet.
 * This file is intentionally in Red / compile-failure state until Phase 2
 * production code is written.
 *
 * Run via: ./gradlew runGametest  OR  /test runAll in-game.
 */
public class BlockPhase2GameTests {

    private static final String NAMESPACE = MindcraftMod.MOD_ID;

    /** 3x3 flat stone floor — used for compact single-block tests. */
    private static final String ARENA_3X3 = NAMESPACE + ":empty_3x3";

    /** 5x5 flat stone floor — used for entity interaction tests. */
    private static final String ARENA_5X5 = NAMESPACE + ":empty_5x5";

    // ════════════════════════════════════════════════════════════════════════
    // RustedIronBarsBlock
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Placing rusted iron bars next to a stone neighbor causes NORTH=true after
     * a neighbour update, matching vanilla iron bars connection logic.
     *
     * Setup: bars at (1,1,1), stone placed at (1,1,0) — i.e. north neighbour.
     * Assert: block state at (1,1,1) has NORTH=true.
     */
    @GameTest(templateName = ARENA_3X3)
    public void rustedIronBarsConnectsNorthOnNeighborUpdate(TestContext ctx) {
        BlockPos barsPos   = new BlockPos(1, 1, 1);
        BlockPos northPos  = new BlockPos(1, 1, 0);

        ctx.setBlockState(barsPos,  ModBlocks.RUSTED_IRON_BARS.getDefaultState());
        ctx.setBlockState(northPos, Blocks.STONE.getDefaultState());

        // One tick for the neighbour update to propagate
        ctx.waitAndRun(1, () -> {
            var state = ctx.getBlockState(barsPos);
            ctx.assertTrue(
                    state.get(net.minecraft.block.PaneBlock.NORTH),
                    "RustedIronBars NORTH should be true when a solid block is placed to the north"
            );
            ctx.complete();
        });
    }

    /**
     * Rusted iron bars placed in open air have all directional properties false.
     *
     * Assert: NORTH=false with no surrounding neighbours.
     */
    @GameTest(templateName = ARENA_3X3)
    public void rustedIronBarsDoesNotConnectToAir(TestContext ctx) {
        BlockPos barsPos = new BlockPos(1, 1, 1);
        ctx.setBlockState(barsPos, ModBlocks.RUSTED_IRON_BARS.getDefaultState());

        ctx.waitAndRun(1, () -> {
            var state = ctx.getBlockState(barsPos);
            ctx.assertTrue(
                    !state.get(net.minecraft.block.PaneBlock.NORTH),
                    "RustedIronBars NORTH should be false when north neighbour is air"
            );
            ctx.complete();
        });
    }

    /**
     * Rusted iron bars have a non-full-cube collision shape, so an entity can pass
     * through the gap between the post centre and the block boundary.
     *
     * Assert: collision shape maxX < 1.0 (not a full 16px-wide cube).
     */
    @GameTest(templateName = ARENA_5X5)
    public void rustedIronBarsIsNonSolid_entityPassesThrough(TestContext ctx) {
        BlockPos barsPos = new BlockPos(2, 1, 2);
        ctx.setBlockState(barsPos, ModBlocks.RUSTED_IRON_BARS.getDefaultState());

        ctx.waitAndRun(1, () -> {
            var state   = ctx.getBlockState(barsPos);
            var absPos  = ctx.getAbsolutePos(barsPos);
            var shape   = state.getCollisionShape(ctx.getWorld(), absPos);

            // A full-cube shape spans 0→1 on every axis. PaneBlock posts are ~2px wide
            // centred at 0.5, so the bounding box max should be well under 1.0 when
            // no connections exist (post only).
            boolean isNotFullCube = shape.isEmpty() || shape.getBoundingBox().maxX < 1.0;
            ctx.assertTrue(
                    isNotFullCube,
                    "RustedIronBars collision shape must not be a full cube (pane geometry expected)"
            );
            ctx.complete();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // WarPosterBlock
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Placing a war poster (defaulting to NORTH facing) leaves a WarPosterBlock
     * instance at the position.
     */
    @GameTest(templateName = ARENA_3X3)
    public void warPoster_placesWithFacingNorth(TestContext ctx) {
        BlockPos pos = new BlockPos(1, 1, 1);
        ctx.setBlockState(pos, ModBlocks.WAR_POSTER.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH));

        ctx.waitAndRun(1, () -> {
            var block = ctx.getBlockState(pos).getBlock();
            ctx.assertTrue(
                    block instanceof WarPosterBlock,
                    "Block at pos should be a WarPosterBlock after placement"
            );
            ctx.complete();
        });
    }

    /**
     * A war poster is a thin decorative block — its outline shape should be no
     * wider than 0.25 blocks (4px) to model a flat poster on a wall.
     *
     * The width axis for a NORTH-facing poster is the Z axis (depth into wall).
     */
    @GameTest(templateName = ARENA_3X3)
    public void warPoster_hasCorrectOutlineShape(TestContext ctx) {
        BlockPos pos = new BlockPos(1, 1, 1);
        ctx.setBlockState(pos, ModBlocks.WAR_POSTER.getDefaultState()
                .with(Properties.HORIZONTAL_FACING, Direction.NORTH));

        ctx.waitAndRun(1, () -> {
            var state  = ctx.getBlockState(pos);
            var absPos = ctx.getAbsolutePos(pos);
            var shape  = state.getOutlineShape(ctx.getWorld(), absPos);

            double depth = shape.getBoundingBox().maxZ - shape.getBoundingBox().minZ;
            ctx.assertTrue(
                    depth <= 0.25,
                    "WarPoster outline depth (Z axis for NORTH facing) should be ≤ 0.25 blocks, got " + depth
            );
            ctx.complete();
        });
    }

    /**
     * Cycling through all four WarPosterBlock variants (RECRUITMENT, PROPAGANDA,
     * VICTORY, WARNING) does not throw an exception, confirming all blockstate
     * entries exist and the VARIANT property is registered.
     */
    @GameTest(templateName = ARENA_3X3)
    public void warPoster_allFourVariantsHaveBlockstateEntries(TestContext ctx) {
        BlockPos pos = new BlockPos(1, 1, 1);

        for (WarPosterBlock.PosterVariant variant : WarPosterBlock.PosterVariant.values()) {
            ctx.setBlockState(pos, ModBlocks.WAR_POSTER.getDefaultState()
                    .with(WarPosterBlock.VARIANT, variant));
        }

        ctx.waitAndRun(1, () -> {
            // If we reached here without an exception, all four variants are valid states.
            var state = ctx.getBlockState(pos);
            ctx.assertTrue(
                    state.isOf(ModBlocks.WAR_POSTER),
                    "Block should still be a WarPosterBlock after cycling all variants"
            );
            ctx.complete();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // GraveMarkerBlock
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Placing a grave marker on top of a solid stone block succeeds — the block
     * is registered and the state is retained.
     */
    @GameTest(templateName = ARENA_3X3)
    public void graveMarker_placesOnSolidSurface(TestContext ctx) {
        BlockPos stonePos  = new BlockPos(1, 1, 1);
        BlockPos markerPos = new BlockPos(1, 2, 1);

        ctx.setBlockState(stonePos,  Blocks.STONE.getDefaultState());
        ctx.setBlockState(markerPos, ModBlocks.GRAVE_MARKER.getDefaultState());

        ctx.waitAndRun(1, () -> {
            ctx.assertTrue(
                    ctx.getBlockState(markerPos).isOf(ModBlocks.GRAVE_MARKER),
                    "GraveMarker should remain placed on top of a solid stone block"
            );
            ctx.complete();
        });
    }

    /**
     * GraveMarkerBlock.canPlaceAt returns false when the block below is air,
     * preventing floating grave markers.
     */
    @GameTest(templateName = ARENA_5X5)
    public void graveMarker_cannotPlaceInAir(TestContext ctx) {
        // Pos (2,3,2) has air below it (arena floor is at y=0, stone at y=1 in 5x5,
        // so y=2 is air and y=3 is also air — well above the floor).
        BlockPos markerPos = new BlockPos(2, 3, 2);
        // Ensure below is air (default in the arena above the stone floor)
        ctx.setBlockState(new BlockPos(2, 2, 2), Blocks.AIR.getDefaultState());

        var state  = ModBlocks.GRAVE_MARKER.getDefaultState();
        var absPos = ctx.getAbsolutePos(markerPos);
        boolean canPlace = state.canPlaceAt(ctx.getWorld(), absPos);

        ctx.assertTrue(
                !canPlace,
                "GraveMarker canPlaceAt should return false when the block below is air"
        );
        ctx.complete();
    }

    /**
     * GraveMarker is a cross-shaped non-full-cube voxel. Its collision shape
     * must not span the full 1×1×1 cube.
     */
    @GameTest(templateName = ARENA_3X3)
    public void graveMarker_hasNonFullCubeOutline(TestContext ctx) {
        BlockPos stonePos  = new BlockPos(1, 1, 1);
        BlockPos markerPos = new BlockPos(1, 2, 1);

        ctx.setBlockState(stonePos,  Blocks.STONE.getDefaultState());
        ctx.setBlockState(markerPos, ModBlocks.GRAVE_MARKER.getDefaultState());

        ctx.waitAndRun(1, () -> {
            var state  = ctx.getBlockState(markerPos);
            var absPos = ctx.getAbsolutePos(markerPos);
            var shape  = state.getCollisionShape(ctx.getWorld(), absPos);

            // A full cube spans 0→1 on every axis. A cross shape is narrower on at
            // least the X or Z axis.
            boolean isNotFullCube = shape.isEmpty()
                    || shape.getBoundingBox().maxX < 1.0
                    || shape.getBoundingBox().maxZ < 1.0;
            ctx.assertTrue(
                    isNotFullCube,
                    "GraveMarker collision shape must not be a full 1×1×1 cube (cross voxel expected)"
            );
            ctx.complete();
        });
    }
}
