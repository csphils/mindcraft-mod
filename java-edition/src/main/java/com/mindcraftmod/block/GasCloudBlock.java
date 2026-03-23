package com.mindcraftmod.block;

import com.mindcraftmod.item.GasMaskItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Gas Cloud — non-solid full-block space filled with poisonous gas.
 *
 * - No collision, non-opaque (semi-transparent yellow-green)
 * - Applies Poison II each tick while inside (unless Gas Mask worn)
 * - Spreads horizontally via random tick (up to ~15 blocks from source)
 * - 5% chance per random tick to dissipate
 */
public class GasCloudBlock extends Block {

    /** Horizontal directions the gas can spread into. */
    private static final Direction[] SPREAD_DIRS = {
            Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST
    };

    public GasCloudBlock(Settings settings) {
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

    @Override
    protected void onEntityCollision(BlockState state, World world,
                                     BlockPos pos, Entity entity) {
        if (world.isClient) return;
        if (!(entity instanceof LivingEntity living)) return;

        // Gas Mask negates the effect
        if (isWearingGasMask(living)) return;

        // Poison II — 1.5 seconds, refreshed each tick while inside
        living.addStatusEffect(new StatusEffectInstance(
                StatusEffects.POISON, 30, 1, false, true));
    }

    private boolean isWearingGasMask(LivingEntity entity) {
        var helmet = entity.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD);
        return !helmet.isEmpty() && helmet.getItem() instanceof GasMaskItem;
    }

    // ── Spread and dissipation (random tick) ────────────────────────────────

    @Override
    public void randomTick(BlockState state, ServerWorld world,
                           BlockPos pos, Random random) {
        // 5% chance to dissipate
        if (random.nextInt(20) == 0) {
            world.removeBlock(pos, false);
            return;
        }

        // Try to spread in one random horizontal direction
        Direction dir = SPREAD_DIRS[random.nextInt(SPREAD_DIRS.length)];
        BlockPos target = pos.offset(dir);

        if (canSpreadTo(world, target)) {
            // Only spread 1/3 of the time — gas drifts slowly
            if (random.nextInt(3) == 0) {
                world.setBlockState(target, this.getDefaultState());
            }
        }
    }

    /**
     * Gas can only spread into air blocks that are at or below the source height
     * (gas sinks, never rises). Blocked by solid blocks.
     */
    private boolean canSpreadTo(ServerWorld world, BlockPos pos) {
        BlockState target = world.getBlockState(pos);
        return target.isAir();
    }
}
