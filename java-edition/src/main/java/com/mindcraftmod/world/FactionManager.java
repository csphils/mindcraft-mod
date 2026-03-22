package com.mindcraftmod.world;

import com.mindcraftmod.MindcraftMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side persistent storage for player faction membership.
 *
 * Stored as NBT in the world's data folder: data/mindcraftmod_factions.dat
 * Survives server restarts and world reloads.
 *
 * Access via: FactionManager.get(server)
 */
public class FactionManager extends PersistentState {

    public enum Faction {
        ALLIES, CENTRAL_POWERS, NONE;

        public String displayName() {
            return switch (this) {
                case ALLIES         -> "Allies";
                case CENTRAL_POWERS -> "Central Powers";
                case NONE           -> "None";
            };
        }
    }

    private static final String DATA_KEY = "mindcraftmod_factions";
    private final Map<UUID, Faction> playerFactions = new HashMap<>();

    // ── Public API ──────────────────────────────────────────────────────────

    public Faction getFaction(UUID playerId) {
        return playerFactions.getOrDefault(playerId, Faction.NONE);
    }

    public void setFaction(UUID playerId, Faction faction) {
        playerFactions.put(playerId, faction);
        markDirty(); // signals PersistentState to save on next autosave
    }

    public boolean areSameFaction(UUID a, UUID b) {
        Faction fa = getFaction(a);
        Faction fb = getFaction(b);
        return fa != Faction.NONE && fa == fb;
    }

    /** Returns count of players in each faction across all currently online players. */
    public Map<Faction, Long> getOnlineCounts(MinecraftServer server) {
        Map<Faction, Long> counts = new HashMap<>();
        for (var player : server.getPlayerManager().getPlayerList()) {
            Faction f = getFaction(player.getUuid());
            counts.merge(f, 1L, Long::sum);
        }
        return counts;
    }

    // ── PersistentState serialization ───────────────────────────────────────

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound factions = new NbtCompound();
        playerFactions.forEach((uuid, faction) ->
                factions.putString(uuid.toString(), faction.name()));
        nbt.put("playerFactions", factions);
        return nbt;
    }

    private static FactionManager fromNbt(NbtCompound nbt) {
        FactionManager manager = new FactionManager();
        NbtCompound factions = nbt.getCompound("playerFactions");
        for (String key : factions.getKeys()) {
            try {
                UUID uuid = UUID.fromString(key);
                Faction faction = Faction.valueOf(factions.getString(key));
                manager.playerFactions.put(uuid, faction);
            } catch (IllegalArgumentException e) {
                MindcraftMod.LOGGER.warn("Skipping corrupt faction entry: {}", key);
            }
        }
        return manager;
    }

    // ── Factory ─────────────────────────────────────────────────────────────

    public static FactionManager get(MinecraftServer server) {
        PersistentStateManager psm = server
                .getWorld(net.minecraft.world.World.OVERWORLD)
                .getPersistentStateManager();
        return psm.getOrCreate(
                new PersistentState.Type<>(FactionManager::new, FactionManager::fromNbt, null),
                DATA_KEY);
    }
}
