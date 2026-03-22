# Items & Weapons Specification

---

## Java Edition Implementation Notes

All items registered in `ModItems.java` via `Registry.register(Registries.ITEM, ...)`.
Custom item classes extend `Item` or appropriate vanilla subclasses (`RangedWeaponItem`, `SwordItem`, `ArmorItem`).
Crafting recipes in `data/mindcraftmod/recipes/` as JSON.
All items added to `ModItemGroups.WAR_SUPPLIES` creative tab.

---

## Weapons

### Bolt-Action Rifle

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:bolt_action_rifle` |
| **Type** | Custom ranged weapon |
| **Damage** | 8 per shot |
| **Range** | 30 blocks (effective); projectile travels full world |
| **Reload** | 1-second bolt-action delay between shots (enforced server-side) |
| **Ammo** | Consumes 1x Rifle Cartridge per shot from inventory |
| **No ammo** | Plays click sound, no projectile fired |
| **Durability** | 384 uses |
| **Use mechanic** | Right-click to aim (zoom 1.5x FOV); release to fire |
| **Projectile** | `TrenchRifleProjectile` — fast, straight, no gravity |
| **Crafting** | Disabled — only found in Supply Crates / Trench Soldier drops |
| **Enchantable** | Yes: Unbreaking, Mending only |
| **Stack size** | 1 |
| **Bedrock** | ✅ Via `minecraft:shooter` component + Script API for reload delay |

---

### Trench Bayonet

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:trench_bayonet` |
| **Type** | Sword subclass |
| **Damage** | 6 attack damage (+2 over iron sword) |
| **Attack speed** | 1.6 (slightly slower than sword) |
| **Sweep** | Yes — +2 sweep damage |
| **Durability** | 300 |
| **Crafting** | Iron Ingot + Iron Ingot + Stick (vertical column, shaped) |
| **Enchantable** | Yes — sword enchantments |
| **Bedrock** | ✅ Via `minecraft:digger` + damage component |

---

### Grenade

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:grenade` |
| **Type** | Throwable (extends `ThrownItemEntity`) |
| **Fuse** | 3 seconds after thrown |
| **Explosion radius** | 5 blocks |
| **Explosion damage** | 8 direct, decreasing with distance |
| **Block damage** | Yes (standard explosion) |
| **Use mechanic** | Right-click to pull pin and hold; release to throw (further = held longer, up to 1.5s) |
| **Crafting** | Iron Ingot (top) + Gunpowder (middle) + Iron Ingot (bottom) → 1x Grenade |
| **Stack size** | 16 |
| **Bedrock** | ✅ Via `minecraft:throwable` + Script API for delayed explosion |

---

## Armor

### Gas Mask

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:gas_mask` |
| **Slot** | Helmet |
| **Armor points** | 2 |
| **Special** | Negates Gas Cloud (Poison II) and Smoke Screen (Blindness) effects while worn |
| **Durability** | 165 |
| **Crafting** | 2x Iron Ingot + 2x Glass Pane + 1x String → Gas Mask |
| **Enchantable** | Yes — helmet enchantments |
| **Visual** | Full-head gas mask model with glass lens |
| **Bedrock** | ✅ Via Script API checking armor slot on effect application |

---

### Trench Coat

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:trench_coat` |
| **Slot** | Chestplate |
| **Armor points** | 5 (same as iron chestplate) |
| **Special** | Reduces Mud Pit slowness from Slowness II → Slowness I |
| **Durability** | 240 |
| **Crafting** | 7x Leather + 1x Iron Ingot (center) → Trench Coat (standard chestplate pattern) |
| **Enchantable** | Yes — chestplate enchantments |
| **Bedrock** | ✅ Via movement modifier Script API override |

---

### Horse Armor Plate

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:horse_armor_plate` |
| **Slot** | War Horse armor slot |
| **Armor points** | +4 to War Horse |
| **Crafting** | 7x Iron Ingot → Horse Armor Plate (standard horse armor pattern) |
| **Bedrock** | ✅ Standard `minecraft:horse_armor` item type |

---

## Utility Items

### Field Rations

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:field_rations` |
| **Type** | Food |
| **Hunger restored** | 4 (2 drumsticks) |
| **Saturation** | 1.2 |
| **Effect on eat** | None |
| **Use** | Taming material for Guard Dog (3 required) |
| **Crafting** | Wheat + Cooked Beef + Bowl → 2x Field Rations |
| **Bedrock** | ✅ Via `minecraft:food` component |

---

### Signal Flare

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:signal_flare` |
| **Type** | Throwable |
| **Variants** | Red (Gas Attack event), Green (Supply Drop event), Gray (Smoke Screen) |
| **Crafting** | Gunpowder + Dye (Red/Green/Gray) + Iron Ingot → 2x Signal Flare |
| **Use mechanic** | Right-click to throw; on landing, triggers corresponding world event at that location and fires colored light beam visible to all players |
| **Stack size** | 16 |
| **Server broadcast** | All players receive chat message: `[EVENT] Signal flare spotted at X, Z — <event name>!` |
| **Bedrock** | ✅ Via Script API world events |

---

### Rifle Cartridge

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:rifle_cartridge` |
| **Type** | Ammo (non-usable directly) |
| **Stack size** | 64 |
| **Crafting** | Iron Ingot + Gunpowder → 8x Rifle Cartridge |
| **Bedrock** | ✅ Used as `minecraft:ammo` type |

---

### Gas Canister

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:gas_canister` |
| **Type** | Throwable (player-usable version of Gas Grenadier projectile) |
| **Effect** | Places 3x3 Gas Cloud on impact |
| **Crafting** | Glass Bottle + Gunpowder + Yellow Dye → Gas Canister |
| **Stack size** | 8 |
| **Bedrock** | ✅ Via Script API block placement on entity hit event |

---

### Mud Ball

| Property | Value |
|---|---|
| **Item ID** | `mindcraftmod:mud_ball` |
| **Type** | Throwable (like Snowball) |
| **Effect** | On hit: Slowness I for 3 seconds. No damage. |
| **Source** | Dropped by Mud Pit block when broken |
| **Stack size** | 16 |

---

## Creative Tab

**Tab ID:** `mindcraftmod:war_supplies`
**Icon:** Bolt-Action Rifle
**Groups within tab:**
1. Blocks (all mod blocks + block items)
2. Weapons (Rifle, Bayonet, Grenade, Gas Canister, Mud Ball)
3. Armor (Gas Mask, Trench Coat, Horse Armor Plate)
4. Utility (Field Rations, Signal Flare, Rifle Cartridge)
5. Spawn Eggs (all mob eggs)

---

## Bedrock Coverage Summary

| Java Item | Bedrock | Notes |
|---|---|---|
| Bolt-Action Rifle | ✅ | Simplified reload via Script |
| Trench Bayonet | ✅ | |
| Grenade | ✅ | |
| Gas Mask | ✅ | Script API effect block |
| Trench Coat | ✅ | |
| Horse Armor Plate | ✅ | |
| Field Rations | ✅ | |
| Signal Flare (all 3) | ✅ | |
| Rifle Cartridge | ✅ | |
| Gas Canister | ✅ | |
| Mud Ball | ✅ | |
