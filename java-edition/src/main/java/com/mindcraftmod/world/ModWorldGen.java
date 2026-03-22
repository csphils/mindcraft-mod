package com.mindcraftmod.world;

import com.mindcraftmod.MindcraftMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.SpawnSettings;

import static com.mindcraftmod.entity.ModEntities.*;

/**
 * Configures mob spawn conditions and biome modifications.
 *
 * Structure placement is handled entirely via data-driven JSON files in
 * data/mindcraftmod/worldgen/ — no code registration needed for structures
 * beyond registering the StructureType codecs in ModStructures.
 */
public class ModWorldGen {

    public static void register() {
        registerSpawns();
        MindcraftMod.LOGGER.info("World gen configured.");
    }

    private static void registerSpawns() {
        // War Horse — Plains and Savanna, replaces some vanilla horse spawns
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                        net.minecraft.world.biome.BiomeKeys.PLAINS,
                        net.minecraft.world.biome.BiomeKeys.SAVANNA),
                SpawnGroup.CREATURE,
                WAR_HORSE,
                /* weight= */ 4, /* minGroup= */ 1, /* maxGroup= */ 3);

        // Carrier Pigeon — spawns near structures; ambient biome spawn as fallback
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(),
                SpawnGroup.CREATURE,
                CARRIER_PIGEON,
                2, 2, 4);

        // Trench Rat — dark areas in overworld
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(),
                SpawnGroup.CREATURE,
                TRENCH_RAT,
                3, 2, 6);

        // Guard Dog — Plains and Savanna (structure-bound spawners preferred)
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(
                        net.minecraft.world.biome.BiomeKeys.PLAINS,
                        net.minecraft.world.biome.BiomeKeys.SAVANNA),
                SpawnGroup.CREATURE,
                GUARD_DOG,
                2, 1, 2);

        // Hostile mobs spawn via structure-bound spawner blocks only.
        // (TrenchSoldier, Sniper, GasGrenadier are placed by structure NBTs.)
    }
}
