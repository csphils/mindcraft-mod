package com.mindcraftmod;

import com.mindcraftmod.block.ModBlocks;
import com.mindcraftmod.block.entity.ModBlockEntities;
import com.mindcraftmod.entity.ModEntities;
import com.mindcraftmod.entity.ModEntityAttributes;
import com.mindcraftmod.item.ModItems;
import com.mindcraftmod.item.ModItemGroups;
import com.mindcraftmod.structure.ModStructurePieceTypes;
import com.mindcraftmod.structure.ModStructures;
import com.mindcraftmod.network.FactionSyncPayload;
import com.mindcraftmod.world.FactionCommand;
import com.mindcraftmod.world.FactionManager;
import com.mindcraftmod.world.ModWorldGen;
import com.mindcraftmod.world.TerritoryManager;
import com.mindcraftmod.world.WorldEventScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

        // Registration order: blocks → block entities → entities → entity attrs → items → groups → structures → world gen
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModEntityAttributes.register(); // must come after ModEntities
        ModItems.register();
        ModItemGroups.register();
        ModStructures.register();
        ModStructurePieceTypes.register(); // must come after ModStructures
        ModWorldGen.register();

        // Phase 6 — Multiplayer systems (event-driven, registered last)
        FactionCommand.register();
        WorldEventScheduler.register();
        TerritoryManager.register();

        // Networking — S→C faction sync packet
        PayloadTypeRegistry.playS2C().register(FactionSyncPayload.ID, FactionSyncPayload.CODEC);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            String factionName = FactionManager.get(server)
                    .getFaction(handler.player.getUuid()).name();
            ServerPlayNetworking.send(handler.player, new FactionSyncPayload(factionName));
        });

        LOGGER.info("Mindcraft Mod initialized.");
    }
}
