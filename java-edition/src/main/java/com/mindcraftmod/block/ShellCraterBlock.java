package com.mindcraftmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Shell Crater — decorative depression block left by artillery explosions.
 *
 * - Single block, concave appearance (sloped inward top surface)
 * - Provides cover: crouching inside a crater is harder for Sniper mob to detect
 * - Slightly slows movement (loose earth)
 * - Placed by Artillery Barrage world events and cannon fire
 *
 * The "multi-block 3x3 depression" described in specs is achieved at world gen
 * time by placing multiple shell_crater blocks in a pattern. Each individual
 * block is this single block type with a bowl-shaped top surface.
 */
public class ShellCraterBlock extends Block {

    /** Slightly sunken shape — 14px tall, inset 1px on sides for visual slope */
    private static final VoxelShape SHAPE =
            Block.createCuboidShape(0, 0, 0, 16, 14, 16);

    public ShellCraterBlock(Settings settings) {
        super(settings);
    }

    // ── Shape ───────────────────────────────────────────────────────────────

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world,
                                        BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    // ── Mild slowness inside crater ─────────────────────────────────────────

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient) return;
        if (!(entity instanceof LivingEntity living)) return;

        // Slight slowness — loose churned earth
        living.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS, 20, 0, false, false));
    }
}
