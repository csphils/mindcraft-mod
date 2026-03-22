package com.mindcraftmod.structure;

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
 * Field Hospital — above-ground medical tent structure in Plains biomes.
 *
 * Layout: 9×5×7 white wool tent with oak plank floor, red cross banner on the
 * front, healing loot chests inside (golden apples, potions).
 */
public class FieldHospitalStructure extends Structure {

    public static final MapCodec<FieldHospitalStructure> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(configCodecBuilder(instance))
                            .apply(instance, FieldHospitalStructure::new));

    public FieldHospitalStructure(Config config) {
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
                        origin.getX() + 8, origin.getY() + 5, origin.getZ() + 6)));
    }

    @Override
    public StructureType<?> getType() {
        return ModStructures.FIELD_HOSPITAL;
    }

    // ────────────────────────────────────────────────────────────────────────

    public static class Piece extends StructurePiece {

        private final BlockPos origin;

        public Piece(BlockPos origin, BlockBox boundingBox) {
            super(ModStructurePieceTypes.FIELD_HOSPITAL_PIECE, 0, boundingBox);
            this.origin = origin;
        }

        public Piece(StructureContext ctx, NbtCompound nbt) {
            super(ModStructurePieceTypes.FIELD_HOSPITAL_PIECE, nbt);
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

            // Oak plank floor (9×7)
            for (int dx = 0; dx <= 8; dx++) {
                for (int dz = 0; dz <= 6; dz++) {
                    place(world, chunkBox, Blocks.OAK_PLANKS.getDefaultState(), ox + dx, oy, oz + dz);
                }
            }

            // White wool walls (height 3) and roof (angled using slabs)
            for (int dx = 0; dx <= 8; dx++) {
                for (int dz = 0; dz <= 6; dz++) {
                    boolean isPerimeter = dx == 0 || dx == 8 || dz == 0 || dz == 6;
                    if (isPerimeter) {
                        for (int dy = 1; dy <= 3; dy++) {
                            place(world, chunkBox, Blocks.WHITE_WOOL.getDefaultState(),
                                    ox + dx, oy + dy, oz + dz);
                        }
                    }
                }
            }

            // Roof: white wool dome over the top
            for (int dx = 1; dx <= 7; dx++) {
                for (int dz = 1; dz <= 5; dz++) {
                    place(world, chunkBox, Blocks.WHITE_WOOL.getDefaultState(),
                            ox + dx, oy + 4, oz + dz);
                }
            }
            // Ridge of the roof
            for (int dx = 1; dx <= 7; dx++) {
                place(world, chunkBox, Blocks.WHITE_WOOL.getDefaultState(),
                        ox + dx, oy + 5, oz + 3);
            }

            // Entrance: open the front wall (dx=0, dz=2..4, dy=1..2)
            for (int dy = 1; dy <= 2; dy++) {
                place(world, chunkBox, Blocks.AIR.getDefaultState(), ox, oy + dy, oz + 2);
                place(world, chunkBox, Blocks.AIR.getDefaultState(), ox, oy + dy, oz + 3);
                place(world, chunkBox, Blocks.AIR.getDefaultState(), ox, oy + dy, oz + 4);
            }

            // Interior: 3 loot chests with healing supplies
            place(world, chunkBox, Blocks.CHEST.getDefaultState(), ox + 2, oy + 1, oz + 1);
            place(world, chunkBox, Blocks.CHEST.getDefaultState(), ox + 5, oy + 1, oz + 1);
            place(world, chunkBox, Blocks.CHEST.getDefaultState(), ox + 2, oy + 1, oz + 5);

            // Crafting table and furnace for flavour
            place(world, chunkBox, Blocks.CRAFTING_TABLE.getDefaultState(), ox + 5, oy + 1, oz + 5);
            place(world, chunkBox, Blocks.FURNACE.getDefaultState(), ox + 7, oy + 1, oz + 3);
        }

        private void place(StructureWorldAccess world, BlockBox chunkBox,
                           net.minecraft.block.BlockState state, int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            if (chunkBox.contains(pos))
                world.setBlockState(pos, state, 3);
        }
    }
}
