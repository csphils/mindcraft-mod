# Mindcraft Mod — WW1 Warfare Theme
## Project Overview

**Theme:** World War 1 — trench warfare, artillery, gas attacks, cavalry, and field tactics

**Tagline:** Bring the mud and thunder of the Great War to Minecraft.

---

## Editions

| Edition | Platform | Framework | Language |
|---|---|---|---|
| **Java Edition** (Primary) | PC only | Fabric, Minecraft 1.21.4 | Java 21 |
| **Bedrock Edition** (Secondary) | Switch, Xbox, Mobile, Win10 | Add-on (Behavior + Resource Pack) | JSON + JavaScript (Script API 2.0) |

Java Edition is the full-featured build. Bedrock is a curated subset with parity where the platform allows.

---

## Feature Summary

### Blocks
- **Pass-through blocks**: Barbed Wire, Gas Cloud, Smoke Screen, Mud Pit
- **Functional blocks**: Sandbags, Trench Wall, Artillery Platform, Field Telephone, Supply Crate, Shell Crater
- **Decorative**: Barbed wire post, rusted iron bars, war poster, grave marker

### Mobs
- **Passive**: War Horse, Carrier Pigeon, Trench Rat
- **Neutral**: Guard Dog (tameable)
- **Hostile**: Trench Soldier, Sniper, Gas Grenadier

### Items & Weapons
- Bolt-Action Rifle, Trench Bayonet, Grenade
- Gas Mask, Trench Coat, Field Rations
- Signal Flare, Rifle Cartridge

### Structures (Procedurally Generated)
- Trench Network, No Man's Land, Command Bunker
- Artillery Emplacement, Observation Tower, Field Hospital, Ruined Village

### Multiplayer Features
- Faction system (Allies vs Central Powers)
- Shared world events (Artillery Barrages, Gas Attacks, Supply Drops)
- Territory control via Flag Blocks

---

## Mod ID & Namespace
- **Mod ID:** `mindcraftmod`
- **Java package:** `com.mindcraftmod`
- **Bedrock namespace:** `mindcraft`

---

## Spec Files
- [blocks.md](blocks.md) — Block definitions and behaviors
- [mobs.md](mobs.md) — Entity definitions and AI behaviors
- [items.md](items.md) — Item and weapon definitions
- [structures.md](structures.md) — Structure layouts and world gen
- [multiplayer.md](multiplayer.md) — Faction, events, and territory systems

---

## Implementation Phases

| Phase | Focus |
|---|---|
| 1 | Specs + project setup (this phase) |
| 2 | Core blocks (pass-through + functional) |
| 3 | Items & weapons |
| 4 | Mobs & entity AI |
| 5 | Structures & world gen |
| 6 | Multiplayer systems |
| 7 | Polish (sounds, particles, advancements, HUD) |
