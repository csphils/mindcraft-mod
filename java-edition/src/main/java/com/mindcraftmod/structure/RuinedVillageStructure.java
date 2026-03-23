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
 * Ruined Village — scattered destroyed buildings with shell craters.
 *
 * Generates 3–4 ruined cobblestone houses (partial walls, missing roofs),
 * shell craters between them, and abandoned supply crates.
 */
public class RuinedVillageStructure extends Structure {

    public static final MapCodec<RuinedVillageStructure> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(configCodecBuilder(instance))
                            .apply(instance, RuinedVillageStructure::new));

    public RuinedVillageStructure(Config config) {
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
                new BlockBox(origin.getX() - 5, origin.getY() - 2, origin.getZ() - 5,
                        origin.getX() + 35, origin.getY() + 5, origin.getZ() + 35)));
    }

    @Override
    public StructureType<?> getType() {
        return ModStructures.RUINED_VILLAGE;
    }

    // ────────────────────────────────────────────────────────────────────────

    public static class Piece extends StructurePiece {

        private final BlockPos origin;

        public Piece(BlockPos origin, BlockBox boundingBox) {
            super(ModStructurePieceTypes.RUINED_VILLAGE_PIECE, 0, boundingBox);
            this.origin = origin;
        }

        public Piece(StructureContext ctx, NbtCompound nbt) {
            super(ModStructurePieceTypes.RUINED_VILLAGE_PIECE, nbt);
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

            // Four ruined houses placed at offsets
            buildRuinedHouse(world, chunkBox, random, ox,      oy, oz);
            buildRuinedHouse(world, chunkBox, random, ox + 12, oy, oz);
            buildRuinedHouse(world, chunkBox, random, ox,      oy, oz + 12);
            buildRuinedHouse(world, chunkBox, random, ox + 14, oy, oz + 14);

            // Shell craters between the houses
            buildCrater(world, chunkBox, ox + 7,  oy, oz + 7);
            buildCrater(world, chunkBox, ox + 20, oy, oz + 5);

            // Abandoned supply crates
            place(world, chunkBox, ModBlocks.SUPPLY_CRATE.getDefaultState(), ox + 6,  oy + 1, oz + 3);
            place(world, chunkBox, ModBlocks.SUPPLY_CRATE.getDefaultState(), ox + 22, oy + 1, oz + 18);
        }

        /** Generates a partially-collapsed 7×5×5 cobblestone house. */
        private void buildRuinedHouse(StructureWorldAccess world, BlockBox chunkBox,
                                      Random random, int rx, int ry, int rz) {
            for (int dx = 0; dx <= 6; dx++) {
                for (int dz = 0; dz <= 4; dz++) {
                    for (int dy = 0; dy <= 4; dy++) {
                        boolean isWall = dx == 0 || dx == 6 || dz == 0 || dz == 4;
                        boolean isFloor = dy == 0;
                        if (!isFloor && !isWall) continue;

                        // Skip some blocks for the "ruined" look (30% chance to omit wall block)
                        if (isWall && dy >= 2 && random.nextFloat() < 0.3f) continue;

                        BlockPos pos = new BlockPos(rx + dx, ry + dy, rz + dz);
                        if (chunkBox.contains(pos))
                            world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState(), 3);
                    }
                }
            }
            // Doorway
            place(world, chunkBox, Blocks.AIR.getDefaultState(), rx, ry + 1, rz + 2);
            place(world, chunkBox, Blocks.AIR.getDefaultState(), rx, ry + 2, rz + 2);
        }

        /** Generates a 5×2×5 shell crater depression. */
        private void buildCrater(StructureWorldAccess world, BlockBox chunkBox,
                                 int cx, int cy, int cz) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    int depth = (Math.abs(dx) + Math.abs(dz) <= 2) ? 2 : 1;
                    for (int dy = 0; dy <= depth; dy++) {
                        BlockPos pos = new BlockPos(cx + dx, cy - dy, cz + dz);
                        if (chunkBox.contains(pos))
                            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                    }
                    // Crater floor (gravel/dirt)
                    BlockPos floor = new BlockPos(cx + dx, cy - depth - 1, cz + dz);
                    if (chunkBox.contains(floor))
                        world.setBlockState(floor, Blocks.GRAVEL.getDefaultState(), 3);
                }
            }
            // Shell crater block in the center
            BlockPos craterPos = new BlockPos(cx, cy - 1, cz);
            if (chunkBox.contains(craterPos))
                world.setBlockState(craterPos, ModBlocks.SHELL_CRATER.getDefaultState(), 3);
        }

        private void place(StructureWorldAccess world, BlockBox chunkBox,
                           net.minecraft.block.BlockState state, int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            if (chunkBox.contains(pos))
                world.setBlockState(pos, state, 3);
        }
    }
}
