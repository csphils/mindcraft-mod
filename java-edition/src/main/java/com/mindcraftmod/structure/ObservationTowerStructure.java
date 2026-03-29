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
 * Observation Tower — 15-block tall lookout in forests.
 *
 * Structure: 5×5 base of spruce logs, open-air observation platform at the top
 * with oak fence railing, ladder access, loot chest at base, Sniper spawner at top.
 */
public class ObservationTowerStructure extends Structure {

    public static final MapCodec<ObservationTowerStructure> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(configCodecBuilder(instance))
                            .apply(instance, ObservationTowerStructure::new));

    public ObservationTowerStructure(Config config) {
        super(config);
    }

    @Override
    public Optional<StructurePosition> getStructurePosition(Context context) {
        int x = context.chunkPos().getCenterX();
        int z = context.chunkPos().getCenterZ();
        int y = context.chunkGenerator().getHeight(
                x, z, Heightmap.Type.WORLD_SURFACE_WG,
                context.world(), context.noiseConfig());

        BlockPos origin = new BlockPos(x, y, z);
        return Optional.of(new StructurePosition(origin, collector ->
                addPieces(collector, origin)));
    }

    private static void addPieces(StructurePiecesCollector collector, BlockPos origin) {
        collector.addPiece(new Piece(
                origin,
                new BlockBox(origin.getX(), origin.getY(), origin.getZ(),
                        origin.getX() + 4, origin.getY() + 17, origin.getZ() + 4)));
    }

    @Override
    public StructureType<?> getType() {
        return ModStructures.OBSERVATION_TOWER;
    }

    // ────────────────────────────────────────────────────────────────────────

    public static class Piece extends StructurePiece {

        private final BlockPos origin;

        public Piece(BlockPos origin, BlockBox boundingBox) {
            super(ModStructurePieceTypes.OBSERVATION_TOWER_PIECE, 0, boundingBox);
            this.origin = origin;
        }

        public Piece(StructureContext ctx, NbtCompound nbt) {
            super(ModStructurePieceTypes.OBSERVATION_TOWER_PIECE, nbt);
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

            // 4 corner spruce log pillars, 15 blocks tall
            for (int dy = 0; dy <= 14; dy++) {
                place(world, chunkBox, Blocks.SPRUCE_LOG.getDefaultState(), ox,     oy + dy, oz);
                place(world, chunkBox, Blocks.SPRUCE_LOG.getDefaultState(), ox + 4, oy + dy, oz);
                place(world, chunkBox, Blocks.SPRUCE_LOG.getDefaultState(), ox,     oy + dy, oz + 4);
                place(world, chunkBox, Blocks.SPRUCE_LOG.getDefaultState(), ox + 4, oy + dy, oz + 4);
            }

            // Ladder on the north face (pillar at x=0, z=0) for access
            for (int dy = 1; dy <= 14; dy++) {
                place(world, chunkBox, Blocks.LADDER.getDefaultState(), ox + 1, oy + dy, oz + 1);
            }

            // Observation platform at the top (floors + fence railing)
            for (int dx = 0; dx <= 4; dx++) {
                for (int dz = 0; dz <= 4; dz++) {
                    place(world, chunkBox, Blocks.OAK_PLANKS.getDefaultState(),
                            ox + dx, oy + 15, oz + dz);
                    // Fence railing around the perimeter
                    if (dx == 0 || dx == 4 || dz == 0 || dz == 4) {
                        place(world, chunkBox, Blocks.OAK_FENCE.getDefaultState(),
                                ox + dx, oy + 16, oz + dz);
                    }
                }
            }

            // Sniper spawner on the platform
            place(world, chunkBox, Blocks.SPAWNER.getDefaultState(), ox + 2, oy + 16, oz + 2);

            // Loot chest at ground level
            place(world, chunkBox, Blocks.CHEST.getDefaultState(), ox + 2, oy + 1, oz + 2);

            // Sandbag wall at the base for flavour
            for (int dx = 0; dx <= 4; dx++) {
                place(world, chunkBox, ModBlocks.SANDBAG.getDefaultState(), ox + dx, oy, oz - 1);
                place(world, chunkBox, ModBlocks.SANDBAG.getDefaultState(), ox + dx, oy, oz + 5);
            }
        }

        private void place(StructureWorldAccess world, BlockBox chunkBox,
                           net.minecraft.block.BlockState state, int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            if (chunkBox.contains(pos))
                world.setBlockState(pos, state, 3);
        }
    }
}
