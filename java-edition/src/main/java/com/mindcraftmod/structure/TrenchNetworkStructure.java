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
 * Trench Network — winding trench in Plains and Savanna biomes.
 *
 * Generates an L-shaped 30×5×3 open-topped trench dug into the surface.
 * Sandbag walls line the edges; barbed wire marks the perimeter.
 */
public class TrenchNetworkStructure extends Structure {

    public static final MapCodec<TrenchNetworkStructure> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(configCodecBuilder(instance))
                            .apply(instance, TrenchNetworkStructure::new));

    public TrenchNetworkStructure(Config config) {
        super(config);
    }

    @Override
    public Optional<StructurePosition> getStructurePosition(Context context) {
        int x = context.chunkPos().getCenterX();
        int z = context.chunkPos().getCenterZ();
        int y = context.chunkGenerator().getHeight(
                x, z, Heightmap.Type.WORLD_SURFACE_WG,
                context.heightAccessor(), context.randomState());

        BlockPos origin = new BlockPos(x, y, z);
        return Optional.of(new StructurePosition(origin, collector ->
                addPieces(collector, origin)));
    }

    private static void addPieces(StructurePiecesCollector collector, BlockPos origin) {
        // Main east–west arm (30 blocks long)
        collector.addPiece(new Piece(
                origin,
                new BlockBox(origin.getX(), origin.getY() - 3, origin.getZ(),
                        origin.getX() + 30, origin.getY() + 2, origin.getZ() + 4)));
    }

    @Override
    public StructureType<?> getType() {
        return ModStructures.TRENCH_NETWORK;
    }

    // ────────────────────────────────────────────────────────────────────────

    public static class Piece extends StructurePiece {

        private final BlockPos origin;

        public Piece(BlockPos origin, BlockBox boundingBox) {
            super(ModStructurePieceTypes.TRENCH_NETWORK_PIECE, 0, boundingBox);
            this.origin = origin;
        }

        /** NBT deserialization constructor. */
        public Piece(StructureContext ctx, NbtCompound nbt) {
            super(ModStructurePieceTypes.TRENCH_NETWORK_PIECE, nbt);
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

            int ox = origin.getX();
            int oy = origin.getY();
            int oz = origin.getZ();

            // Dig the trench: 30 blocks long (X), 3 blocks wide (Z), 3 blocks deep
            for (int dx = 0; dx <= 29; dx++) {
                for (int dz = 1; dz <= 3; dz++) {
                    for (int dy = -3; dy <= -1; dy++) {
                        BlockPos pos = new BlockPos(ox + dx, oy + dy, oz + dz);
                        if (chunkBox.contains(pos)) {
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                        }
                    }
                    // Trench floor
                    BlockPos floor = new BlockPos(ox + dx, oy - 4, oz + dz);
                    if (chunkBox.contains(floor)) {
                        world.setBlockState(floor, Blocks.GRAVEL.getDefaultState(), 3);
                    }
                }

                // Sandbag walls along the two long edges
                for (int wallY = 0; wallY <= 1; wallY++) {
                    BlockPos wallN = new BlockPos(ox + dx, oy + wallY, oz);
                    BlockPos wallS = new BlockPos(ox + dx, oy + wallY, oz + 4);
                    if (chunkBox.contains(wallN))
                        world.setBlockState(wallN, ModBlocks.SANDBAG.getDefaultState(), 3);
                    if (chunkBox.contains(wallS))
                        world.setBlockState(wallS, ModBlocks.SANDBAG.getDefaultState(), 3);
                }

                // Barbed wire perimeter, 1 block outside the sandbag walls
                BlockPos wireN = new BlockPos(ox + dx, oy + 1, oz - 1);
                BlockPos wireS = new BlockPos(ox + dx, oy + 1, oz + 5);
                if (chunkBox.contains(wireN))
                    world.setBlockState(wireN, ModBlocks.BARBED_WIRE.getDefaultState(), 3);
                if (chunkBox.contains(wireS))
                    world.setBlockState(wireS, ModBlocks.BARBED_WIRE.getDefaultState(), 3);
            }

            // Perpendicular south arm (10 blocks): an L-shape
            for (int dz = 5; dz <= 15; dz++) {
                for (int dx = 1; dx <= 3; dx++) {
                    for (int dy = -3; dy <= -1; dy++) {
                        BlockPos pos = new BlockPos(ox + dx, oy + dy, oz + dz);
                        if (chunkBox.contains(pos))
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                    }
                    BlockPos floor = new BlockPos(ox + dx, oy - 4, oz + dz);
                    if (chunkBox.contains(floor))
                        world.setBlockState(floor, Blocks.GRAVEL.getDefaultState(), 3);
                }
            }
        }
    }
}
