#!/usr/bin/env python3
"""
Mindcraft Mod — placeholder texture generator.

Creates flat-colour 16×16 RGBA PNG textures for all mod blocks and items.
Colours use a WW1 earth-tone palette as stand-ins for final hand-drawn pixel art.

Usage (from java-edition/):
    pip install Pillow
    python gen_textures.py

Output:
    src/main/resources/assets/mindcraftmod/textures/block/*.png  (13 files)
    src/main/resources/assets/mindcraftmod/textures/item/*.png   (14 files)
"""

import os
from PIL import Image

BLOCK_DIR = "src/main/resources/assets/mindcraftmod/textures/block"
ITEM_DIR  = "src/main/resources/assets/mindcraftmod/textures/item"

os.makedirs(BLOCK_DIR, exist_ok=True)
os.makedirs(ITEM_DIR,  exist_ok=True)


def solid(path: str, rgb: tuple, alpha: int = 255) -> None:
    """Write a flat 16×16 RGBA tile to disk."""
    img = Image.new("RGBA", (16, 16), (*rgb, alpha))
    img.save(path)
    print(f"  {path}")


# ── Block textures ────────────────────────────────────────────────────────────

print("Generating block textures...")

solid(f"{BLOCK_DIR}/sandbag.png",             (196, 164, 107))        # weathered tan burlap
solid(f"{BLOCK_DIR}/trench_wall.png",         ( 92,  61,  30))        # dark timber / packed dirt
solid(f"{BLOCK_DIR}/barbed_wire.png",         (120, 110, 100), 210)   # dull steel, semi-transparent
solid(f"{BLOCK_DIR}/gas_cloud.png",           (140, 200, 120), 100)   # sickly yellow-green cloud
solid(f"{BLOCK_DIR}/smoke_screen.png",        (180, 180, 180), 140)   # grey billowing smoke
solid(f"{BLOCK_DIR}/mud_pit.png",             ( 74,  55,  40))        # churned dark mud
solid(f"{BLOCK_DIR}/artillery_platform.png",  (110, 110, 118))        # poured stone-grey concrete
solid(f"{BLOCK_DIR}/field_telephone.png",     ( 55,  38,  20))        # black-brown bakelite
solid(f"{BLOCK_DIR}/supply_crate.png",        (139, 105,  20))        # aged stencilled timber
solid(f"{BLOCK_DIR}/shell_crater.png",        ( 60,  55,  48))        # scorched earth / blast debris
solid(f"{BLOCK_DIR}/flag_block.png",          (200,  60,  60))        # neutral red placeholder
solid(f"{BLOCK_DIR}/barbed_wire_post.png",    ( 90,  80,  70))        # weathered iron post
solid(f"{BLOCK_DIR}/rusted_iron_bars.png",    (140,  70,  30))        # heavy rust orange-brown

# ── Item textures ─────────────────────────────────────────────────────────────

print("Generating item textures...")

solid(f"{ITEM_DIR}/bolt_action_rifle.png",    ( 80,  72,  62))        # gunmetal + wood stock
solid(f"{ITEM_DIR}/trench_bayonet.png",       (160, 160, 168))        # polished steel blade
solid(f"{ITEM_DIR}/grenade.png",              ( 68,  88,  58))        # olive drab pineapple body
solid(f"{ITEM_DIR}/gas_mask.png",             (108, 120,  96))        # khaki-green vulcanised rubber
solid(f"{ITEM_DIR}/trench_coat.png",          (139, 128,  64))        # British khaki serge
solid(f"{ITEM_DIR}/horse_armor_plate.png",    (150, 152, 162))        # grey wrought iron plate
solid(f"{ITEM_DIR}/field_rations.png",        (180, 145,  80))        # biscuit / hardtack beige
solid(f"{ITEM_DIR}/signal_flare_red.png",     (220,  55,  55))        # distress red
solid(f"{ITEM_DIR}/signal_flare_green.png",   ( 55, 200,  80))        # advance green
solid(f"{ITEM_DIR}/signal_flare_gray.png",    (160, 160, 160))        # smoke / neutral grey
solid(f"{ITEM_DIR}/rifle_cartridge.png",      (200, 175,  45))        # brass case yellow
solid(f"{ITEM_DIR}/gas_canister.png",         ( 96, 118,  78))        # army olive drab cylinder
solid(f"{ITEM_DIR}/mud_ball.png",             (100,  80,  58))        # clumped dark mud
solid(f"{ITEM_DIR}/leather_scrap.png",        (165, 112,  68))        # undyed tan leather

print(f"\nDone — 27 placeholder textures written.")
print("These flat-colour tiles are stand-ins. Replace with final WW1 pixel art before release.")
