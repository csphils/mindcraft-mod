package com.mindcraftmod.world;

import com.mindcraftmod.block.ModBlocks;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

/**
 * Schedules and fires shared world events on a probabilistic server-tick basis.
 *
 * Events:
 *   - Artillery Barrage: fires every 24000–48000 ticks (20–40 min), deals 3 damage
 *     to players in a 30×30 zone and places Shell Crater blocks
 *   - Gas Attack: covers a 15×15 area with Gas Cloud blocks for ~60 seconds
 *   - Supply Drop: drops a Supply Crate item at a random online player's location
 *   - Reinforcements: spawns Trench Soldiers near the outnumbered faction's players
 */
public class WorldEventScheduler {

    /** Minimum ticks between Artillery Barrage events (20 min). */
    private static final int BARRAGE_MIN_TICKS  = 24_000;
    /** Maximum ticks between Artillery Barrage events (40 min). */
    private static final int BARRAGE_MAX_TICKS  = 48_000;

    private static int nextBarrageTick    = BARRAGE_MIN_TICKS;
    private static int barrageCooldown    = 0;

    private static int nextGasAttackTick  = 36_000; // 30 min initial
    private static int gasAttackCooldown  = 0;

    private static int nextSupplyDropTick = 12_000; // 10 min initial
    private static int supplyDropCooldown = 0;

    private static int reinforceCooldown  = 6_000;  // check every 5 min

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(WorldEventScheduler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) return;

        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        if (players.isEmpty()) return;

        barrageCooldown++;
        gasAttackCooldown++;
        supplyDropCooldown++;
        reinforceCooldown--;

        // ── Artillery Barrage ────────────────────────────────────────────────
        if (barrageCooldown >= nextBarrageTick) {
            triggerArtilleryBarrage(server, overworld, players);
            barrageCooldown = 0;
            nextBarrageTick = BARRAGE_MIN_TICKS
                    + overworld.getRandom().nextInt(BARRAGE_MAX_TICKS - BARRAGE_MIN_TICKS);
        }

        // ── Gas Attack ───────────────────────────────────────────────────────
        if (gasAttackCooldown >= nextGasAttackTick) {
            triggerGasAttack(overworld, players);
            gasAttackCooldown = 0;
            nextGasAttackTick = 24_000 + overworld.getRandom().nextInt(24_000);
        }

        // ── Supply Drop ──────────────────────────────────────────────────────
        if (supplyDropCooldown >= nextSupplyDropTick) {
            triggerSupplyDrop(overworld, players);
            supplyDropCooldown = 0;
            nextSupplyDropTick = 8_000 + overworld.getRandom().nextInt(8_000);
        }

        // ── Reinforcements ───────────────────────────────────────────────────
        if (reinforceCooldown <= 0) {
            checkReinforcements(server, overworld, players);
            reinforceCooldown = 6_000;
        }
    }

    // ── Artillery Barrage ────────────────────────────────────────────────────

    private static void triggerArtilleryBarrage(MinecraftServer server,
                                                 ServerWorld world,
                                                 List<ServerPlayerEntity> players) {
        // Warning broadcast
        server.getPlayerManager().broadcast(
                Text.literal("⚠ INCOMING ARTILLERY! Take cover — barrage in 10 seconds!"), false);

        // Pick a random player as the barrage centre
        ServerPlayerEntity centre = players.get(world.getRandom().nextInt(players.size()));
        int cx = centre.getBlockX();
        int cz = centre.getBlockZ();

        // Schedule the barrage 10 seconds later via a delayed task
        // (Minecraft doesn't have a built-in delay; we use a simple countdown approach
        //  implemented as a one-shot follow-up in the NEXT invocation of this scheduler
        //  by flagging pending damage via a static counter.)
        pendingBarrageX    = cx;
        pendingBarrageZ    = cz;
        pendingBarrageWorld = world;
        pendingBarragePlayers = players;
        pendingBarrageDelay = 200; // 10 seconds
    }

    private static int    pendingBarrageDelay   = -1;
    private static int    pendingBarrageX;
    private static int    pendingBarrageZ;
    private static ServerWorld          pendingBarrageWorld;
    private static List<ServerPlayerEntity> pendingBarragePlayers;

    static {
        // Register a second tick listener to fire the delayed barrage damage
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (pendingBarrageDelay > 0) {
                pendingBarrageDelay--;
            } else if (pendingBarrageDelay == 0) {
                pendingBarrageDelay = -1;
                fireBarrage(server, pendingBarrageWorld, pendingBarragePlayers,
                        pendingBarrageX, pendingBarrageZ);
            }
        });
    }

    private static void fireBarrage(MinecraftServer server, ServerWorld world,
                                    List<ServerPlayerEntity> players, int cx, int cz) {
        // Place 8–12 shell craters in a 30×30 zone
        int craterCount = 8 + world.getRandom().nextInt(5);
        for (int i = 0; i < craterCount; i++) {
            int bx = cx + world.getRandom().nextInt(30) - 15;
            int bz = cz + world.getRandom().nextInt(30) - 15;
            int by = world.getTopY(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, bx, bz) - 1;
            world.setBlockState(new BlockPos(bx, by, bz),
                    ModBlocks.SHELL_CRATER.getDefaultState());
        }

        // Damage players within the 30×30 zone
        for (ServerPlayerEntity player : players) {
            if (Math.abs(player.getBlockX() - cx) <= 15
                    && Math.abs(player.getBlockZ() - cz) <= 15) {
                player.damage(world, world.getDamageSources().generic(), 3.0f);
                player.sendMessage(Text.literal("You were caught in the artillery barrage!"), true);
            }
        }

        server.getPlayerManager().broadcast(
                Text.literal("The artillery barrage has ended."), false);
    }

    // ── Gas Attack ───────────────────────────────────────────────────────────

    private static void triggerGasAttack(ServerWorld world, List<ServerPlayerEntity> players) {
        world.getServer().getPlayerManager().broadcast(
                Text.literal("⚠ GAS ATTACK! Don your gas masks!"), false);

        // Place gas cloud blocks in a 15×15 area around a random player
        ServerPlayerEntity target = players.get(world.getRandom().nextInt(players.size()));
        int tx = target.getBlockX();
        int tz = target.getBlockZ();
        int ty = target.getBlockY();

        for (int dx = -7; dx <= 7; dx += 2) {
            for (int dz = -7; dz <= 7; dz += 2) {
                BlockPos pos = new BlockPos(tx + dx, ty, tz + dz);
                if (world.getBlockState(pos).isAir()) {
                    world.setBlockState(pos, ModBlocks.GAS_CLOUD.getDefaultState());
                }
            }
        }
    }

    // ── Supply Drop ──────────────────────────────────────────────────────────

    private static void triggerSupplyDrop(ServerWorld world, List<ServerPlayerEntity> players) {
        ServerPlayerEntity recipient = players.get(world.getRandom().nextInt(players.size()));
        world.getServer().getPlayerManager().broadcast(
                Text.literal("📦 Supply drop incoming at "
                        + recipient.getName().getString() + "'s position!"), false);

        // Drop a supply crate item from 20 blocks above the player
        BlockPos dropPos = recipient.getBlockPos().up(20);
        net.minecraft.entity.ItemEntity crate = new net.minecraft.entity.ItemEntity(
                world,
                dropPos.getX(), dropPos.getY(), dropPos.getZ(),
                new net.minecraft.item.ItemStack(ModBlocks.SUPPLY_CRATE.asItem()));
        crate.setVelocity(0, -0.5, 0);
        world.spawnEntity(crate);
    }

    // ── Reinforcements ───────────────────────────────────────────────────────

    private static void checkReinforcements(MinecraftServer server, ServerWorld world,
                                             List<ServerPlayerEntity> players) {
        FactionManager factions = FactionManager.get(server);
        Map<FactionManager.Faction, Long> counts = factions.getOnlineCounts(server);

        long allies  = counts.getOrDefault(FactionManager.Faction.ALLIES, 0L);
        long central = counts.getOrDefault(FactionManager.Faction.CENTRAL_POWERS, 0L);

        // Only fire when one side has 3× the players of the other
        if (allies == 0 || central == 0) return;

        FactionManager.Faction outnumbered = null;
        if (allies * 3 <= central) outnumbered = FactionManager.Faction.ALLIES;
        if (central * 3 <= allies) outnumbered = FactionManager.Faction.CENTRAL_POWERS;
        if (outnumbered == null) return;

        final FactionManager.Faction finalOutnumbered = outnumbered;
        List<ServerPlayerEntity> outnumberedPlayers = players.stream()
                .filter(p -> factions.getFaction(p.getUuid()) == finalOutnumbered)
                .toList();

        if (outnumberedPlayers.isEmpty()) return;

        server.getPlayerManager().broadcast(
                Text.literal("Reinforcements arriving for the "
                        + outnumbered.displayName() + "!"), false);

        // Spawn 3–5 Trench Soldiers near each outnumbered player
        for (ServerPlayerEntity player : outnumberedPlayers) {
            int count = 3 + world.getRandom().nextInt(3);
            for (int i = 0; i < count; i++) {
                com.mindcraftmod.entity.TrenchSoldierEntity soldier =
                        new com.mindcraftmod.entity.TrenchSoldierEntity(
                                com.mindcraftmod.entity.ModEntities.TRENCH_SOLDIER, world);
                double spawnX = player.getX() + world.getRandom().nextDouble() * 10 - 5;
                double spawnZ = player.getZ() + world.getRandom().nextDouble() * 10 - 5;
                double spawnY = world.getTopY(
                        net.minecraft.world.Heightmap.Type.WORLD_SURFACE,
                        (int) spawnX, (int) spawnZ);
                soldier.refreshPositionAndAngles(spawnX, spawnY, spawnZ, 0, 0);
                world.spawnEntityAndPassengers(soldier);
            }
        }
    }
}
