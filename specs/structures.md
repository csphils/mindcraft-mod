# Structures Specification

---

## Java Edition Implementation Notes

Structures built using the in-game Structure Block tool, exported as `.nbt` files.
NBT files stored in `data/mindcraftmod/structures/`.
Structure registration via `StructureType`, `Structure`, and `StructurePlacement` registered in `ModStructures.java`.
World generation hooks via Fabric API `BiomeModifications.addStructure()`.

Structure class hierarchy:
```
structure/
├── ModStructures.java          # Registry of all structure types
├── ModStructureSets.java       # Defines placement (frequency, spread)
├── TrenchNetworkStructure.java # Custom structure logic
├── NoMansLandStructure.java
├── CommandBunkerStructure.java
├── ArtilleryEmplacementStructure.java
├── ObservationTowerStructure.java
├── FieldHospitalStructure.java
└── RuinedVillageStructure.java
```

---

## Structures

### Trench Network

| Property | Value |
|---|---|
| **Structure ID** | `mindcraftmod:trench_network` |
| **Biomes** | Plains, Savanna, Sunflower Plains |
| **Spawn frequency** | ~1 per 800x800 chunk area |
| **Footprint** | 40-60 blocks long, 8-12 blocks wide |
| **Depth** | 3 blocks below surface |
| **Generation** | Procedural: random L/T/+ junctions from segment NBT pieces |

#### Segments (NBT pieces):
- `trench_straight.nbt` — 8-block straight section
- `trench_corner.nbt` — 90° corner
- `trench_t_junction.nbt` — T-intersection
- `trench_dugout.nbt` — 3x3 room off the main trench (Supply Crate, Field Telephone inside)
- `trench_entrance.nbt` — Ladder up to surface with sandbag walls

#### Contents:
- Trench Wall blocks lining all sides
- Sandbag stacks (2-3 layers) at surface level openings
- Barbed Wire strips at all surface approaches
- 1x Supply Crate per dugout section (loot table: `supply_crate`)
- 1x Field Telephone per trench network
- 1x Flag Block (Allies or Central Powers, 50/50 on spawn)
- Mob spawners: Trench Soldier (3-6), Guard Dog (1-2), Gas Grenadier (1-2)
- Carrier Pigeon ambient spawn (2-4, not via spawner)

---

### No Man's Land

| Property | Value |
|---|---|
| **Structure ID** | `mindcraftmod:no_mans_land` |
| **Biomes** | Plains, Savanna (between two Trench Networks) |
| **Spawn condition** | Always placed between a pair of Trench Networks when both generate within 100 blocks |
| **Footprint** | Variable — fills the gap between the two Trench Networks |

#### Contents:
- Mud Pit blocks replacing ground layer
- Shell Craters: 3-8 scattered across area
- Barbed Wire strips in 3-4 horizontal bands across the zone
- Gas Cloud patches: 1-3 clusters of 2x2 gas blocks
- No mob spawners (No Man's Land is a crossing zone, not a base)

---

### Command Bunker

| Property | Value |
|---|---|
| **Structure ID** | `mindcraftmod:command_bunker` |
| **Biomes** | Same as Trench Network — always generated underground below one |
| **Footprint** | 20x12 underground, 3 rooms |
| **Entrance** | Ladder shaft from inside the Trench Network dugout |

#### Rooms:
1. **Command Room** — Map table (vanilla), Field Telephone, 2x Supply Crates (uncommon loot)
2. **Barracks** — Beds (3-4), chests with Field Rations + Rifle Cartridges
3. **Armory** — 2x Supply Crates (rare loot), Bolt-Action Rifle display rack (item frame)

#### Mobs:
- Trench Soldier: 2-3 in command room and barracks
- Gas Grenadier: 1 in armory
- Trench Rat: 2-4 ambient

---

### Artillery Emplacement

| Property | Value |
|---|---|
| **Structure ID** | `mindcraftmod:artillery_emplacement` |
| **Biomes** | Hills, Windswept Hills, Meadow |
| **Spawn frequency** | ~1 per 1200x1200 chunk area |
| **Footprint** | 15x15, elevated on hill |
| **Height** | Generates on natural terrain peaks |

#### Contents:
- Artillery Platform (3x3 stone slab raised base)
- Sandbag walls around perimeter (3 layers high)
- 1x Cannon entity on the platform (fires Artillery Shells at random intervals as world event)
- 1x Supply Crate (rare loot)
- Mob spawners: Trench Soldier (2-3), Guard Dog (1)
- Horse pen: 1-2 War Horses tethered

---

### Observation Tower

| Property | Value |
|---|---|
| **Structure ID** | `mindcraftmod:observation_tower` |
| **Biomes** | Forest, Birch Forest, Taiga |
| **Spawn frequency** | ~1 per 600x600 chunk area |
| **Footprint** | 5x5 base, 15 blocks tall |

#### Contents:
- Wooden tower with ladder on each floor
- Small platform on top with railing (Barbed Wire Posts)
- 1x Sniper mob at top
- 1x Chest at base (uncommon loot)
- Small campfire and sitting area at base (decorative)

---

### Field Hospital

| Property | Value |
|---|---|
| **Structure ID** | `mindcraftmod:field_hospital` |
| **Biomes** | Plains, Meadow (near Trench Networks preferably) |
| **Spawn frequency** | ~1 per 1000x1000 chunk area |
| **Footprint** | 12x10 tent structure |

#### Contents:
- White wool tent structure (canvas aesthetic)
- Beds: 4-6 (healing area — standing near a bed regenerates health at 1 HP/5 sec while in structure)
- Healing loot chests: Golden Apples x1-2, Potions of Healing x2-4, Potions of Regeneration x1
- 1x Carrier Pigeon ambient spawn
- No hostile mobs spawn in this structure

#### Special mechanic: Healing Zone
Players within 10 blocks of a Field Hospital's Flag Block (white flag) receive Regeneration I passively.
This is implemented as a Block Entity tick on the white Flag Block variant.

---

### Ruined Village

| Property | Value |
|---|---|
| **Structure ID** | `mindcraftmod:ruined_village` |
| **Biomes** | Plains, Savanna, Taiga, Snowy Plains |
| **Spawn frequency** | ~1 per 500x500 chunk area |
| **Footprint** | 40-60 blocks (variable, 4-8 ruined buildings) |

#### Contents (per building):
- Partial walls (missing sections, cracked stone/wood), no roofs on most
- Shell Craters in streets between buildings
- 1-2 Supply Crates per village (common loot)
- Abandoned item frames with items still in them (50% chance: war poster, 50% empty)
- Grave Markers scattered throughout
- 1-2 Trench Soldiers patrol the village streets

#### Generation approach:
- Place 4-8 building NBT pieces from a pool of ruined variants
- Scatter Shell Craters procedurally between pieces
- Connect with gravel/mud paths

---

## Loot Tables

### `data/mindcraftmod/loot_tables/supply_crate.json`

```json
{
  "type": "minecraft:chest",
  "pools": [
    {
      "rolls": { "min": 2, "max": 4 },
      "entries": [
        { "type": "item", "name": "mindcraftmod:rifle_cartridge",
          "functions": [{ "function": "minecraft:set_count", "count": {"min": 8, "max": 16} }],
          "weight": 30 },
        { "type": "item", "name": "mindcraftmod:field_rations",
          "functions": [{ "function": "minecraft:set_count", "count": {"min": 3, "max": 5} }],
          "weight": 25 },
        { "type": "item", "name": "minecraft:gunpowder",
          "functions": [{ "function": "minecraft:set_count", "count": {"min": 2, "max": 6} }],
          "weight": 20 },
        { "type": "item", "name": "mindcraftmod:trench_bayonet", "weight": 10 },
        { "type": "item", "name": "mindcraftmod:gas_mask", "weight": 8 },
        { "type": "item", "name": "mindcraftmod:grenade",
          "functions": [{ "function": "minecraft:set_count", "count": {"min": 1, "max": 2} }],
          "weight": 8 },
        { "type": "item", "name": "mindcraftmod:bolt_action_rifle", "weight": 3 },
        { "type": "item", "name": "mindcraftmod:trench_coat", "weight": 3 },
        { "type": "item", "name": "mindcraftmod:signal_flare", "weight": 3 }
      ]
    }
  ]
}
```

---

## Bedrock Edition Structures

Bedrock structures exported from Java NBT using the Bedrock Structure Block, stored in `behavior_pack/structures/`.

| Java Structure | Bedrock | Method |
|---|---|---|
| Trench Network | ✅ | Structure Block export + Feature Rules |
| No Man's Land | ✅ | Simplified procedural via Script API |
| Command Bunker | ✅ | Structure Block export |
| Artillery Emplacement | ✅ | Structure Block export |
| Observation Tower | ✅ | Structure Block export |
| Field Hospital | ✅ | Structure Block export |
| Ruined Village | ✅ | 4 building variants |

Bedrock structure placement uses `minecraft:structure_template_feature` in Feature Rules JSON registered per biome.
