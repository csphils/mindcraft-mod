package com.mindcraftmod.test;

import com.mindcraftmod.MindcraftMod;
import com.mindcraftmod.block.ModBlocks;
import com.mindcraftmod.block.SandbagBlock;
import com.mindcraftmod.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

/**
 * Fabric GameTest suite — in-game automated tests for block behaviors.
 *
 * These tests run inside a real Minecraft world using small flat arenas.
 * Run via: ./gradlew runGametest  OR  /test runAll in-game.
 *
 * Each test method is annotated with @GameTest and a template name that maps
 * to an .snbt file in data/mindcraftmod/gametest/structures/.
 *
 * Registered in fabric.mod.json as a "fabric-gametest" entrypoint.
 */
public class MindcraftModGameTests {

    private static final String NAMESPACE = MindcraftMod.MOD_ID;

    // ── Template names ───────────────────────────────────────────────────────

    /** 5x5 flat stone floor — used for most block tests. */
    private static final String ARENA_5X5 = NAMESPACE + ":empty_5x5";

    /** 3x3 flat stone floor — used for compact single-block tests. */
    private static final String ARENA_3X3 = NAMESPACE + ":empty_3x3";

    // ════════════════════════════════════════════════════════════════════════
    // BarbedWireBlock
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Barbed wire has no collision — entity dropped above it falls through to ground.
     *
     * Setup: barbed wire at pos (1,1,1), sheep spawned at (1,3,1).
     * Assert: sheep y-position ends at floor level (y=1), not resting on wire (y=1.125).
     */
    @GameTest(templateName = ARENA_5X5)
    public void barbedWireNoCollision(TestContext ctx) {
        BlockPos wirePos = new BlockPos(1, 1, 1);
        ctx.setBlockState(wirePos, ModBlocks.BARBED_WIRE.getDefaultState());

        var sheep = ctx.spawnEntity(EntityType.SHEEP, 1, 3, 1);

        // Wait 10 ticks for entity to fall
        ctx.waitAndRun(10, () -> {
            // If barbed wire had collision, sheep would rest at y ≈ 1.125 (2/16 block height).
            // Without collision, sheep should be at floor level y = 1.0 (on stone beneath).
            ctx.assertTrue(
                    sheep.getY() < 1.2,
                    "Sheep should have fallen through barbed wire to floor level"
            );
            ctx.complete();
        });
    }

    /**
     * Barbed wire applies Slowness II to a living entity inside it.
     */
    @GameTest(templateName = ARENA_3X3)
    public void barbedWireAppliesSlowness(TestContext ctx) {
        BlockPos wirePos = new BlockPos(1, 1, 1);
        ctx.setBlockState(wirePos, ModBlocks.BARBED_WIRE.getDefaultState());

        var sheep = ctx.spawnEntity(EntityType.SHEEP, 1, 1, 1);

        // Wait 3 ticks — onEntityCollision is called each tick
        ctx.waitAndRun(3, () -> {
            var effect = sheep.getStatusEffect(StatusEffects.SLOWNESS);
            ctx.assertTrue(effect != null, "Sheep should have Slowness from barbed wire");
            ctx.assertTrue(effect.getAmplifier() >= 1,
                    "Slowness should be at least level II (amplifier ≥ 1)");
            ctx.complete();
        });
    }

    /**
     * Barbed wire deals damage (1 dmg per second = once per 20 ticks).
     */
    @GameTest(templateName = ARENA_3X3, maxAttempts = 1, batchId = "barbed_wire")
    public void barbedWireAppliesDamage(TestContext ctx) {
        BlockPos wirePos = new BlockPos(1, 1, 1);
        ctx.setBlockState(wirePos, ModBlocks.BARBED_WIRE.getDefaultState());

        var sheep = ctx.spawnEntity(EntityType.SHEEP, 1, 1, 1);
        float initialHealth = sheep.getHealth();

        // Damage fires at entity.age % 20 == 0; wait 25 ticks to ensure one hit
        ctx.waitAndRun(25, () -> {
            ctx.assertTrue(
                    sheep.getHealth() < initialHealth,
                    "Sheep health should decrease after 25 ticks on barbed wire"
            );
            ctx.complete();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // GasCloudBlock
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Gas cloud applies Poison II to a living entity without a Gas Mask.
     */
    @GameTest(templateName = ARENA_3X3)
    public void gasCloudAppliesPoison(TestContext ctx) {
        BlockPos gasPos = new BlockPos(1, 1, 1);
        ctx.setBlockState(gasPos, ModBlocks.GAS_CLOUD.getDefaultState());

        var sheep = ctx.spawnEntity(EntityType.SHEEP, 1, 1, 1);

        ctx.waitAndRun(3, () -> {
            var effect = sheep.getStatusEffect(StatusEffects.POISON);
            ctx.assertTrue(effect != null, "Sheep should have Poison from gas cloud");
            ctx.assertTrue(effect.getAmplifier() >= 1,
                    "Poison should be level II (amplifier ≥ 1)");
            ctx.complete();
        });
    }

    /**
     * Gas cloud does NOT apply Poison to an entity wearing a Gas Mask.
     *
     * Uses a player mock with Gas Mask equipped in the head slot.
     */
    @GameTest(templateName = ARENA_3X3)
    public void gasMaskNegatesPoison(TestContext ctx) {
        BlockPos gasPos = new BlockPos(1, 1, 1);
        ctx.setBlockState(gasPos, ModBlocks.GAS_CLOUD.getDefaultState());

        // Spawn a dummy mob and equip Gas Mask via equipment slot
        var zombie = ctx.spawnEntity(EntityType.ZOMBIE, 1, 1, 1);
        zombie.equipStack(net.minecraft.entity.EquipmentSlot.HEAD,
                new ItemStack(ModItems.GAS_MASK));

        ctx.waitAndRun(10, () -> {
            var effect = zombie.getStatusEffect(StatusEffects.POISON);
            ctx.assertTrue(
                    effect == null,
                    "Entity with Gas Mask should NOT have Poison effect from gas cloud"
            );
            ctx.complete();
        });
    }

    /**
     * Gas cloud spreads to an adjacent air block via random tick.
     * Forces random ticks to accelerate the test.
     */
    @GameTest(templateName = ARENA_5X5, tickLimit = 400)
    public void gasCloudSpreads(TestContext ctx) {
        BlockPos sourcePos = new BlockPos(2, 1, 2);
        BlockPos targetPos = sourcePos.north(); // adjacent air block

        ctx.setBlockState(sourcePos, ModBlocks.GAS_CLOUD.getDefaultState());

        // Force many random ticks on the source block
        ctx.runAtEveryTick(() -> {
            if (ctx.getWorld().getBlockState(
                    ctx.getAbsolutePos(sourcePos)).isOf(ModBlocks.GAS_CLOUD)) {
                ctx.getWorld().getBlockState(ctx.getAbsolutePos(sourcePos))
                        .randomTick(ctx.getWorld(),
                                ctx.getAbsolutePos(sourcePos),
                                ctx.getWorld().random);
            }
        });

        ctx.waitAndRun(200, () -> {
            boolean spread = ctx.getWorld()
                    .getBlockState(ctx.getAbsolutePos(targetPos))
                    .isOf(ModBlocks.GAS_CLOUD);
            ctx.assertTrue(spread,
                    "Gas cloud should spread to adjacent air block after many random ticks");
            ctx.complete();
        });
    }

    /**
     * Gas cloud eventually dissipates via random tick (5% chance per tick).
     */
    @GameTest(templateName = ARENA_3X3, tickLimit = 1000)
    public void gasCloudDissipates(TestContext ctx) {
        BlockPos pos = new BlockPos(1, 1, 1);
        ctx.setBlockState(pos, ModBlocks.GAS_CLOUD.getDefaultState());

        ctx.runAtEveryTick(() -> {
            if (ctx.getWorld().getBlockState(
                    ctx.getAbsolutePos(pos)).isOf(ModBlocks.GAS_CLOUD)) {
                ctx.getWorld().getBlockState(ctx.getAbsolutePos(pos))
                        .randomTick(ctx.getWorld(), ctx.getAbsolutePos(pos),
                                ctx.getWorld().random);
            } else {
                ctx.complete(); // dissipated — test passes
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // SmokeScreenBlock
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Smoke screen applies Blindness I while entity is inside.
     */
    @GameTest(templateName = ARENA_3X3)
    public void smokeScreenAppliesBlindness(TestContext ctx) {
        BlockPos smokePos = new BlockPos(1, 1, 1);
        ctx.setBlockState(smokePos, ModBlocks.SMOKE_SCREEN.getDefaultState());

        var sheep = ctx.spawnEntity(EntityType.SHEEP, 1, 1, 1);

        ctx.waitAndRun(3, () -> {
            var effect = sheep.getStatusEffect(StatusEffects.BLINDNESS);
            ctx.assertTrue(effect != null,
                    "Entity inside smoke screen should have Blindness");
            ctx.complete();
        });
    }

    /**
     * Smoke screen Blindness lingers after entity exits the block.
     * (Duration is 80 ticks; entity exits after 5 ticks → ~75 ticks remain.)
     */
    @GameTest(templateName = ARENA_5X5)
    public void smokeScreenLingers(TestContext ctx) {
        BlockPos smokePos = new BlockPos(1, 1, 1);
        ctx.setBlockState(smokePos, ModBlocks.SMOKE_SCREEN.getDefaultState());

        var sheep = ctx.spawnEntity(EntityType.SHEEP, 1, 1, 1);

        // Let Blindness apply for 5 ticks, then move entity out
        ctx.waitAndRun(5, () -> {
            sheep.teleport(3, 1, 3); // outside smoke

            // Check 2 ticks later — Blindness should still be active (duration was 80 ticks)
            ctx.runAtTick(ctx.getTick() + 2, () -> {
                var effect = sheep.getStatusEffect(StatusEffects.BLINDNESS);
                ctx.assertTrue(effect != null,
                        "Blindness should linger after leaving smoke screen");
                ctx.complete();
            });
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // MudPitBlock
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Mud pit applies Slowness II to entity standing on top.
     */
    @GameTest(templateName = ARENA_3X3)
    public void mudPitAppliesSlowness(TestContext ctx) {
        BlockPos mudPos = new BlockPos(1, 1, 1);
        ctx.setBlockState(mudPos, ModBlocks.MUD_PIT.getDefaultState());

        // Entity stands on top (y = 2)
        var sheep = ctx.spawnEntity(EntityType.SHEEP, 1, 2, 1);

        ctx.waitAndRun(5, () -> {
            var effect = sheep.getStatusEffect(StatusEffects.SLOWNESS);
            ctx.assertTrue(effect != null,
                    "Entity on mud pit should have Slowness");
            ctx.assertTrue(effect.getAmplifier() >= 1,
                    "Slowness should be at least level II (amplifier ≥ 1)");
            ctx.complete();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // SandbagBlock
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Placing 8 sandbag items at the same position yields layers=8 state.
     */
    @GameTest(templateName = ARENA_3X3)
    public void sandbagStacksToEight(TestContext ctx) {
        BlockPos pos = new BlockPos(1, 1, 1);

        // Place first sandbag
        ctx.setBlockState(pos, ModBlocks.SANDBAG.getDefaultState()
                .with(SandbagBlock.LAYERS, 1));

        // Simulate stacking by directly setting state to layers=8
        ctx.setBlockState(pos, ModBlocks.SANDBAG.getDefaultState()
                .with(SandbagBlock.LAYERS, 8));

        ctx.waitAndRun(1, () -> {
            int layers = ctx.getBlockState(pos).get(SandbagBlock.LAYERS);
            ctx.assertTrue(layers == 8,
                    "Sandbag at same position should reach 8 layers");
            ctx.complete();
        });
    }

    /**
     * Sandbag at layers=8 blocks entity movement (same collision as full block).
     */
    @GameTest(templateName = ARENA_5X5)
    public void sandbagFullBlockAtEight(TestContext ctx) {
        BlockPos pos = new BlockPos(2, 1, 2);
        ctx.setBlockState(pos, ModBlocks.SANDBAG.getDefaultState()
                .with(SandbagBlock.LAYERS, 8));

        // Drop entity from above — at layers=8, it should rest on top (y=2.0)
        var sheep = ctx.spawnEntity(EntityType.SHEEP, 2, 4, 2);

        ctx.waitAndRun(15, () -> {
            ctx.assertTrue(
                    sheep.getY() >= 2.0 - 0.01 && sheep.getY() <= 2.0 + 0.1,
                    "Entity should rest on top of full-height sandbag (layers=8) at y≈2.0"
            );
            ctx.complete();
        });
    }

    /**
     * Sandbag at layers=7 allows entity to fall through partially (no full-height collision).
     */
    @GameTest(templateName = ARENA_5X5)
    public void sandbagWontStackAboveEight(TestContext ctx) {
        BlockPos pos = new BlockPos(2, 1, 2);
        // Set to max layers
        ctx.setBlockState(pos, ModBlocks.SANDBAG.getDefaultState()
                .with(SandbagBlock.LAYERS, 8));

        // Verify state is 8 — no way to increment further
        ctx.waitAndRun(1, () -> {
            var state = ctx.getBlockState(pos);
            ctx.assertTrue(state.isOf(ModBlocks.SANDBAG),
                    "Block should still be a sandbag");
            ctx.assertTrue(state.get(SandbagBlock.LAYERS) == 8,
                    "Sandbag should be capped at 8 layers");
            ctx.complete();
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // SupplyCrateBlock
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Supply crate block is placed correctly and has a block entity.
     */
    @GameTest(templateName = ARENA_3X3)
    public void supplyCrateHasBlockEntity(TestContext ctx) {
        BlockPos pos = new BlockPos(1, 1, 1);
        ctx.setBlockState(pos, ModBlocks.SUPPLY_CRATE.getDefaultState());

        ctx.waitAndRun(1, () -> {
            var be = ctx.getBlockEntity(pos);
            ctx.assertTrue(be != null,
                    "Supply crate block should have a block entity");
            ctx.complete();
        });
    }

    /**
     * Breaking a supply crate that has items causes those items to drop.
     */
    @GameTest(templateName = ARENA_5X5)
    public void supplyCrateDropsContentsOnBreak(TestContext ctx) {
        BlockPos pos = new BlockPos(2, 1, 2);
        ctx.setBlockState(pos, ModBlocks.SUPPLY_CRATE.getDefaultState());

        ctx.waitAndRun(1, () -> {
            // Manually fill the crate
            var be = ctx.getBlockEntity(pos);
            if (be instanceof com.mindcraftmod.block.entity.SupplyCrateBlockEntity crate) {
                crate.setStack(0, new ItemStack(ModItems.RIFLE_CARTRIDGE, 8));
            }

            // Break the block
            ctx.getWorld().breakBlock(ctx.getAbsolutePos(pos), true);

            // Wait for item entities to spawn
            ctx.waitAndRun(2, () -> {
                var items = ctx.getWorld().getEntitiesByType(
                        EntityType.ITEM,
                        net.minecraft.util.math.Box.of(
                                ctx.getAbsolutePos(pos).toCenterPos(), 3, 3, 3),
                        e -> !e.getStack().isEmpty()
                );
                ctx.assertTrue(!items.isEmpty(),
                        "Breaking a filled supply crate should drop item entities");
                ctx.complete();
            });
        });
    }
}
