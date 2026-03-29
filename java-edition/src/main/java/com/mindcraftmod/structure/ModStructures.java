package com.mindcraftmod.structure;

import com.mindcraftmod.MindcraftMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.StructureType;

/**
 * Registers custom structure types.
 *
 * Structure data (NBT pieces) live in:
 *   data/mindcraftmod/structures/
 *
 * Structure sets (placement frequency/spread) defined in:
 *   data/mindcraftmod/worldgen/structure_set/
 *
 * Structures themselves defined in:
 *   data/mindcraftmod/worldgen/structure/
 *
 * Biome tags that allow these structures:
 *   data/mindcraftmod/tags/worldgen/biome/has_structure/
 */
public class ModStructures {

    // Structure types are registered here; the actual structure generation
    // logic (NBT piece selection, placement rules) lives in the JSON data files
    // plus the Structure subclass for each type.

    public static final StructureType<TrenchNetworkStructure> TRENCH_NETWORK =
            Registry.register(Registries.STRUCTURE_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "trench_network"),
                    () -> TrenchNetworkStructure.CODEC);

    public static final StructureType<CommandBunkerStructure> COMMAND_BUNKER =
            Registry.register(Registries.STRUCTURE_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "command_bunker"),
                    () -> CommandBunkerStructure.CODEC);

    public static final StructureType<ArtilleryEmplacementStructure> ARTILLERY_EMPLACEMENT =
            Registry.register(Registries.STRUCTURE_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "artillery_emplacement"),
                    () -> ArtilleryEmplacementStructure.CODEC);

    public static final StructureType<ObservationTowerStructure> OBSERVATION_TOWER =
            Registry.register(Registries.STRUCTURE_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "observation_tower"),
                    () -> ObservationTowerStructure.CODEC);

    public static final StructureType<FieldHospitalStructure> FIELD_HOSPITAL =
            Registry.register(Registries.STRUCTURE_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "field_hospital"),
                    () -> FieldHospitalStructure.CODEC);

    public static final StructureType<RuinedVillageStructure> RUINED_VILLAGE =
            Registry.register(Registries.STRUCTURE_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "ruined_village"),
                    () -> RuinedVillageStructure.CODEC);

    public static void register() {
        // Field initializers trigger registration above.
        MindcraftMod.LOGGER.info("Structures registered.");
    }
}
