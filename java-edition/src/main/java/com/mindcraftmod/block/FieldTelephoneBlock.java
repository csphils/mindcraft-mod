package com.mindcraftmod.block;

import com.mindcraftmod.world.FactionManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Field Telephone — right-click to send a faction-scoped chat message
 * to all players in the same faction within 200 blocks.
 *
 * Directional (faces the player when placed).
 * Phase 6 will add a proper GUI; for now uses a server command workaround.
 */
public class FieldTelephoneBlock extends HorizontalFacingBlock {

    private static final VoxelShape SHAPE =
            Block.createCuboidShape(4, 0, 4, 12, 12, 12);

    /** Broadcast radius in blocks. */
    private static final double RANGE = 200.0;

    public FieldTelephoneBlock(Settings settings) {
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
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    // ── Shape ───────────────────────────────────────────────────────────────

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world,
                                      BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    // ── Interaction ─────────────────────────────────────────────────────────

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            // Phase 7: open GUI on client side
            return ActionResult.SUCCESS;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        FactionManager factions = FactionManager.get(serverWorld.getServer());
        FactionManager.Faction playerFaction = factions.getFaction(player.getUuid());

        if (playerFaction == FactionManager.Faction.NONE) {
            player.sendMessage(Text.literal("You must join a faction to use the Field Telephone.")
                    .formatted(Formatting.RED), true);
            return ActionResult.FAIL;
        }

        // Prompt — Phase 7 will open a proper screen
        // For now, inform the player of the faction and range
        player.sendMessage(Text.literal(String.format(
                "[Field Tel - %s] Right-click to talk (Phase 7: proper GUI coming)",
                playerFaction.displayName()))
                .formatted(Formatting.YELLOW), true);

        return ActionResult.SUCCESS;
    }

    /**
     * Broadcasts a message to all same-faction players within RANGE.
     * Called by Phase 7 GUI screen handler on message submit.
     */
    public static void broadcast(ServerWorld world, BlockPos phonePos,
                                  ServerPlayerEntity sender, String message) {
        FactionManager factions = FactionManager.get(world.getServer());
        FactionManager.Faction senderFaction = factions.getFaction(sender.getUuid());
        if (senderFaction == FactionManager.Faction.NONE) return;

        String header = String.format("[Field Tel - %s] %s: ",
                senderFaction.displayName(), sender.getName().getString());

        Text formatted = Text.literal(header).formatted(Formatting.GREEN)
                .append(Text.literal(message).formatted(Formatting.WHITE));

        for (ServerPlayerEntity recipient : world.getServer().getPlayerManager().getPlayerList()) {
            if (!factions.areSameFaction(sender.getUuid(), recipient.getUuid())) continue;
            if (recipient.getBlockPos().isWithinDistance(phonePos, RANGE)) {
                recipient.sendMessage(formatted);
            }
        }
    }
}
