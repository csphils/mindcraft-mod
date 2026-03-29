# Mobs / Entities Specification

---

## Java Edition Implementation Notes

All entities registered in `ModEntities.java` using `FabricEntityTypeBuilder`.
Entity AI uses Fabric's `Goal`/`Task` system (extending vanilla Goal classes).
Entity models use `EntityModelLayer` + `EntityRenderer` registered in `EntityRendererRegistry`.
Spawn biomes and conditions registered via `SpawnPlacements` and `BiomeModifications` (Fabric API).

---

## Passive Mobs

### War Horse

| Property | Value |
|---|---|
| **Entity ID** | `mindcraftmod:war_horse` |
| **Health** | 30 HP (15 hearts) |
| **Rideable** | Yes — uses vanilla horse riding mechanics |
| **Tameable** | Yes — feed Sugar x3 or Hay Bale |
| **Armor** | Equip "Horse Armor Plate" in armor slot → +4 armor points |
| **Speed** | 0.3 base (vanilla horse: 0.225) |
| **Cavalry Charge** | While ridden and sprinting, deals 10 melee damage on contact with hostile mobs |
| **Drops** | Leather x1-3, Horse Armor Plate (if equipped) |
| **Spawn biome** | Plains, Savanna (replaces 20% of vanilla horse spawns) |
| **Model** | Extended vanilla horse model — larger build, military saddle texture |
| **Sound** | Custom heavy whinny, louder hoof impacts |
| **Bedrock** | ✅ via `minecraft:horse` base with behavior overrides |

---

### Carrier Pigeon

| Property | Value |
|---|---|
| **Entity ID** | `mindcraftmod:carrier_pigeon` |
| **Health** | 4 HP (2 hearts) |
| **Behavior** | Flies overhead, lands on Trench Wall / posts; ambient cooing sounds |
| **Interaction** | Right-click with a Written Book → enters "delivery mode" |
| **Delivery** | Player selects online faction-mate from list; pigeon flies to them and delivers the book |
| **Delivery mechanic** | Server-side: pigeon entity path-finds or teleports to target player's last known position, then plays arrive animation and delivers to player inventory |
| **Drops** | Feather x1 |
| **Spawn** | Near Trench structures (2-4 per network) |
| **Bedrock** | ⚠️ Partial — delivers message as chat text only (no item transfer Phase 1) |

---

### Trench Rat

| Property | Value |
|---|---|
| **Entity ID** | `mindcraftmod:trench_rat` |
| **Health** | 2 HP (1 heart) |
| **Behavior** | Passive, scurries along ground, flees players |
| **Drops** | Leather Scrap (custom item) x1, 50% chance |
| **Special** | Can be caught with an empty Bucket (right-click) → Bucket of Rat; release drops the rat again |
| **Spawn** | Inside Trench structures, Command Bunkers (dark areas, light level < 7) |
| **Bedrock** | ✅ simplified flee behavior |

---

## Neutral Mobs

### Guard Dog

| Property | Value |
|---|---|
| **Entity ID** | `mindcraftmod:guard_dog` |
| **Health** | 20 HP (10 hearts) |
| **Default behavior** | Patrols a 10-block radius around spawn point; attacks players who enter the radius |
| **Tameable** | Feed Field Rations x3 — becomes neutral, then follow/sit like vanilla wolf |
| **Tamed behavior** | Follows owner; when a hostile mob or non-faction player comes within 16 blocks, sends chat alert: `[Guard Dog] Enemy spotted nearby!` |
| **Attack** | 4 damage per bite, applies Slowness I for 2 seconds |
| **Drops** | None (tamed) / Bone x1 (wild) |
| **Spawn** | Near Artillery Emplacements, Trench structures (1-2 per structure) |
| **Bedrock** | ✅ via `minecraft:tameable` component + Script API alert |

---

## Hostile Mobs

### Trench Soldier

| Property | Value |
|---|---|
| **Entity ID** | `mindcraftmod:trench_soldier` |
| **Health** | 24 HP (12 hearts) |
| **Armor** | Leather chestplate + iron helmet equivalent (4 armor points) |
| **AI Goals (in priority order)** | 1. Float in water, 2. Flee if health < 6, 3. Fire rifle at target, 4. Melee attack if within 2 blocks, 5. Patrol trench path, 6. Idle look |
| **Ranged attack** | Fires `TrenchRifleProjectile` — 7 damage, 1.5 second cooldown, 24-block range |
| **Melee attack** | Bayonet — 5 damage |
| **Aggro range** | 20 blocks |
| **Retreat behavior** | At < 6 HP, pathfinds to nearest Sandbag or Trench Wall and crouches (reduced hitbox) |
| **Drops** | Rifle Cartridge x3-8, Field Rations x0-1, 10% chance: Bolt-Action Rifle |
| **Spawn** | In Trench Networks and Artillery Emplacements (3-6 per structure) |
| **Equipment** | Holds Bolt-Action Rifle in main hand visually |
| **Bedrock** | ✅ via ranged attack behavior component + Script API |

---

### Sniper

| Property | Value |
|---|---|
| **Entity ID** | `mindcraftmod:sniper` |
| **Health** | 18 HP (9 hearts) |
| **AI Goals** | 1. Stay within 5 blocks of spawn (rooftop/tower), 2. Track targets up to 30 blocks, 3. Fire after 2-second aim delay |
| **Ranged attack** | High-damage `SniperProjectile` — 12 damage, 3-second cooldown, 30-block range |
| **Detection** | Crouching players are detected at 50% range (15 blocks). Smoke Screen blocks detection entirely. |
| **Aggro** | Does not move to chase — fires from position. Will re-target if primary target crouches/hides. |
| **Drops** | Rifle Cartridge x4-10, 20% chance: Bolt-Action Rifle |
| **Spawn** | Top floor of Observation Towers (1 per tower, rare spawn) |
| **Bedrock** | ⚠️ Partial — ranged attack at range, no crouch detection in Phase 1 |

---

### Gas Grenadier

| Property | Value |
|---|---|
| **Entity ID** | `mindcraftmod:gas_grenadier` |
| **Health** | 20 HP (10 hearts) |
| **Armor** | Wears Gas Mask (immune to own gas) |
| **AI Goals** | 1. Throw gas canister at target (3-second cooldown), 2. Flee if target within 4 blocks, 3. Patrol |
| **Projectile** | `GasCanisters` entity — on impact, places 3x3 area of Gas Cloud blocks |
| **Melee** | Will melee if cornered — 3 damage |
| **Drops** | Gas Mask (20%), Gunpowder x2-4 |
| **Spawn** | Trench Networks (1-2 per network), Command Bunkers (1) |
| **Bedrock** | ✅ simplified gas area placement via Script API on projectile land event |

---

## Entity AI Notes (Java Edition)

### Custom Goal Classes

```
entity/ai/
├── goal/
│   ├── PatrolTrenchGoal.java      # Follows a waypoint path along trench
│   ├── RetreatToCoverGoal.java    # Finds nearest sandbag/wall when low health
│   ├── FireRifleGoal.java         # Ranged attack with aim delay and cooldown
│   ├── ThrowGasCanisteGoal.java   # Lob projectile toward target
│   └── AlertOwnerGoal.java        # Guard Dog faction alert
└── sensor/
    └── CrouchDetectionSensor.java # Reduces detection range for crouching targets
```

### Projectile Classes

```
entity/projectile/
├── TrenchRifleProjectile.java    # Fast, straight, 7 damage
├── SniperProjectile.java         # Fast, straight, 12 damage, small hitbox
└── GasCanisteProjectile.java     # Arcing (gravity), places gas blocks on impact
```

---

## Spawn Conditions

| Mob | Biome | Structure Required | Light Level | Max group |
|---|---|---|---|---|
| War Horse | Plains, Savanna | No | Any | 3 |
| Carrier Pigeon | Any | Trench Network | ≥ 8 | 4 |
| Trench Rat | Any | Trench / Bunker | ≤ 7 | 6 |
| Guard Dog | Any | Artillery / Trench | Any | 2 |
| Trench Soldier | Any | Trench / Artillery | Any | 6 |
| Sniper | Any | Observation Tower | Any | 1 |
| Gas Grenadier | Any | Trench / Bunker | Any | 2 |

---

## Creative Tab

All mob spawn eggs added to `ModItemGroups.WAR_SUPPLIES` creative tab.
Spawn eggs use faction-colored dye: Allied blue / Central red.
