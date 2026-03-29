package com.mindcraftmod.world;

import com.mindcraftmod.block.ModBlocks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks territory-control progress for Flag Blocks.
 *
 * Rules:
 * - A player within 5 blocks of an uncaptured (or enemy-captured) Flag Block starts
 *   a 2-minute (2400-tick) uncontested capture timer.
 * - If a player from the opposing faction is also in range, the timer pauses.
 * - On capture: server broadcasts the event and schedules a daily Supply Crate bonus
 *   (the Supply Crate bonus is represented by spawning crates at the flag location
 *   via the WorldEventScheduler, configured at capture time).
 *
 * State is in-memory only (resets on server restart). Flag block positions are
 * discovered dynamically as players approach them.
 */
public class TerritoryManager {

    /** Capture radius in blocks (squared for efficiency). */
    private static final double CAPTURE_RADIUS_SQ = 5.0 * 5.0;
    /** Ticks required for uncontested capture: 2 minutes. */
    private static final int CAPTURE_TICKS = 2_400;
    /** Server ticks between territory scan cycles. */
    private static final int SCAN_INTERVAL = 20;

    /** Maps flag position → current controlling faction (or NONE). */
    private static final Map<BlockPos, FactionManager.Faction> flagOwners = new HashMap<>();
    /** Maps flag position → capture progress ticks (for the challenger faction). */
    private static final Map<BlockPos, Integer> captureProgress = new HashMap<>();
    /** Maps flag position → which faction is currently capturing. */
    private static final Map<BlockPos, FactionManager.Faction> capturingFaction = new HashMap<>();

    private static int scanCooldown = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TerritoryManager::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        scanCooldown--;
        if (scanCooldown > 0) return;
        scanCooldown = SCAN_INTERVAL;

        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) return;

        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        FactionManager factions = FactionManager.get(server);

        // Check each flag block currently known
        for (Map.Entry<BlockPos, FactionManager.Faction> entry : flagOwners.entrySet()) {
            BlockPos flagPos = entry.getKey();

            // Verify block still exists
            BlockState state = overworld.getBlockState(flagPos);
            if (!state.isOf(ModBlocks.FLAG_BLOCK)) {
                flagOwners.remove(flagPos);
                captureProgress.remove(flagPos);
                capturingFaction.remove(flagPos);
                break;
            }

            processFlag(server, overworld, flagPos, players, factions);
        }

        // Discover new flag blocks near players
        for (ServerPlayerEntity player : players) {
            BlockPos playerPos = player.getBlockPos();
            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    BlockPos check = playerPos.add(dx, 0, dz);
                    if (overworld.getBlockState(check).isOf(ModBlocks.FLAG_BLOCK)) {
                        flagOwners.putIfAbsent(check, FactionManager.Faction.NONE);
                    }
                }
            }
        }
    }

    private static void processFlag(MinecraftServer server, ServerWorld world,
                                     BlockPos flagPos, List<ServerPlayerEntity> players,
                                     FactionManager factions) {
        FactionManager.Faction owner = flagOwners.get(flagPos);

        // Count players of each faction in range
        long alliesNearby  = 0;
        long centralNearby = 0;
        for (ServerPlayerEntity p : players) {
            if (p.squaredDistanceTo(flagPos.getX(), flagPos.getY(), flagPos.getZ())
                    > CAPTURE_RADIUS_SQ) continue;
            FactionManager.Faction f = factions.getFaction(p.getUuid());
            if (f == FactionManager.Faction.ALLIES)         alliesNearby++;
            if (f == FactionManager.Faction.CENTRAL_POWERS) centralNearby++;
        }

        // No one contesting — reset progress if contested was happening
        if (alliesNearby == 0 && centralNearby == 0) {
            captureProgress.remove(flagPos);
            capturingFaction.remove(flagPos);
            return;
        }

        // Both factions present — contested, pause the timer
        if (alliesNearby > 0 && centralNearby > 0) {
            return;
        }

        // One faction uncontested
        FactionManager.Faction challenger =
                alliesNearby > 0 ? FactionManager.Faction.ALLIES : FactionManager.Faction.CENTRAL_POWERS;

        // If challenger already owns it, nothing to do
        if (challenger == owner) {
            captureProgress.remove(flagPos);
            capturingFaction.remove(flagPos);
            return;
        }

        // Different faction challenging — accumulate ticks (×20 per scan cycle)
        FactionManager.Faction current = capturingFaction.getOrDefault(flagPos, challenger);
        if (current != challenger) {
            // Faction switched — reset progress
            captureProgress.put(flagPos, 0);
            capturingFaction.put(flagPos, challenger);
        } else {
            int progress = captureProgress.getOrDefault(flagPos, 0) + SCAN_INTERVAL;
            captureProgress.put(flagPos, progress);
            capturingFaction.put(flagPos, challenger);

            if (progress >= CAPTURE_TICKS) {
                captureFlag(server, world, flagPos, challenger);
            }
        }
    }

    private static void captureFlag(MinecraftServer server, ServerWorld world,
                                     BlockPos flagPos, FactionManager.Faction capturer) {
        flagOwners.put(flagPos, capturer);
        captureProgress.remove(flagPos);
        capturingFaction.remove(flagPos);

        server.getPlayerManager().broadcast(
                Text.literal("🏴 The " + capturer.displayName()
                        + " have captured a trench at ["
                        + flagPos.getX() + ", " + flagPos.getZ() + "]!"), false);

        // Bonus supply crate at the flag location
        net.minecraft.entity.ItemEntity crate = new net.minecraft.entity.ItemEntity(
                world,
                flagPos.getX() + 0.5, flagPos.getY() + 1.5, flagPos.getZ() + 0.5,
                new net.minecraft.item.ItemStack(ModBlocks.SUPPLY_CRATE.asItem(), 2));
        world.spawnEntity(crate);
    }
}
