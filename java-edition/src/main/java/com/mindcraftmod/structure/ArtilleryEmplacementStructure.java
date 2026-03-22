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
 * Artillery Emplacement — an elevated stone platform with sandbag walls.
 *
 * Layout: 9×9 stone slab platform raised 3 blocks, sandbag walls around the perimeter,
 * Artillery Platform block in the center, guarded by 2–4 Trench Soldiers (spawner blocks).
 */
public class ArtilleryEmplacementStructure extends Structure {

    public static final MapCodec<ArtilleryEmplacementStructure> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(configCodecBuilder(instance))
                            .apply(instance, ArtilleryEmplacementStructure::new));

    public ArtilleryEmplacementStructure(Config config) {
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
        collector.addPiece(new Piece(
                origin,
                new BlockBox(origin.getX(), origin.getY(), origin.getZ(),
                        origin.getX() + 8, origin.getY() + 5, origin.getZ() + 8)));
    }

    @Override
    public StructureType<?> getType() {
        return ModStructures.ARTILLERY_EMPLACEMENT;
    }

    // ────────────────────────────────────────────────────────────────────────

    public static class Piece extends StructurePiece {

        private final BlockPos origin;

        public Piece(BlockPos origin, BlockBox boundingBox) {
            super(ModStructurePieceTypes.ARTILLERY_EMPLACEMENT_PIECE, 0, boundingBox);
            this.origin = origin;
        }

        public Piece(StructureContext ctx, NbtCompound nbt) {
            super(ModStructurePieceTypes.ARTILLERY_EMPLACEMENT_PIECE, nbt);
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

            // Raised stone platform (9×9, 3 blocks tall support columns + slab surface)
            for (int dx = 0; dx <= 8; dx++) {
                for (int dz = 0; dz <= 8; dz++) {
                    // Support pillars at corners and edges
                    for (int dy = 0; dy <= 2; dy++) {
                        BlockPos pos = new BlockPos(ox + dx, oy + dy, oz + dz);
                        if (chunkBox.contains(pos))
                            world.setBlockState(pos, Blocks.STONE_BRICKS.getDefaultState(), 3);
                    }
                    // Platform surface
                    BlockPos surface = new BlockPos(ox + dx, oy + 3, oz + dz);
                    if (chunkBox.contains(surface))
                        world.setBlockState(surface, Blocks.STONE_BRICK_SLAB.getDefaultState(), 3);

                    // Sandbag walls around perimeter (2 high, on the surface)
                    if (dx == 0 || dx == 8 || dz == 0 || dz == 8) {
                        for (int wallY = 4; wallY <= 5; wallY++) {
                            BlockPos wall = new BlockPos(ox + dx, oy + wallY, oz + dz);
                            if (chunkBox.contains(wall))
                                world.setBlockState(wall, ModBlocks.SANDBAG.getDefaultState(), 3);
                        }
                    }
                }
            }

            // Artillery Platform block in center
            BlockPos artilleryPos = new BlockPos(ox + 4, oy + 4, oz + 4);
            if (chunkBox.contains(artilleryPos))
                world.setBlockState(artilleryPos, ModBlocks.ARTILLERY_PLATFORM.getDefaultState(), 3);

            // Mob spawner for Trench Soldiers at the two back corners
            placeSpawner(world, chunkBox, new BlockPos(ox + 1, oy + 4, oz + 1));
            placeSpawner(world, chunkBox, new BlockPos(ox + 7, oy + 4, oz + 7));
        }

        private void placeSpawner(StructureWorldAccess world, BlockBox chunkBox, BlockPos pos) {
            if (!chunkBox.contains(pos)) return;
            world.setBlockState(pos, Blocks.SPAWNER.getDefaultState(), 3);
            // In a full implementation, the spawner tile entity would be configured
            // here via BlockEntity NBT. Placeholder for now.
        }
    }
}
