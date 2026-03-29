package com.mindcraftmod.structure;

import com.mindcraftmod.MindcraftMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.structure.StructurePieceType;

/**
 * Registers StructurePieceType instances for all mod structures.
 *
 * Each piece type maps a registry key to an NBT deserializer so that
 * saved chunks can reconstruct the piece during load.
 *
 * Must be called BEFORE any structure generates (i.e., before world gen runs).
 * Register in MindcraftMod.onInitialize() after ModStructures.register().
 */
public class ModStructurePieceTypes {

    public static final StructurePieceType TRENCH_NETWORK_PIECE =
            Registry.register(Registries.STRUCTURE_PIECE,
                    Identifier.of(MindcraftMod.MOD_ID, "trench_network_piece"),
                    TrenchNetworkStructure.Piece::new);

    public static final StructurePieceType COMMAND_BUNKER_PIECE =
            Registry.register(Registries.STRUCTURE_PIECE,
                    Identifier.of(MindcraftMod.MOD_ID, "command_bunker_piece"),
                    CommandBunkerStructure.Piece::new);

    public static final StructurePieceType ARTILLERY_EMPLACEMENT_PIECE =
            Registry.register(Registries.STRUCTURE_PIECE,
                    Identifier.of(MindcraftMod.MOD_ID, "artillery_emplacement_piece"),
                    ArtilleryEmplacementStructure.Piece::new);

    public static final StructurePieceType OBSERVATION_TOWER_PIECE =
            Registry.register(Registries.STRUCTURE_PIECE,
                    Identifier.of(MindcraftMod.MOD_ID, "observation_tower_piece"),
                    ObservationTowerStructure.Piece::new);

    public static final StructurePieceType FIELD_HOSPITAL_PIECE =
            Registry.register(Registries.STRUCTURE_PIECE,
                    Identifier.of(MindcraftMod.MOD_ID, "field_hospital_piece"),
                    FieldHospitalStructure.Piece::new);

    public static final StructurePieceType RUINED_VILLAGE_PIECE =
            Registry.register(Registries.STRUCTURE_PIECE,
                    Identifier.of(MindcraftMod.MOD_ID, "ruined_village_piece"),
                    RuinedVillageStructure.Piece::new);

    public static void register() {
        MindcraftMod.LOGGER.info("Structure piece types registered.");
    }
}
