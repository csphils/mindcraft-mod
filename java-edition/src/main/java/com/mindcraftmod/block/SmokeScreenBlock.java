package com.mindcraftmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Smoke Screen — non-solid block that applies Blindness while inside.
 *
 * - No collision, non-opaque (dark gray)
 * - Applies Blindness I each tick while inside
 * - Applies Blindness I for 3 seconds (60 ticks) after leaving (lingering)
 * - Dissipates over ~30 seconds via random tick (20% per tick)
 * - Does NOT spread — placed in a 3x3x3 area by Signal Flare (Gray)
 */
public class SmokeScreenBlock extends Block {

    public SmokeScreenBlock(Settings settings) {
        super(settings);
    }

    // ── Shape ───────────────────────────────────────────────────────────────

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world,
                                        BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    // ── Effect on contact ───────────────────────────────────────────────────

    /**
     * Applies Blindness with a longer duration than the refresh rate
     * so that the effect persists for 3 seconds after the entity exits.
     */
    @Override
    protected void onEntityCollision(BlockState state, World world,
                                     BlockPos pos, Entity entity) {
        if (world.isClient) return;
        if (!(entity instanceof LivingEntity living)) return;

        // Blindness I — 4 seconds (80 ticks) duration.
        // Entity spends 1 tick in block → gets 4s of Blindness.
        // While inside, this refreshes each tick keeping it at 4s.
        // After leaving, the remaining ~3s runs down naturally.
        living.addStatusEffect(new StatusEffectInstance(
                StatusEffects.BLINDNESS, 80, 0, false, true));
    }

    // ── Dissipation (random tick) ────────────────────────────────────────────

    @Override
    public void randomTick(BlockState state, ServerWorld world,
                           BlockPos pos, Random random) {
        // 20% chance to dissipate per random tick — fully gone in ~30 seconds on average
        if (random.nextInt(5) == 0) {
            world.removeBlock(pos, false);
        }
    }
}
