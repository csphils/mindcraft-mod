package com.mindcraftmod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

/**
 * Grave Marker — small Latin cross, purely decorative.
 * Non-full-cube, requires a solid surface below, placed like a sign.
 */
public class GraveMarkerBlock extends HorizontalFacingBlock {

    public static final MapCodec<GraveMarkerBlock> CODEC = createCodec(GraveMarkerBlock::new);

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    // Cross shape: vertical shaft + horizontal crossbar
    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(6, 0, 6, 10, 16,  10), // vertical shaft
            Block.createCuboidShape(3, 8, 6, 13, 12, 10)   // crossbar
    );

    public GraveMarkerBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos below = pos.offset(Direction.DOWN);
        return world.getBlockState(below).isSideSolidFullSquare(world, below, Direction.UP);
    }

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
}
