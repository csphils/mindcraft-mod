# Mindcraft Mod — WW1 Warfare

A WW1-themed Minecraft mod featuring trench warfare, artillery, gas attacks, cavalry, and multiplayer faction battles.

---

## Editions

| Edition | Platform | Status |
|---|---|---|
| **Java Edition** (Fabric, MC 1.21.4) | PC | In development — Phase 1 complete |
| **Bedrock Edition** (Add-on) | Switch, Xbox, Mobile, Win10 | In development — Phase 1 complete |

---

## Features (Planned / In Progress)

### Blocks
- **Pass-through blocks**: Barbed Wire, Gas Cloud, Smoke Screen, Mud Pit
- **Functional blocks**: Sandbags (stackable), Trench Wall, Artillery Platform, Field Telephone, Supply Crate, Shell Crater
- **Decorative**: Barbed Wire Post, Rusted Iron Bars, War Poster, Grave Marker, Flag Block

### Mobs
- **Passive**: War Horse (cavalry charge), Carrier Pigeon (item delivery), Trench Rat
- **Neutral**: Guard Dog (tameable sentry)
- **Hostile**: Trench Soldier (rifle + bayonet), Sniper (30-block range), Gas Grenadier

### Items & Weapons
- Bolt-Action Rifle, Trench Bayonet, Grenade, Gas Canister, Mud Ball
- Gas Mask (negates gas effects), Trench Coat, Horse Armor Plate
- Field Rations, Signal Flares (Red/Green/Gray), Rifle Cartridge

### Structures (Procedurally Generated)
- Trench Network, No Man's Land, Command Bunker
- Artillery Emplacement, Observation Tower, Field Hospital, Ruined Village

### Multiplayer
- Faction system: Allies vs Central Powers
- Shared world events: Artillery Barrages, Gas Attacks, Supply Drops
- Territory control: Capture Flag Blocks to claim trench networks

---

## Project Structure

```
mindcraft-mod/
├── specs/                  # Feature design documents
│   ├── overview.md
│   ├── blocks.md
│   ├── mobs.md
│   ├── items.md
│   ├── structures.md
│   └── multiplayer.md
├── java-edition/           # Fabric mod (PC)
│   ├── build.gradle
│   ├── gradle.properties   # Pin: MC 1.21.4, Fabric Loader 0.16.10
│   └── src/main/
│       ├── java/com/mindcraftmod/
│       └── resources/
└── bedrock-edition/        # Bedrock Add-on (Switch/Xbox/Mobile)
    ├── behavior_pack/
    └── resource_pack/
```

---

## Setup

### Java Edition (PC)

Requirements: JDK 21, Gradle 8+

```bash
cd java-edition
./gradlew build          # Compile
./gradlew runClient      # Launch Minecraft with mod loaded
```

Output JAR: `java-edition/build/libs/mindcraftmod-*.jar`
Copy to your `.minecraft/mods/` folder.

### Bedrock Edition (Switch / Xbox / Mobile / Win10)

1. Zip `bedrock-edition/behavior_pack/` → rename to `mindcraftmod_bp.mcpack`
2. Zip `bedrock-edition/resource_pack/` → rename to `mindcraftmod_rp.mcpack`
3. Double-click each `.mcpack` file — Minecraft imports them automatically
4. Enable both packs when creating or editing a world

---

## Development Phases

- [x] **Phase 1**: Specs + project scaffold
- [ ] **Phase 2**: Core blocks (pass-through + functional)
- [ ] **Phase 3**: Items & weapons
- [ ] **Phase 4**: Mobs & entity AI
- [ ] **Phase 5**: Structures & world gen
- [ ] **Phase 6**: Multiplayer systems
- [ ] **Phase 7**: Polish (sounds, particles, advancements, HUD)

---

## Design Specs

See the [`specs/`](specs/) directory for detailed feature specifications covering every block, mob, item, structure, and multiplayer mechanic.

---

## License

MIT
