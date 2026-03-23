# Mindcraft Mod — Texture Art Plan
## WW1 Theme: Color Palette & Per-Item Specs

---

## Color Palette

All textures should draw from this restrained, desaturated WW1 palette.
Minecraft item textures are 16×16 pixels, RGBA PNG.

### Primary Colors
| Role            | Hex       | RGB             | Usage |
|-----------------|-----------|-----------------|-------|
| Khaki / Field   | `#8B7D52` | 139, 125, 82    | Uniforms, sandbags, coats |
| Olive Drab      | `#6B6B3A` | 107, 107, 58    | Equipment, helmets, crates |
| Mud Brown       | `#5C4A2A` | 92, 74, 42      | Mud pit, mud ball, shell crater |
| Aged Steel      | `#7A7A7A` | 122, 122, 122   | Gun metal, bayonets, wire |
| Dark Iron       | `#3D3D3D` | 61, 61, 61      | Barrel shading, dark metal |
| Rust / Oxide    | `#8B4513` | 139, 69, 19     | Rusted barbed wire, old iron |
| Canvas Tan      | `#C8B88A` | 200, 184, 138   | Bags, straps, rations wrap |
| Worn Leather    | `#7B5E3A` | 123, 94, 58     | Leather scrap, coat details |

### Accent Colors (used sparingly)
| Role             | Hex       | RGB             | Usage |
|------------------|-----------|-----------------|-------|
| Signal Red       | `#C0392B` | 192, 57, 43     | Signal flare (red), danger |
| Signal Green     | `#27AE60` | 39, 174, 96     | Signal flare (green), friendly |
| Bone / Neutral   | `#E8DEC8` | 232, 222, 200   | Signal flare (gray/white smoke) |
| Pale Yellow Gas  | `#C8C820` | 200, 200, 32    | Gas cloud, gas canister fill |
| Blood Red        | `#8B0000` | 139, 0, 0       | Bayonet blade tip accent |
| Brass            | `#B8860B` | 184, 134, 11    | Rifle cartridge casing |

### Background / Shadow
| Role        | Hex       | Usage |
|-------------|-----------|-------|
| Shadow      | `#1A1A1A` | Outlines, barrel end, shadow pixels |
| Transparent | `#00000000` | All non-drawn pixels (alpha = 0) |

---

## Pixel Art Style Guide

- **Outline**: 1-pixel dark outline (`#1A1A1A`) on all visible edges against transparent bg
- **Shading**: 2–3 tone shading (base color, one highlight +30 brightness, one shadow -30)
- **No anti-aliasing**: Hard pixel edges only — matches vanilla Minecraft style
- **Detail level**: Readable silhouette at 16×16; fine detail at 32×32 if using resource pack scale
- **Held-item orientation**: Items held in hand are rotated 45° by the engine — draw upright

---

## Item Texture Specs

### Weapons

#### `bolt_action_rifle.png` — 16×16
- **Silhouette**: Long horizontal rifle shape, stock right, barrel left
- **Colors**: Aged Steel barrel `#7A7A7A`, Dark Iron shading `#3D3D3D`, Khaki wood stock `#8B7D52`
- **Detail**: 3-pixel barrel, trigger guard loop, bolt handle nub upper-right
- **Stand-in**: `minecraft:item/crossbow`

#### `trench_bayonet.png` — 16×16
- **Silhouette**: Straight blade, cross-guard, short handle — oriented diagonally NW→SE
- **Colors**: Aged Steel blade `#7A7A7A`, Dark Iron edge `#3D3D3D`, Blood Red tip `#8B0000`, Worn Leather handle `#7B5E3A`
- **Detail**: Single-edge highlight on upper blade face
- **Stand-in**: `minecraft:item/iron_sword`

#### `rifle_cartridge.png` — 16×16
- **Silhouette**: Single bullet pointing upward — narrow neck, wider casing body
- **Colors**: Brass casing `#B8860B`, Aged Steel tip `#7A7A7A`, Dark Iron shadow `#3D3D3D`
- **Detail**: Faint seam line between tip and case; base rim 1px darker
- **Stand-in**: `minecraft:item/arrow`

---

### Throwables / Explosives

#### `grenade.png` — 16×16
- **Silhouette**: Oval/egg body with segmented lines and a short stick handle (stick grenade style)
- **Colors**: Olive Drab body `#6B6B3A`, Dark Iron segments `#3D3D3D`, Canvas Tan handle `#C8B88A`
- **Detail**: 4-segment grid on body, pin loop at top (1–2 px)
- **Stand-in**: `minecraft:item/fire_charge`

#### `mud_ball.png` — 16×16
- **Silhouette**: Rough circle, slightly uneven edges (not perfectly round)
- **Colors**: Mud Brown `#5C4A2A`, lighter highlight patch `#7A6040`, darker shadow `#3A2A10`
- **Detail**: 2–3 px irregular lighter patch upper-left for roundness
- **Stand-in**: `minecraft:item/snowball`

#### `gas_canister.png` — 16×16
- **Silhouette**: Upright cylinder — flat top with nozzle, wider body, flat base
- **Colors**: Olive Drab body `#6B6B3A`, Aged Steel nozzle `#7A7A7A`, Pale Yellow Gas fill tint `#C8C820` (1-px ring visible at top)
- **Detail**: Vertical highlight stripe on left side of cylinder; "GAS" text not feasible at 16px — use a ☠ skull 3×3 px mark instead
- **Stand-in**: `minecraft:item/bucket`

---

### Signal Flares

All three flares share the same **silhouette** — a short cylindrical tube with a flared cap —
differing only in the **flame/smoke color** at the top.

#### `signal_flare_red.png` — 16×16
- **Body**: Khaki `#8B7D52` cylinder, Dark Iron bands `#3D3D3D`
- **Flame**: Signal Red `#C0392B` with 1-px lighter core `#E74C3C`
- **Stand-in**: `minecraft:item/torch`

#### `signal_flare_green.png` — 16×16
- **Body**: Same as red flare
- **Flame**: Signal Green `#27AE60` with 1-px lighter core `#2ECC71`
- **Stand-in**: `minecraft:item/torch`

#### `signal_flare_gray.png` — 16×16
- **Body**: Same as red flare
- **Flame/Smoke**: Bone `#E8DEC8` puff (3×3 cloud shape) — represents white smoke
- **Stand-in**: `minecraft:item/torch`

---

### Armor / Wearables

#### `gas_mask.png` — 16×16
- **Silhouette**: Oval face-plate, two round lens circles, strap loops at sides, snout/filter cylinder
- **Colors**: Olive Drab shell `#6B6B3A`, Dark Iron lens rings `#3D3D3D`, Aged Steel lens glass `#7A7A7A` (or transparent for "glass" effect)
- **Detail**: Round lens ~4×4 px each, centered; filter nub lower-center 2×3 px
- **Stand-in**: `minecraft:item/iron_helmet`

#### `trench_coat.png` — 16×16
- **Silhouette**: Front view of coat — wide shoulders, double-breasted button row, wide lapels
- **Colors**: Khaki main `#8B7D52`, Canvas Tan lapels `#C8B88A`, Dark Iron button dots `#3D3D3D`
- **Detail**: 4 button dots in center column; collar notch at top
- **Stand-in**: `minecraft:item/leather_chestplate`

#### `horse_armor_plate.png` — 16×16
- **Silhouette**: Rectangular steel plate with rounded corners and a center ridge
- **Colors**: Aged Steel `#7A7A7A`, Dark Iron edge `#3D3D3D`, light highlight stripe along ridge
- **Detail**: 2 rivet dots on left and right sides; looks like barding
- **Stand-in**: `minecraft:item/iron_horse_armor`

---

### Consumables / Crafting

#### `field_rations.png` — 16×16
- **Silhouette**: Small tin can with a key-tab pull on top, or a wrapped cloth parcel tied with string
- **Colors**: Canvas Tan wrap `#C8B88A`, Worn Leather tie `#7B5E3A`, Aged Steel can rim `#7A7A7A`
- **Detail**: Horizontal wrap line across middle; tied knot at center top
- **Stand-in**: `minecraft:item/bread`

#### `leather_scrap.png` — 16×16
- **Silhouette**: Irregular torn piece of leather — no straight edges
- **Colors**: Worn Leather `#7B5E3A`, Mud Brown shadow `#5C4A2A`, Canvas Tan worn edge `#C8B88A`
- **Detail**: 1–2 ragged edge pixels to imply torn material
- **Stand-in**: `minecraft:item/leather`

---

## Block Texture Gaps

The following blocks have placeholder PNGs (all 86 bytes / near-transparent).
These need proper 16×16 textures as well, but are lower priority than items
since block textures default to the missing-texture purple instead of invisible.

| Block              | File                        | Style Notes |
|--------------------|-----------------------------|-------------|
| `sandbag`          | `textures/block/sandbag.png`| Hessian weave pattern, Canvas Tan + Khaki |
| `trench_wall`      | `textures/block/trench_wall.png` | Rough timber plank, Mud Brown + Dark Iron nails |
| `barbed_wire`      | `textures/block/barbed_wire.png` | Thin diagonal wire lines, Rust color `#8B4513` |
| `gas_cloud`        | `textures/block/gas_cloud.png` | Wispy translucent yellow-green, alpha ~128 |
| `smoke_screen`     | `textures/block/smoke_screen.png` | Gray translucent puff, alpha ~160 |
| `mud_pit`          | `textures/block/mud_pit.png` | Dark brown smooth with crack lines |
| `artillery_platform` | `textures/block/artillery_platform.png` | Stone + metal plate grid |
| `field_telephone`  | `textures/block/field_telephone.png` | Dark olive box with handset silhouette |
| `supply_crate`     | `textures/block/supply_crate.png` | Wooden crate slats + stenciled "SUPPLIES" |
| `shell_crater`     | `textures/block/shell_crater.png` | Dark dirt with radiating crack lines |
| `flag_block`       | `textures/block/flag_block.png` | Pole + flat colored flag panel |
| `barbed_wire_post` | `textures/block/barbed_wire_post.png` | Upright iron post, Rust color |
| `rusted_iron_bars` | `textures/block/rusted_iron_bars.png` | Iron bars with Rust overlay patches |

---

## Implementation Order (suggested)

1. **Items** (high visibility, test feedback fast)
   - [ ] bolt_action_rifle
   - [ ] trench_bayonet
   - [ ] rifle_cartridge
   - [ ] grenade
   - [ ] gas_canister
   - [ ] mud_ball
   - [ ] signal_flare_red
   - [ ] signal_flare_green
   - [ ] signal_flare_gray
   - [ ] gas_mask
   - [ ] trench_coat
   - [ ] horse_armor_plate
   - [ ] field_rations
   - [ ] leather_scrap

2. **Blocks** (visible in world, affects feel)
   - [ ] sandbag
   - [ ] trench_wall
   - [ ] barbed_wire
   - [ ] mud_pit
   - [ ] shell_crater
   - [ ] supply_crate
   - [ ] gas_cloud (semi-transparent)
   - [ ] smoke_screen (semi-transparent)
   - [ ] artillery_platform
   - [ ] field_telephone
   - [ ] flag_block
   - [ ] barbed_wire_post
   - [ ] rusted_iron_bars
