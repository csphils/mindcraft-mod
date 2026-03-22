# Multiplayer & Online Features Specification

---

## Overview

All multiplayer features are server-side authoritative. Clients receive state via custom packets (Java Edition) or Script API events (Bedrock).

Features work in:
- Dedicated servers (Java: Paper/Vanilla; Bedrock: BDS)
- LAN multiplayer
- Single-player (faction system degrades gracefully — player is assigned a faction, world events still fire)

---

## Faction System

### Design

Two factions:
- **Allies** (blue) — represented by Allied flag (blue Flag Block)
- **Central Powers** (red) — represented by Central flag (red Flag Block)

Players choose faction on first join or via `/faction join allies|central`.
Faction stored per-player in `FactionManager` using NBT data attached to the server-level data (persists across sessions).

### Java Edition Implementation

**`FactionManager.java`** — singleton accessed via server world's `PersistentState`:
```java
public class FactionManager extends PersistentState {
    private final Map<UUID, Faction> playerFactions = new HashMap<>();

    public Faction getFaction(UUID playerId) { ... }
    public void setFaction(UUID playerId, Faction faction) { ... }
    // Saves/loads via NbtCompound
}
```

**Commands:**
- `/faction join <allies|central>` — join a faction (cooldown: 24 hours)
- `/faction info` — show your faction and current territory counts
- `/faction list` — list online players by faction

**HUD:** Faction color shown as colored boss bar at top of screen while online.

### Effects of Faction Membership

| System | Allied Players | Central Players |
|---|---|---|
| Field Telephone | Can hear/send on Allied network | Can hear/send on Central network |
| Carrier Pigeon | Can deliver to Allied players | Can deliver to Central players |
| Flag Block capture | Capture red flags | Capture blue flags |
| Territory Control rewards | Get supply crates from Allied territory | Get supply crates from Central territory |
| Reinforcements event | Triggered when outnumbered | Triggered when outnumbered |

### Bedrock Implementation

Faction data stored in `scoreboard` objectives:
- `faction_allies` — 1 if player is Allied, 0 otherwise
- `faction_central` — 1 if player is Central, 0 otherwise

Script API manages faction changes and validates via server-side script.

---

## Territory Control

### Design

Each generated Trench Network contains 1x **Flag Block** (Allies or Central, set at generation time).
Players can capture a Flag Block by standing within 3 blocks of it for 2 continuous minutes without being attacked.

### Capture Mechanic

1. Player enters 3-block radius of Flag Block → capture timer starts (visible as action bar progress)
2. If player takes damage or leaves radius → timer resets
3. If enemy faction player also in radius → "contested" state — neither timer runs
4. At 2 minutes → Flag Block converts to player's faction color → server broadcasts capture

### Reward

Captured territory produces 1x Supply Crate every 20 real-world minutes (via scheduled server tick), placed adjacent to the Flag Block.

### Java Implementation

`FlagBlockEntity.java` — block entity that:
- Tracks capture timer per player UUID
- Broadcasts state updates to nearby clients via custom S2C packet
- Schedules supply crate spawns via `ServerTickEvents`

### Bedrock Implementation

Script API `world.afterEvents.playerInteractWithBlock` + scoreboard timer tracking.

---

## Shared World Events

Events fire on a server-wide timer and affect all players.

### Event Scheduler

**Java:** `WorldEventScheduler.java` registered via `ServerTickEvents.END_SERVER_TICK`.
Checks elapsed time and triggers events based on configurable intervals (stored in `mindcraftmod-server.toml`).

**Config file:** `config/mindcraftmod-server.toml`
```toml
[events]
artillery_barrage_interval_min = 20
artillery_barrage_interval_max = 40
gas_attack_enabled = true
supply_drop_enabled = true
reinforcements_enabled = true
```

---

### Event: Artillery Barrage

| Property | Value |
|---|---|
| **Trigger** | Every 20-40 minutes (random within range, configurable) |
| **Warning** | 10-second countdown broadcast to all players: `[WAR EVENT] Artillery barrage incoming at X, Z! Seek cover!` |
| **Area** | Random 30x30 zone in the overworld (biased toward No Man's Land / open plains) |
| **Effect** | Shell Craters placed in 6-10 random positions in the zone; players in zone take 3 damage per second for 10 seconds |
| **Visual** | Explosion particles + custom `artillery_shell` sound event |
| **Flag Block interaction** | Shell Craters cannot overwrite Flag Blocks (safe zones) |

---

### Event: Gas Attack

| Property | Value |
|---|---|
| **Trigger** | Signal Flare (Red) thrown by any player |
| **Area** | 15x15 zone centered on Signal Flare landing position |
| **Duration** | 60 seconds (Gas Cloud blocks dissipate after) |
| **Broadcast** | `[WAR EVENT] Gas attack at X, Z! Equip your gas mask!` |
| **Warning** | 3-second delay after flare lands before gas spreads |

---

### Event: Supply Drop

| Property | Value |
|---|---|
| **Trigger** | Signal Flare (Green) thrown by any player |
| **Effect** | Supply Crate entity falls from sky (Y=255) at flare landing position, lands with landing animation |
| **Broadcast** | `[WAR EVENT] Supply drop incoming at X, Z!` |
| **Loot** | Supply Crate loot table (uncommon tier bias) |
| **Cooldown** | 5-minute cooldown per player to prevent spam |

---

### Event: Reinforcements

| Property | Value |
|---|---|
| **Trigger** | Automatic — checks every 5 minutes; fires if one faction has 3x more online players than the other |
| **Effect** | 3-5 Trench Soldiers spawn near the outnumbered faction's nearest captured Trench Network |
| **Broadcast** | `[WAR EVENT] Reinforcements arriving for <faction>!` |
| **Purpose** | Balance mechanic for uneven servers |

---

### Event: Signal Flare (Smoke)

| Property | Value |
|---|---|
| **Trigger** | Signal Flare (Gray) thrown by any player |
| **Effect** | 3x3x3 Smoke Screen block area at landing position |
| **Duration** | 30 seconds |
| **No broadcast** | Tactical use only — no server-wide message |

---

## Multiplayer Sync Architecture (Java Edition)

### Custom Packet Channel
`mindcraftmod:sync`

### Packets

| Packet | Direction | Purpose |
|---|---|---|
| `FactionSyncPacket` | S→C | Sends player's faction on join/change |
| `TerritoryStatePacket` | S→C | Sends capture timer state to nearby players |
| `WorldEventPacket` | S→C | Broadcasts event type + location to all clients |
| `JoinFactionPacket` | C→S | Player requests faction change |
| `PigeonDeliveryPacket` | S→C | Notifies target player of pigeon delivery |

### Packet Registration
Via `ServerPlayNetworking.registerGlobalReceiver()` (C→S) and `ServerPlayNetworking.send()` (S→C) using Fabric Networking API.

---

## Bedrock Multiplayer Architecture

### Script API Events

| Event | Type | Purpose |
|---|---|---|
| `FactionJoin` | Script custom event | Player joins faction |
| `TerritoryCapture` | Script custom event | Flag captured |
| `WorldEventFire` | Script custom event | Trigger barrage/gas/drop |
| `PigeonDeliver` | Script custom event | Item/message delivery |

All events use `world.sendMessage()` for broadcasts.
Faction data stored in scoreboard objectives (persists across sessions on Bedrock dedicated server).

---

## Single-Player Degradation

| Feature | Single-Player Behavior |
|---|---|
| Faction system | Player auto-assigned to Allies; Central structures still generate and have hostile NPCs |
| Field Telephone | No function (no other players to reach) |
| Carrier Pigeon | Pigeon delivery to self is a no-op; still decorative |
| Artillery Barrage | Still fires — affects player |
| Gas Attack | Still fires on Signal Flare |
| Supply Drop | Still drops crate |
| Reinforcements | Does not fire (no faction balance check needed) |
| Territory Control | Player can capture flags — no other faction to contest |
