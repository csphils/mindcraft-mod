package com.mindcraftmod;

import com.mindcraftmod.block.ModBlocks;
import com.mindcraftmod.block.entity.ModBlockEntities;
import com.mindcraftmod.entity.ModEntities;
import com.mindcraftmod.item.ModItems;
import com.mindcraftmod.item.ModItemGroups;
import com.mindcraftmod.structure.ModStructures;
import com.mindcraftmod.world.ModWorldGen;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod entrypoint.
 *
 * Registration order matters — blocks must be registered before items (block items),
 * and both before creative tabs that reference them.
 */
public class MindcraftMod implements ModInitializer {

    public static final String MOD_ID = "mindcraftmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Mindcraft Mod — WW1 Warfare");

        // Registration order: blocks → block entities → entities → items → groups → structures → world gen
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModItems.register();
        ModItemGroups.register();
        ModStructures.register();
        ModWorldGen.register();

        LOGGER.info("Mindcraft Mod initialized.");
    }
}
