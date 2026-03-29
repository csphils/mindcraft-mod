package com.mindcraftmod.world;

import net.minecraft.nbt.NbtCompound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.mindcraftmod.world.FactionManager.Faction.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FactionManager — pure Java logic, no Minecraft runtime needed.
 *
 * Tests NBT serialization, faction assignment, and the areSameFaction helper.
 * Run with: ./gradlew test
 */
class FactionManagerTest {

    private FactionManager manager;
    private final UUID alice = UUID.randomUUID();
    private final UUID bob   = UUID.randomUUID();
    private final UUID carol = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        manager = new FactionManager();
    }

    // ── getFaction ───────────────────────────────────────────────────────────

    @Test
    void getFaction_unknownPlayer_returnsNone() {
        assertEquals(NONE, manager.getFaction(UUID.randomUUID()));
    }

    // ── setFaction / getFaction round-trip ───────────────────────────────────

    @Test
    void setFaction_allies_persistsCorrectly() {
        manager.setFaction(alice, ALLIES);
        assertEquals(ALLIES, manager.getFaction(alice));
    }

    @Test
    void setFaction_central_persistsCorrectly() {
        manager.setFaction(bob, CENTRAL_POWERS);
        assertEquals(CENTRAL_POWERS, manager.getFaction(bob));
    }

    @Test
    void setFaction_canChangeFromAlliesToCentral() {
        manager.setFaction(alice, ALLIES);
        manager.setFaction(alice, CENTRAL_POWERS);
        assertEquals(CENTRAL_POWERS, manager.getFaction(alice));
    }

    @Test
    void setFaction_canResetToNone() {
        manager.setFaction(alice, ALLIES);
        manager.setFaction(alice, NONE);
        assertEquals(NONE, manager.getFaction(alice));
    }

    // ── areSameFaction ───────────────────────────────────────────────────────

    @Test
    void areSameFaction_bothAllies_returnsTrue() {
        manager.setFaction(alice, ALLIES);
        manager.setFaction(bob, ALLIES);
        assertTrue(manager.areSameFaction(alice, bob));
    }

    @Test
    void areSameFaction_bothCentral_returnsTrue() {
        manager.setFaction(alice, CENTRAL_POWERS);
        manager.setFaction(bob, CENTRAL_POWERS);
        assertTrue(manager.areSameFaction(alice, bob));
    }

    @Test
    void areSameFaction_differentFactions_returnsFalse() {
        manager.setFaction(alice, ALLIES);
        manager.setFaction(bob, CENTRAL_POWERS);
        assertFalse(manager.areSameFaction(alice, bob));
    }

    @Test
    void areSameFaction_oneIsNone_returnsFalse() {
        manager.setFaction(alice, ALLIES);
        // bob stays NONE
        assertFalse(manager.areSameFaction(alice, bob));
    }

    @Test
    void areSameFaction_bothNone_returnsFalse() {
        // Neither alice nor bob has a faction
        assertFalse(manager.areSameFaction(alice, bob));
    }

    @Test
    void areSameFaction_isSymmetric() {
        manager.setFaction(alice, ALLIES);
        manager.setFaction(bob, ALLIES);
        assertEquals(
                manager.areSameFaction(alice, bob),
                manager.areSameFaction(bob, alice)
        );
    }

    // ── NBT round-trip ───────────────────────────────────────────────────────

    @Test
    void nbtRoundTrip_preservesAllFactions() {
        manager.setFaction(alice, ALLIES);
        manager.setFaction(bob, CENTRAL_POWERS);
        manager.setFaction(carol, NONE);

        // Serialize
        NbtCompound nbt = FactionManager.writeNbtForTest(manager);

        // Deserialize into a new instance by calling fromNbt via reflection
        // (fromNbt is package-private; we test via the public Type factory indirectly)
        FactionManager restored = FactionManager.fromNbtForTest(nbt);

        assertEquals(ALLIES,          restored.getFaction(alice));
        assertEquals(CENTRAL_POWERS,  restored.getFaction(bob));
        assertEquals(NONE,            restored.getFaction(carol));
    }

    @Test
    void nbtRoundTrip_emptyManager_roundTripsCleanly() {
        NbtCompound nbt = FactionManager.writeNbtForTest(manager);
        FactionManager restored = FactionManager.fromNbtForTest(nbt);
        assertEquals(NONE, restored.getFaction(UUID.randomUUID()));
    }

    @Test
    void nbtRoundTrip_manyPlayers_allPreserved() {
        UUID[] players = new UUID[50];
        for (int i = 0; i < players.length; i++) {
            players[i] = UUID.randomUUID();
            manager.setFaction(players[i], i % 2 == 0 ? ALLIES : CENTRAL_POWERS);
        }

        NbtCompound nbt = FactionManager.writeNbtForTest(manager);
        FactionManager restored = FactionManager.fromNbtForTest(nbt);

        for (int i = 0; i < players.length; i++) {
            FactionManager.Faction expected = i % 2 == 0 ? ALLIES : CENTRAL_POWERS;
            assertEquals(expected, restored.getFaction(players[i]),
                    "Player at index " + i + " had wrong faction after round-trip");
        }
    }

    // ── Corrupt NBT handling ─────────────────────────────────────────────────

    @Test
    void fromNbt_corruptKey_skipsGracefully() {
        NbtCompound nbt = new NbtCompound();
        NbtCompound factions = new NbtCompound();
        factions.putString("not-a-uuid", "ALLIES");          // corrupt key
        factions.putString("also-bad", "INVALID_FACTION");   // invalid faction value
        factions.putString(alice.toString(), "ALLIES");      // valid entry
        nbt.put("playerFactions", factions);

        // Should not throw
        FactionManager restored = assertDoesNotThrow(() -> FactionManager.fromNbtForTest(nbt));
        // Valid entry still present
        assertEquals(ALLIES, restored.getFaction(alice));
    }

    // ── Faction.displayName ──────────────────────────────────────────────────

    @Test
    void displayName_allVariants_nonEmpty() {
        for (FactionManager.Faction f : FactionManager.Faction.values()) {
            assertNotNull(f.displayName());
            assertFalse(f.displayName().isBlank(), f + ".displayName() must not be blank");
        }
    }
}
