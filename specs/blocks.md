# Blocks Specification

---

## Pass-Through Blocks

These blocks have no collision shape — players and mobs walk through them — but apply effects on contact.

**Java Edition implementation:** Override `getCollisionShape()` to return `VoxelShapes.empty()`. Use `randomDisplayTick` or block entity ticking to apply status effects to players inside the block's bounding box.

**Bedrock implementation:** Set `"minecraft:collision_box": { "enabled": false }` in block JSON. Use Script API `world.afterEvents.entityEnterBlock` to apply effects.

---

### Barbed Wire

| Property | Value |
|---|---|
| **Block ID** | `mindcraftmod:barbed_wire` |
| **Placed like** | Carpet (flat, 1/16 height) |
| **Collision** | None |
| **Effect** | Slowness II + 1 damage per second while standing in it |
| **Breaks with** | Shears (silk touch), or any tool (drops nothing) |
| **Crafting** | 6x Iron Ingot → 3x Barbed Wire (shaped: alternating) |
| **Flammable** | No |
| **Blast resistance** | 0 (destroyed by explosion) |
| **Notes** | Does not prevent mob pathfinding. Hostile mobs ignore damage from it. |

---

### Gas Cloud

| Property | Value |
|---|---|
| **Block ID** | `mindcraftmod:gas_cloud` |
| **Placed like** | Scaffolding (full-block space, visual is semi-transparent yellow-green) |
| **Collision** | None |
| **Effect** | Poison II for 3 seconds (refreshed each tick in block) |
| **Gas Mask** | Wearing Gas Mask item negates all effects |
| **Spread** | Spreads to adjacent air blocks every 10 random ticks (up to 15 blocks from source) |
| **Dissipation** | 5% chance per random tick to dissipate; fully clears in 60-120 seconds |
| **Placed by** | Gas Grenadier mob on impact, or player using Gas Canister item |
| **Breaks with** | Any tool instantly, or natural dissipation |
| **Notes** | Cannot spread upward. Spread is blocked by solid blocks. |

---

### Smoke Screen

| Property | Value |
|---|---|
| **Block ID** | `mindcraftmod:smoke_screen` |
| **Placed like** | Gas Cloud |
| **Collision** | None |
| **Effect** | Blindness I while inside; Blindness I for 3 seconds after exiting |
| **Spread** | Does not spread; placed in a 3x3x3 area by Signal Flare (gray) |
| **Dissipation** | Dissipates over 30 seconds (20% per random tick) |
| **Notes** | Provides cover from Sniper mob detection. |

---

### Mud Pit

| Property | Value |
|---|---|
| **Block ID** | `mindcraftmod:mud_pit` |
| **Placed like** | Full solid block (replaces standard ground blocks near Trench Structures) |
| **Collision** | Yes (solid) |
| **Effect** | Slowness II, reduces jump height by 1 block (like Soul Sand mechanic) |
| **Breaks with** | Shovel |
| **Drop** | Mud Ball x2 (crafting ingredient) |
| **Notes** | Trench Soldiers pathfind around mud by default unless no alternative. |

---

## Functional Blocks

### Sandbag

| Property | Value |
|---|---|
| **Block ID** | `mindcraftmod:sandbag` |
| **Placed like** | Snow layer (stackable 1-8 layers) |
| **Collision** | Yes, matches height of layer count |
| **Blast resistance** | 6 (absorbs explosion, reduced radius by 30%) |
| **Crafting** | 4x Sand + 2x String → 2x Sandbag |
| **Notes** | Can form effective walls when stacked 3-4 layers high. Used heavily in Trench structures. |

---

### Trench Wall

| Property | Value |
|---|---|
| **Block ID** | `mindcraftmod:trench_wall` |
| **Shape** | Directional — connects like fence, forms L/T/+ joints |
| **Material** | Dirt + timber appearance |
| **Blast resistance** | 2 |
| **Breaks with** | Shovel or Axe |
| **Notes** | Forms the main structural element of generated Trench Networks. |

---

### Artillery Platform

| Property | Value |
|---|---|
| **Block ID** | `mindcraftmod:artillery_platform` |
| **Shape** | 3x3 flat raised stone slab |
| **Special** | Required base block to place Cannon entity |
| **Blast resistance** | 10 |
| **Breaks with** | Pickaxe |

---

### Field Telephone

| Property | Value |
|---|---|
| **Block ID** | `mindcraftmod:field_telephone` |
| **Interaction** | Right-click opens chat input |
| **Function** | Sends text message to all players in same faction within 200 blocks |
| **Crafting** | 3x Iron Ingot + 2x Redstone + 1x String → 1x Field Telephone |
| **Notes** | Message appears as `[Field Tel - <FactionName>] <PlayerName>: <message>` |

---

### Supply Crate

| Property | Value |
|---|---|
| **Block ID** | `mindcraftmod:supply_crate` |
| **Interaction** | Right-click opens 27-slot inventory (like chest) |
| **Loot** | Pre-filled from loot table on world gen placement |
| **Breaks with** | Axe (drops empty Supply Crate item) |
| **Notes** | Does not connect like a double chest. Found in structures and dropped by Supply Drop event. |

#### Supply Crate Loot Table (`data/mindcraftmod/loot_tables/supply_crate.json`)

| Tier | Items | Weight |
|---|---|---|
| Common | Rifle Cartridge x8-16, Field Rations x3-5, Gunpowder x4 | 60% |
| Uncommon | Trench Bayonet, Gas Mask, Grenade x2 | 30% |
| Rare | Bolt-Action Rifle, Trench Coat, Signal Flare | 10% |

---

### Shell Crater

| Property | Value |
|---|---|
| **Block ID** | `mindcraftmod:shell_crater` |
| **Shape** | Multi-block: 3x3 base, 2-block deep depression |
| **Placed by** | Artillery Barrage world event, Cannon fire |
| **Fills with** | Water if placed near water source (creates muddy puddle) |
| **Notes** | Decorative but provides cover (lowers player hit box visibility from Sniper mob). |

---

## Decorative Blocks

| Block ID | Description |
|---|---|
| `mindcraftmod:barbed_wire_post` | Vertical post that barbed wire can visually connect to |
| `mindcraftmod:rusted_iron_bars` | Iron bars with rust texture |
| `mindcraftmod:war_poster` | Directional block like a sign, 4 poster variants |
| `mindcraftmod:grave_marker` | Small cross grave marker, places like a sign |
| `mindcraftmod:flag_block` | Faction flag — Allies (blue) or Central Powers (red); used for Territory Control |

---

## Java Edition Block Registration

All blocks registered in `ModBlocks.java` via Fabric's `Registry.register(Registries.BLOCK, ...)` pattern.

Block items auto-registered for all non-fluid blocks using `BlockItem` with `ModItemGroups.WAR_SUPPLIES` creative tab.

Block states in `assets/mindcraftmod/blockstates/`.
Models in `assets/mindcraftmod/models/block/`.
Textures in `assets/mindcraftmod/textures/block/`.

---

## Bedrock Edition Coverage

| Java Block | Bedrock Equivalent | Notes |
|---|---|---|
| Barbed Wire | ✅ `mindcraft:barbed_wire` | JSON block + Script API for damage |
| Gas Cloud | ✅ `mindcraft:gas_cloud` | Simplified: no spread mechanic |
| Smoke Screen | ✅ `mindcraft:smoke_screen` | Static 3x3 placement only |
| Mud Pit | ✅ `mindcraft:mud_pit` | Via `minecraft:movement_modifier` component |
| Sandbag | ✅ `mindcraft:sandbag` | Layer variant per block (8 block types) |
| Trench Wall | ✅ `mindcraft:trench_wall` | 4 directional variants |
| Supply Crate | ✅ `mindcraft:supply_crate` | Via `minecraft:loot` component |
| Field Telephone | ⚠️ Partial | Chat relay via Script API, no GUI |
| Shell Crater | ✅ `mindcraft:shell_crater` | Single-block depression variant |
| Artillery Platform | ✅ `mindcraft:artillery_platform` | No Cannon entity on Bedrock Phase 1 |
