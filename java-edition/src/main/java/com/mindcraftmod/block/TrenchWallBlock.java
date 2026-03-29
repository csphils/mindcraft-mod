package com.mindcraftmod.block;

import net.minecraft.block.WallBlock;

/**
 * Trench Wall — connecting wall block with timber+dirt aesthetic.
 *
 * Extends vanilla WallBlock which handles all the connection logic,
 * block states (north/south/east/west connections, tall/low variants),
 * and collision shapes automatically.
 *
 * All we need to define is the textures in the blockstate/model JSON files.
 * Blast resistance and material are set in ModBlocks via AbstractBlock.Settings.
 */
public class TrenchWallBlock extends WallBlock {

    public TrenchWallBlock(Settings settings) {
        super(settings);
    }

    // Inherits all WallBlock behavior:
    // - Connects to adjacent solid blocks
    // - Low (half) / Tall (full) connection variants per direction
    // - Correct bounding boxes for pathfinding
    // - Waterloggable
}
