package com.mindcraftmod.structure;

import com.mindcraftmod.block.ModBlocks;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

/**
 * Command Bunker — 3-room underground complex, typically under a Trench Network.
 *
 * Generates 3 horizontally connected rooms (5×3×5 each) carved 5–8 blocks underground.
 * Each room has cobblestone walls, oak plank floors, a torch, and a Supply Crate.
 */
public class CommandBunkerStructure extends Structure {

    public static final MapCodec<CommandBunkerStructure> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(configCodecBuilder(instance))
                            .apply(instance, CommandBunkerStructure::new));

    public CommandBunkerStructure(Config config) {
        super(config);
    }

    @Override
    public Optional<StructurePosition> getStructurePosition(Context context) {
        int x = context.chunkPos().getCenterX();
        int z = context.chunkPos().getCenterZ();
        int surfaceY = context.chunkGenerator().getHeight(
                x, z, Heightmap.Type.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState());

        BlockPos origin = new BlockPos(x, surfaceY - 8, z);
        return Optional.of(new StructurePosition(origin, collector ->
                addPieces(collector, origin)));
    }

    private static void addPieces(StructurePiecesCollector collector, BlockPos origin) {
        collector.addPiece(new Piece(
                origin,
                new BlockBox(origin.getX() - 1, origin.getY() - 1, origin.getZ() - 1,
                        origin.getX() + 19, origin.getY() + 4, origin.getZ() + 6)));
    }

    @Override
    public StructureType<?> getType() {
        return ModStructures.COMMAND_BUNKER;
    }

    // ────────────────────────────────────────────────────────────────────────

    public static class Piece extends StructurePiece {

        private final BlockPos origin;

        public Piece(BlockPos origin, BlockBox boundingBox) {
            super(ModStructurePieceTypes.COMMAND_BUNKER_PIECE, 0, boundingBox);
            this.origin = origin;
        }

        public Piece(StructureContext ctx, NbtCompound nbt) {
            super(ModStructurePieceTypes.COMMAND_BUNKER_PIECE, nbt);
            this.origin = new BlockPos(
                    nbt.getInt("OriginX"), nbt.getInt("OriginY"), nbt.getInt("OriginZ"));
        }

        @Override
        protected void writeNbt(StructureContext ctx, NbtCompound nbt) {
            nbt.putInt("OriginX", origin.getX());
            nbt.putInt("OriginY", origin.getY());
            nbt.putInt("OriginZ", origin.getZ());
        }

        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor,
                             ChunkGenerator chunkGenerator, Random random,
                             BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            // Three 5×3×5 rooms connected in a row along X-axis
            for (int room = 0; room < 3; room++) {
                int roomOriginX = origin.getX() + room * 6;
                buildRoom(world, chunkBox, roomOriginX, origin.getY(), origin.getZ(), room);
            }
        }

        /** Carves a single 5×3×5 room with cobblestone walls/ceiling and plank floor. */
        private void buildRoom(StructureWorldAccess world, BlockBox chunkBox,
                               int rx, int ry, int rz, int roomIndex) {
            for (int dx = 0; dx <= 4; dx++) {
                for (int dy = 0; dy <= 3; dy++) {
                    for (int dz = 0; dz <= 4; dz++) {
                        BlockPos pos = new BlockPos(rx + dx, ry + dy, rz + dz);
                        if (!chunkBox.contains(pos)) continue;

                        boolean isWall = dx == 0 || dx == 4 || dz == 0 || dz == 4;
                        boolean isCeiling = dy == 3;
                        boolean isFloor = dy == 0;

                        if (isFloor) {
                            world.setBlockState(pos, Blocks.OAK_PLANKS.getDefaultState(), 3);
                        } else if (isCeiling || isWall) {
                            world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState(), 3);
                        } else {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                        }
                    }
                }
            }

            // Torch on the north wall (center)
            BlockPos torchPos = new BlockPos(rx + 2, ry + 2, rz + 1);
            if (chunkBox.contains(torchPos))
                world.setBlockState(torchPos, Blocks.WALL_TORCH.getDefaultState(), 3);

            // Supply crate in the corner of each room
            BlockPos cratePos = new BlockPos(rx + 1, ry + 1, rz + 1);
            if (chunkBox.contains(cratePos))
                world.setBlockState(cratePos, ModBlocks.SUPPLY_CRATE.getDefaultState(), 3);

            // Doorway to the next room (open right wall, centered)
            if (roomIndex < 2) {
                for (int dy = 1; dy <= 2; dy++) {
                    BlockPos door = new BlockPos(rx + 4, ry + dy, rz + 2);
                    if (chunkBox.contains(door))
                        world.setBlockState(door, Blocks.AIR.getDefaultState(), 3);
                }
            }
        }
    }
}
