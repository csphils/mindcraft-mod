package com.mindcraftmod.block;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/**
 * Sandbag — stackable fortification block with 1-8 layers (like Snow).
 *
 * - 8 layers, each 2 pixels (1/8 block) tall
 * - At 8 layers = full block
 * - Blast resistance 6 (absorbs explosions better than dirt)
 * - Waterloggable
 * - Crafting: 4 Sand + 2 String → 2 Sandbag
 */
public class SandbagBlock extends Block implements Waterloggable {

    public static final IntProperty LAYERS =
            IntProperty.of("layers", 1, 8);

    /**
     * Height in pixels (out of 16) for each layer index 0-8.
     * Index 0 unused; index 8 = 16px (full block).
     * Exposed package-visible for unit tests in SandbagBlockLogicTest.
     */
    static final int[] LAYER_HEIGHT_PX = { 0, 2, 4, 6, 8, 10, 12, 14, 16 };

    /** Height in pixels (out of 16) per layer count. */
    private static final VoxelShape[] LAYER_TO_SHAPE = new VoxelShape[]{
            VoxelShapes.empty(),                                   // 0 (unused)
            Block.createCuboidShape(0, 0, 0, 16, 2,  16),  // 1 layer  — 2px
            Block.createCuboidShape(0, 0, 0, 16, 4,  16),  // 2 layers — 4px
            Block.createCuboidShape(0, 0, 0, 16, 6,  16),  // 3 layers — 6px
            Block.createCuboidShape(0, 0, 0, 16, 8,  16),  // 4 layers — 8px
            Block.createCuboidShape(0, 0, 0, 16, 10, 16),  // 5 layers — 10px
            Block.createCuboidShape(0, 0, 0, 16, 12, 16),  // 6 layers — 12px
            Block.createCuboidShape(0, 0, 0, 16, 14, 16),  // 7 layers — 14px
            VoxelShapes.fullCube(),                                 // 8 layers — full
    };

    public SandbagBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(LAYERS, 1)
                .with(Properties.WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LAYERS, Properties.WATERLOGGED);
    }

    // ── Shape ───────────────────────────────────────────────────────────────

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        return LAYER_TO_SHAPE[state.get(LAYERS)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world,
                                        BlockPos pos, ShapeContext context) {
        return LAYER_TO_SHAPE[state.get(LAYERS)];
    }

    @Override
    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return LAYER_TO_SHAPE[state.get(LAYERS)];
    }

    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return state.get(LAYERS) < 8;
    }

    // ── Placement — stack sandbags ───────────────────────────────────────────

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState existing = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (existing.isOf(this)) {
            int current = existing.get(LAYERS);
            if (current < 8) {
                // Stack on existing sandbag
                boolean waterlogged = existing.get(Properties.WATERLOGGED);
                return existing.with(LAYERS, current + 1).with(Properties.WATERLOGGED, waterlogged);
            }
            return null; // Already full
        }

        boolean waterlogged = ctx.getWorld()
                .getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER);
        return getDefaultState()
                .with(LAYERS, 1)
                .with(Properties.WATERLOGGED, waterlogged);
    }

    /**
     * Allow placing into an existing sandbag block to increase layer count.
     */
    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (context.getStack().getItem() instanceof BlockItem bi && bi.getBlock() == this) {
            return state.get(LAYERS) < 8;
        }
        return false;
    }

    // ── Full block at 8 layers ───────────────────────────────────────────────

    @Override
    public boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    // ── Waterlogging ─────────────────────────────────────────────────────────

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED)
                ? Fluids.WATER.getStill(false)
                : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, WorldView world,
                                                ScheduledTickView tickView, BlockPos pos,
                                                Direction direction, BlockPos neighborPos,
                                                BlockState neighborState, Random random) {
        if (state.get(Properties.WATERLOGGED)) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    // ── Pathfinding ──────────────────────────────────────────────────────────

    @Override
    public boolean canPathfindThrough(BlockState state, NavigationType type) {
        return type == NavigationType.LAND && state.get(LAYERS) < 5;
    }
}
