# Mindcraft Mod — Manual Test Checklist

Run automated tests first, then work through this checklist at each phase milestone.

```
./gradlew test           # JUnit 5 (no Minecraft needed)
./gradlew runGametest    # Fabric GameTest (headless game instance)
```

---

## How to use this file

- Check off items as you verify them in-game
- Sign off each phase section before starting the next phase
- Items marked ⚠️ require two players or a dedicated server

---

## Phase 2 — Blocks

### Setup
1. Run `./gradlew runClient`
2. Create a Creative Mode world
3. Open inventory → "War Supplies" tab — all blocks and items should appear

### Pass-Through Blocks

**Barbed Wire**
- [ ] Barbed Wire appears in War Supplies creative tab
- [ ] Barbed Wire can be placed on solid surfaces (stone, dirt, etc.)
- [ ] Barbed Wire cannot be placed in mid-air (no solid block beneath)
- [ ] Player walks through Barbed Wire with no collision (does not stop movement)
- [ ] Player gets Slowness II (hotbar effect icon) while inside Barbed Wire
- [ ] Player takes 1 damage per second while standing in Barbed Wire
- [ ] Barbed Wire shows correct flat outline shape when hovering cursor
- [ ] Barbed Wire broken with Shears → drops Barbed Wire item
- [ ] Barbed Wire broken without tool → drops nothing

**Gas Cloud**
- [ ] Gas Cloud appears in War Supplies creative tab
- [ ] Gas Cloud is visually semi-transparent (not solid)
- [ ] Player walks through Gas Cloud with no collision
- [ ] Player gets Poison II (green effect icon) while inside Gas Cloud
- [ ] Player wearing Gas Mask does NOT get Poison while in Gas Cloud
- [ ] Gas Cloud spreads to adjacent air blocks over time (watch for ~30-60 seconds)
- [ ] Gas Cloud eventually disappears on its own (may take 60-120 seconds)
- [ ] Gas Cloud broken by hand → disappears, drops nothing

**Smoke Screen**
- [ ] Smoke Screen is visually dark/gray and non-solid
- [ ] Player gets Blindness (black screen effect) while inside Smoke Screen
- [ ] Blindness persists for ~3 seconds after player exits Smoke Screen
- [ ] Smoke Screen disappears over ~30 seconds

**Mud Pit**
- [ ] Mud Pit is solid (player cannot pass through)
- [ ] Player on top of Mud Pit gets Slowness II
- [ ] Player jump height is reduced on Mud Pit (cannot jump as high as normal)
- [ ] Breaking Mud Pit with Shovel → drops Mud Ball x1-2
- [ ] Breaking Mud Pit without Shovel → drops nothing (or Mud Ball per loot table)

### Functional Blocks

**Sandbag**
- [ ] Placing first Sandbag creates 1-layer height (very flat)
- [ ] Placing additional Sandbags at same position increases layer height
- [ ] Stack of 8 Sandbags = full block height, solid collision
- [ ] Cannot stack beyond 8 layers at same position
- [ ] Sandbag resists explosion better than dirt (test with TNT — should not crater sandbags easily)

**Trench Wall**
- [ ] Trench Wall connects to adjacent Trench Walls (like a fence/wall)
- [ ] Trench Wall connects to solid blocks on sides
- [ ] Trench Wall shows low/tall connection variants correctly

**Supply Crate**
- [ ] Supply Crate opens 9x3 inventory on right-click
- [ ] Container title reads "Supply Crate"
- [ ] Breaking Supply Crate with Axe drops Supply Crate item + any contents
- [ ] Contents drop as item entities when broken

**Field Telephone**
- [ ] Field Telephone is directional (faces player when placed)
- [ ] Right-clicking shows action bar message about faction
- [ ] ⚠️ Two-player: message reaches same-faction player within 200 blocks

**Flag Block**
- [ ] Flag Block (neutral) can be placed
- [ ] Right-clicking Flag Block shows faction ownership in action bar

**Shell Crater**
- [ ] Shell Crater is 14px tall (slightly sunken)
- [ ] Walking on Shell Crater applies mild Slowness I

---

## Phase 3 — Items & Weapons

**Bolt-Action Rifle**
- [ ] Appears in War Supplies creative tab
- [ ] Right-click aims (slight FOV change — Phase 7)
- [ ] Release fires projectile
- [ ] 1-second delay enforced between shots
- [ ] Without Rifle Cartridge in inventory: click sound, no shot
- [ ] Projectile travels straight (no gravity)
- [ ] Projectile deals ~8 damage to a mob
- [ ] Durability decreases per shot

**Trench Bayonet**
- [ ] Deals 6 attack damage (check F3 debug or hit a mob with known HP)
- [ ] Has sweep attack
- [ ] Crafting: Iron Ingot + Iron Ingot + Stick → Trench Bayonet

**Grenade**
- [ ] Right-click to hold/prime
- [ ] Release throws grenade
- [ ] 3-second fuse before explosion
- [ ] Explosion radius ~5 blocks
- [ ] Crafting: Iron Ingot + Gunpowder + Iron Ingot → Grenade

**Gas Mask**
- [ ] Equippable in helmet slot
- [ ] Negates Gas Cloud Poison while worn (test with Gas Cloud)
- [ ] Negates Smoke Screen Blindness while worn

**Trench Coat**
- [ ] Equippable in chestplate slot
- [ ] Provides armor (check armor bar)
- [ ] Mud Pit slowness reduced to Slowness I while wearing Trench Coat

**Signal Flare (Red)**
- [ ] Thrown with right-click
- [ ] Lands and triggers Gas Attack event
- [ ] Server chat broadcast: `[WAR EVENT] Gas attack at X, Z...`

**Signal Flare (Green)**
- [ ] Thrown and triggers Supply Drop event
- [ ] Supply Crate falls from sky at landing location

**Signal Flare (Gray)**
- [ ] Thrown and places Smoke Screen at landing location
- [ ] No server-wide broadcast (tactical only)

---

## Phase 4 — Mobs & Entities

**War Horse**
- [ ] Spawns in Plains/Savanna biomes
- [ ] Can be tamed with Sugar or Hay Bale
- [ ] Rideable after taming
- [ ] Cavalry charge deals ~10 damage while sprinting on horseback
- [ ] Equipping Horse Armor Plate → armor visible

**Carrier Pigeon**
- [ ] Spawns near Trench structures
- [ ] Right-click with Written Book → delivery mode activates
- [ ] ⚠️ Two-player: pigeon delivers book to same-faction player

**Guard Dog**
- [ ] Spawns near Artillery Emplacements
- [ ] Attacks player who enters radius (before taming)
- [ ] Tamed with 3x Field Rations
- [ ] After taming: follows player, sits on command
- [ ] ⚠️ Two-player: alerts owner with chat message when hostile enters range

**Trench Soldier**
- [ ] Spawns inside Trench Networks (structure spawner)
- [ ] Fires rifle projectile from distance
- [ ] Retreats to sandbag/wall when low health
- [ ] Drops Rifle Cartridges + chance for Bolt-Action Rifle

**Sniper**
- [ ] Spawns atop Observation Towers
- [ ] Detects player at 30 blocks
- [ ] Detection range reduced when player crouches

**Gas Grenadier**
- [ ] Spawns in Trench Networks
- [ ] Throws Gas Canister projectile
- [ ] Gas Cloud blocks appear on impact
- [ ] Drops Gas Mask (20% chance)

---

## Phase 5 — Structures

Use `/locate structure mindcraftmod:trench_network` to find structures.

**Trench Network**
- [ ] Generates in Plains/Savanna biomes
- [ ] Contains Trench Wall, Sandbag walls, Barbed Wire perimeter
- [ ] Has 1+ dugout rooms with Supply Crates and Field Telephone
- [ ] Has 1 Flag Block
- [ ] Trench Soldier spawns present (3-6)

**Command Bunker**
- [ ] Underground beneath Trench Network
- [ ] Accessible via ladder from trench dugout
- [ ] 3 rooms: Command Room, Barracks, Armory
- [ ] Supply Crates with uncommon/rare loot in armory

**Artillery Emplacement**
- [ ] Generates on hills
- [ ] Has Artillery Platform, Sandbag walls
- [ ] 2-3 Trench Soldier guards present

**Observation Tower**
- [ ] Generates in forest biomes
- [ ] 15 blocks tall with ladder
- [ ] Sniper at top
- [ ] Loot chest at base

**Field Hospital**
- [ ] White wool tent
- [ ] Beds inside
- [ ] Healing loot (Golden Apples, Healing Potions)
- [ ] Standing inside provides Regeneration I (healing zone)

**Ruined Village**
- [ ] 4-8 destroyed buildings
- [ ] Shell Craters between buildings
- [ ] Supply Crates (common loot) scattered throughout

**Supply Crate Loot**
- [ ] Open 5 Supply Crates — verify all 3 tiers drop within expected weights
- [ ] Rifle Cartridges are most common drop
- [ ] Bolt-Action Rifle is rare (~10% of crates)

---

## Phase 6 — Multiplayer

All items below require two players on the same server.

**Faction System**
- [ ] ⚠️ Player A: `/faction join allies`  — receives blue confirmation
- [ ] ⚠️ Player B: `/faction join central` — receives red confirmation
- [ ] ⚠️ `/faction info` shows correct faction for each player
- [ ] ⚠️ `/faction list` lists all online players by faction

**Field Telephone**
- [ ] ⚠️ Player A (Allies) right-clicks Field Telephone and sends message
- [ ] ⚠️ Player B (Allies) within 200 blocks receives message
- [ ] ⚠️ Player C (Central Powers) nearby does NOT receive the message

**Territory Control**
- [ ] ⚠️ Player stands within 3 blocks of Flag Block — action bar shows capture progress %
- [ ] ⚠️ After 2 minutes uncontested, flag converts to player's faction color
- [ ] ⚠️ Server broadcasts territory capture to all online players
- [ ] ⚠️ Enemy player approaching contested flag shows "contested" message

**World Events — Artillery Barrage**
- [ ] Event fires after 20-40 minutes (or reduce interval in config for testing)
- [ ] ⚠️ All players receive `[WAR EVENT] Artillery barrage incoming...` chat
- [ ] 10-second warning countdown in chat
- [ ] Shell Craters appear in a 30x30 zone
- [ ] Players in zone take 3 damage

**World Events — Signal Flare (Red → Gas Attack)**
- [ ] ⚠️ All players receive Gas Attack broadcast when flare lands
- [ ] Gas Cloud blocks appear in 15x15 zone

**World Events — Supply Drop (Green Flare)**
- [ ] ⚠️ All players receive Supply Drop broadcast
- [ ] Supply Crate falls from sky at flare landing position

**Reinforcements Event**
- [ ] ⚠️ Join with 3+ players on one faction, 1 on other — Reinforcements should trigger within 5 minutes
- [ ] ⚠️ 3-5 Trench Soldiers spawn near outnumbered faction's captured trench

---

## Phase 7 — Polish

**Sounds**
- [ ] Rifle shot sound plays on fire
- [ ] Grenade explosion sound plays on detonate
- [ ] Carrier Pigeon coos when idle
- [ ] Artillery Barrage event plays rumble/explosion sounds

**Particles**
- [ ] Gas Cloud has yellow-green particle effect
- [ ] Smoke Screen has gray particle effect
- [ ] Grenade and artillery explosions have smoke/fire particles

**HUD Overlay**
- [ ] Faction color displayed as boss bar at top of screen
- [ ] Territory capture progress bar visible near Flag Block

**Advancements**
- [ ] "Boots on the Ground" — join a faction
- [ ] "No Man's Land" — walk through Barbed Wire (survive)
- [ ] "Mask Up" — equip Gas Mask
- [ ] "Over the Top" — capture your first Flag Block

**Bedrock Parity**
- [ ] `.mcpack` files import cleanly
- [ ] All blocks appear in creative inventory
- [ ] Gas effects work via Script API
- [ ] Signal Flare events fire correctly
- [ ] Territory capture logic works via scoreboard system
- [ ] Test on actual Switch or Xbox if available

---

## Regression Checklist (before release)

Run before any release tag:

```bash
cd java-edition
./gradlew test          # must pass 0 failures
./gradlew runGametest   # must pass all 15+ GameTests
./gradlew build         # must compile clean
```

Then do a full in-game pass through Phase 2-7 checklists above.
