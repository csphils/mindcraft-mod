package com.mindcraftmod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

/**
 * War Poster — thin decorative wall block with four propaganda variants.
 * Placed flat against a wall face, 2px deep.
 */
public class WarPosterBlock extends HorizontalFacingBlock {

    public static final MapCodec<WarPosterBlock> CODEC = createCodec(WarPosterBlock::new);

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public enum PosterVariant implements StringIdentifiable {
        RECRUITMENT("recruitment"),
        PROPAGANDA("propaganda"),
        VICTORY("victory"),
        WARNING("warning");

        private final String name;
        PosterVariant(String name) { this.name = name; }

        @Override
        public String asString() { return name; }
    }

    public static final EnumProperty<PosterVariant> VARIANT =
            EnumProperty.of("variant", PosterVariant.class);

    // 2px-deep slab shapes for each facing direction
    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(0, 0, 14, 16, 16, 16);
    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(0, 0,  0, 16, 16,  2);
    private static final VoxelShape SHAPE_EAST  = Block.createCuboidShape(0, 0,  0,  2, 16, 16);
    private static final VoxelShape SHAPE_WEST  = Block.createCuboidShape(14, 0, 0, 16, 16, 16);

    public WarPosterBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(VARIANT, PosterVariant.RECRUITMENT));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, VARIANT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case EAST  -> SHAPE_EAST;
            case WEST  -> SHAPE_WEST;
            default    -> SHAPE_NORTH;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world,
                                        BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }
}
