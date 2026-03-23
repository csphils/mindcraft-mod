package com.mindcraftmod.block;

import com.mindcraftmod.world.FactionManager;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Flag Block — territorial marker for the faction system.
 *
 * Variants: ALLIED (blue), CENTRAL (red), NEUTRAL (white).
 * Generated in Trench Networks; players capture by standing nearby 2 minutes.
 *
 * Territory capture logic is tracked in Phase 6 via server-side tick events.
 * For Phase 2, this block stores faction state and shows info on right-click.
 */
public class FlagBlock extends HorizontalFacingBlock {

    public static final MapCodec<FlagBlock> CODEC = createCodec(FlagBlock::new);

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    /** Which faction owns this flag. */
    public enum FlagFaction implements StringIdentifiable {
        NEUTRAL("neutral"),
        ALLIED("allied"),
        CENTRAL("central");

        private final String name;

        FlagFaction(String name) { this.name = name; }

        @Override
        public String asString() { return name; }
    }

    public static final EnumProperty<FlagFaction> FACTION =
            EnumProperty.of("faction", FlagFaction.class);

    private static final VoxelShape SHAPE =
            Block.createCuboidShape(6, 0, 6, 10, 16, 10);

    public FlagBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FACTION, FlagFaction.NEUTRAL));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACTION);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(FACTION, FlagFaction.NEUTRAL);
    }

    // ── Shape ───────────────────────────────────────────────────────────────

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    // ── Interaction — show territory status ─────────────────────────────────

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        FlagFaction flagFaction = state.get(FACTION);
        String ownerDisplay = switch (flagFaction) {
            case ALLIED  -> "§9Allies§r";
            case CENTRAL -> "§cCentral Powers§r";
            case NEUTRAL -> "§7Unclaimed§r";
        };

        player.sendMessage(
                Text.literal("Territory: ").formatted(Formatting.YELLOW)
                        .append(Text.literal(ownerDisplay)),
                true // action bar
        );

        // Phase 6: territory capture timer logic added here
        return ActionResult.SUCCESS;
    }

    /**
     * Converts this flag to the given faction's color.
     * Called by Phase 6 FlagCaptureManager when capture timer completes.
     */
    public static void capture(ServerWorld world, BlockPos pos,
                                FactionManager.Faction newOwner) {
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof FlagBlock)) return;

        FlagFaction newFlagFaction = switch (newOwner) {
            case ALLIES         -> FlagFaction.ALLIED;
            case CENTRAL_POWERS -> FlagFaction.CENTRAL;
            default             -> FlagFaction.NEUTRAL;
        };

        world.setBlockState(pos, state.with(FACTION, newFlagFaction));
    }
}
