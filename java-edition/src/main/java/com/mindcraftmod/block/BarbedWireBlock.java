package com.mindcraftmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Barbed Wire — flat carpet-like pass-through block.
 *
 * - No collision (entities walk through)
 * - Applies Slowness II and 1 damage/sec while inside
 * - Only 2/16 height visually
 * - Hostile mobs ignore the slowness and damage
 */
public class BarbedWireBlock extends Block {

    // 2px-tall flat shape (for outline/hit detection only — no collision)
    private static final VoxelShape OUTLINE_SHAPE =
            Block.createCuboidShape(0, 0, 0, 16, 2, 16);

    public BarbedWireBlock(Settings settings) {
        super(settings);
    }

    // ── Shape overrides ─────────────────────────────────────────────────────

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world,
                                        BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty(); // no collision — entities pass through
    }

    // ── Effect on contact ───────────────────────────────────────────────────

    /**
     * Called each tick while an entity's bounding box overlaps this block.
     * Applies Slowness II and deals 1 damage per second (every 20 ticks).
     */
    @Override
    protected void onEntityCollision(BlockState state, World world,
                                     BlockPos pos, Entity entity) {
        if (world.isClient) return;
        if (!(entity instanceof LivingEntity living)) return;

        // Slowness II — refreshed each tick so it never runs out while inside
        living.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS, 30, 1, false, true));

        // Damage once per second (20 ticks). Use age as a cheap tick counter.
        if (entity.age % 20 == 0) {
            entity.damage(world.getDamageSources().thorns(entity), 1.0f);
        }
    }

    // ── Placement rules ─────────────────────────────────────────────────────

    /**
     * Barbed wire requires a solid surface beneath it (like a carpet).
     */
    @Override
    public boolean canPlaceAt(BlockState state, net.minecraft.world.WorldView world,
                               BlockPos pos) {
        BlockPos below = pos.offset(Direction.DOWN);
        return world.getBlockState(below).isSideSolidFullSquare(world, below, Direction.UP);
    }
}
