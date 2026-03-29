package com.mindcraftmod.block;

import com.mindcraftmod.block.entity.SupplyCrateBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Supply Crate — a 27-slot loot chest that pre-fills from a loot table
 * on first open. Found in structures; also dropped by Supply Drop events.
 *
 * Does not double like a vanilla chest.
 */
public class SupplyCrateBlock extends BlockWithEntity {

    public static final MapCodec<SupplyCrateBlock> CODEC = createCodec(SupplyCrateBlock::new);

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public SupplyCrateBlock(Settings settings) {
        super(settings);
    }

    // ── Block entity ─────────────────────────────────────────────────────────

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SupplyCrateBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    // ── Interaction ─────────────────────────────────────────────────────────

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof NamedScreenHandlerFactory factory) {
            player.openHandledScreen(factory);
        }
        return ActionResult.CONSUME;
    }

    // ── Drop contents on break ───────────────────────────────────────────────

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos,
                                BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SupplyCrateBlockEntity crate) {
                dropContents(world, pos, crate);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    private void dropContents(World world, BlockPos pos, SupplyCrateBlockEntity crate) {
        for (int i = 0; i < crate.size(); i++) {
            dropStack(world, pos, crate.getStack(i));
        }
        crate.clear();
    }
}
