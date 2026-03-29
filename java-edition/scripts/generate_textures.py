#!/usr/bin/env python3
"""
Generate 16x16 pixel-art block textures for Mindcraft Mod WW1.
Run: python3 java-edition/scripts/generate_textures.py
Outputs to: src/main/resources/assets/mindcraftmod/textures/block/
"""
from PIL import Image
import pathlib, math

OUT = pathlib.Path("C:/Users/phil/code/mindcraft-mod/java-edition/src/main/resources/assets/mindcraftmod/textures/block")

def h(s):
    s = s.lstrip('#')
    return (int(s[0:2], 16), int(s[2:4], 16), int(s[4:6], 16), 255)

def make(pixels):
    img = Image.new("RGBA", (16, 16))
    img.putdata(pixels)
    return img

def save(img, name):
    img.save(OUT / name)
    print(f"  wrote {name}")

def dist(x, y, cx=7.5, cy=7.5):
    return math.sqrt((x - cx) ** 2 + (y - cy) ** 2)


# ── sandbag ───────────────────────────────────────────────────────────────────
def sandbag():
    base = h('#C8A87A')
    dark = h('#7A5230')
    med  = h('#A88050')
    pxs = []
    for y in range(16):
        for x in range(16):
            # Horizontal stitch lines every 4px
            if y % 4 == 3:
                pxs.append(dark)
            # Vertical bag seams every 8px
            elif x in (7, 8):
                pxs.append(dark)
            # Slight shading variation per bag row
            elif (y // 4) % 2 == 1:
                pxs.append(med)
            else:
                pxs.append(base)
    save(make(pxs), "sandbag.png")


# ── gas_cloud ─────────────────────────────────────────────────────────────────
def gas_cloud():
    pxs = []
    for y in range(16):
        for x in range(16):
            d = dist(x, y)
            if d < 2.5:
                pxs.append(h('#C8F070'))   # bright centre
            elif d < 4.5:
                pxs.append(h('#B8E67A'))   # lighter inner
            elif d < 6.5:
                pxs.append(h('#8BC34A'))   # mid green
            elif d < 7.5:
                pxs.append(h('#6BA020'))   # darker ring
            else:
                pxs.append(h('#5A8C1A'))   # dark vignette edge
    save(make(pxs), "gas_cloud.png")


# ── smoke_screen ──────────────────────────────────────────────────────────────
def smoke_screen():
    pxs = []
    for y in range(16):
        for x in range(16):
            if x == 0 or x == 15 or y == 0 or y == 15:
                pxs.append(h('#606060'))   # dark border
            elif (x + y) % 5 == 0:
                pxs.append(h('#D8D8D8'))   # bright wisp highlight
            elif (x + y) % 4 < 2:
                pxs.append(h('#C0C0C0'))   # lighter wisp
            else:
                pxs.append(h('#A0A0A0'))   # mid gray
    save(make(pxs), "smoke_screen.png")


# ── mud_pit ───────────────────────────────────────────────────────────────────
def mud_pit():
    pxs = []
    crack1 = {(x, y) for x in range(16) for y in range(16) if (x + y * 2) % 8 == 0}
    crack2 = {(x, y) for x in range(16) for y in range(16) if (x * 2 + y) % 7 == 0}
    for y in range(16):
        for x in range(16):
            d = dist(x, y)
            if d < 3:
                pxs.append(h('#2A1A10'))   # glossy dark centre
            elif (x, y) in crack1 or (x, y) in crack2:
                pxs.append(h('#6B4830'))   # mud crack highlight
            else:
                pxs.append(h('#4A3020'))   # base dark mud
    save(make(pxs), "mud_pit.png")


# ── supply_crate ──────────────────────────────────────────────────────────────
def supply_crate():
    pxs = []
    for y in range(16):
        for x in range(16):
            corner = (x < 4 and y < 4) or (x < 4 and y > 11) or \
                     (x > 11 and y < 4) or (x > 11 and y > 11)
            plank = y in (3, 7, 11)
            cross_h = y in (7, 8) and 4 <= x <= 11
            cross_v = x in (7, 8) and 4 <= y <= 11
            if corner:
                pxs.append(h('#555555'))
            elif plank:
                pxs.append(h('#8B6010'))
            elif cross_h or cross_v:
                pxs.append(h('#333333'))
            else:
                pxs.append(h('#C49A3C'))
    save(make(pxs), "supply_crate.png")


# ── shell_crater ──────────────────────────────────────────────────────────────
def shell_crater():
    rubble = {(5, 4), (10, 4), (3, 7), (12, 7), (4, 11), (11, 11), (6, 3), (9, 3),
              (2, 9), (13, 6), (7, 2), (8, 13)}
    pxs = []
    for y in range(16):
        for x in range(16):
            d = dist(x, y)
            if (x, y) in rubble:
                pxs.append(h('#909090'))   # gray rubble at rim
            elif d < 2.5:
                pxs.append(h('#2A1808'))   # deepest dark centre
            elif d < 4.0:
                pxs.append(h('#3A2010'))   # dark inner wall
            elif d < 5.5:
                pxs.append(h('#4A3020'))   # mid crater wall
            elif d < 6.5:
                pxs.append(h('#5A3C20'))   # outer rim
            else:
                pxs.append(h('#6B5020'))   # surrounding dirt
    save(make(pxs), "shell_crater.png")


# ── trench_wall ───────────────────────────────────────────────────────────────
def trench_wall():
    pxs = []
    for y in range(16):
        for x in range(16):
            if y in (0, 1, 14, 15):
                pxs.append(h('#3A2010'))          # horizontal nail rail
            elif (x // 4) % 2 == 0:
                pxs.append(h('#7A5230'))          # lighter plank
            else:
                pxs.append(h('#5A3A1A'))          # darker plank
    save(make(pxs), "trench_wall.png")


# ── artillery_platform ────────────────────────────────────────────────────────
def artillery_platform():
    pxs = []
    for y in range(16):
        for x in range(16):
            mortar = x % 4 == 3 or y % 4 == 3
            crack  = (x * 3 + y * 5) % 11 == 0 and not mortar
            if mortar:
                pxs.append(h('#B0B0B0'))   # lighter mortar joint
            elif crack:
                pxs.append(h('#606060'))   # dark crack
            else:
                pxs.append(h('#909090'))   # stone gray base
    save(make(pxs), "artillery_platform.png")


# ── field_telephone ───────────────────────────────────────────────────────────
def field_telephone():
    pxs = []
    for y in range(16):
        for x in range(16):
            panel = 5 <= x <= 10 and 3 <= y <= 12
            dial1 = dist(x, y, 4.0, 10.0) < 1.8
            dial2 = dist(x, y, 11.0, 10.0) < 1.8
            strap = (x in (3, 12)) and 4 <= y <= 11
            if dial1 or dial2:
                pxs.append(h('#202020'))   # dial circles
            elif strap:
                pxs.append(h('#3A2A10'))   # leather strap edge
            elif panel:
                pxs.append(h('#2A3A1A'))   # recessed panel
            else:
                pxs.append(h('#4A5A2A'))   # olive green body
    save(make(pxs), "field_telephone.png")


# ── barbed_wire ───────────────────────────────────────────────────────────────
def barbed_wire():
    pxs = []
    for y in range(16):
        for x in range(16):
            md = abs(x - y)
            ad = abs(x - (15 - y))
            # Barb spikes: every 4px along diagonal, perpendicular flick
            barb = ((x + y) % 4 == 0 and (md == 2 or ad == 2))
            if barb:
                pxs.append(h('#CCCCCC'))   # bright barb tip
            elif md == 0 or ad == 0:
                pxs.append(h('#AAAAAA'))   # wire centre
            elif md <= 1 or ad <= 1:
                pxs.append(h('#8A8A8A'))   # wire edge
            else:
                pxs.append(h('#5A4030'))   # dark dirt background
    save(make(pxs), "barbed_wire.png")


# ── barbed_wire_post ──────────────────────────────────────────────────────────
def barbed_wire_post():
    pxs = []
    for y in range(16):
        for x in range(16):
            rust  = (x * 7 + y * 11) % 17 == 0
            hi    = x in (4, 5)              # highlight stripe
            edge  = x in (0, 15)             # dark seam
            if rust:
                pxs.append(h('#8B4513'))
            elif edge:
                pxs.append(h('#2A2A2A'))
            elif hi:
                pxs.append(h('#666666'))
            else:
                pxs.append(h('#454545'))
    save(make(pxs), "barbed_wire_post.png")


# ── rusted_iron_bars ──────────────────────────────────────────────────────────
def rusted_iron_bars():
    pxs = []
    for y in range(16):
        for x in range(16):
            bar   = x in (0, 1, 7, 8, 14, 15)
            patch = (x * 3 + y * 5) % 7 < 2 and not bar
            if bar:
                pxs.append(h('#3A2010'))   # dark bar edge
            elif patch:
                pxs.append(h('#AA6030'))   # lighter rust patch
            else:
                pxs.append(h('#7A4020'))   # base rust
    save(make(pxs), "rusted_iron_bars.png")


# ── flag_allied (Union Jack, simplified) ──────────────────────────────────────
def flag_allied():
    pxs = []
    for y in range(16):
        for x in range(16):
            c = h('#00247D')   # blue field

            # White St Andrew's diagonal cross, 2px wide
            if abs(x - y) <= 1 or abs(x - (15 - y)) <= 1:
                c = h('#FFFFFF')
            # Red diagonal on top (1px)
            if x == y or x == (15 - y):
                c = h('#CF142B')

            # White St George's upright cross, 2px wide
            if x in (7, 8) or y in (7, 8):
                c = h('#FFFFFF')
            # Red centre strip of upright cross (1px each axis)
            if x == 7 or y == 7:
                c = h('#CF142B')

            pxs.append(c)
    save(make(pxs), "flag_allied.png")


# ── flag_neutral (white surrender / parley flag) ──────────────────────────────
def flag_neutral():
    pxs = []
    for y in range(16):
        for x in range(16):
            if x == 0 or x == 15 or y == 0 or y == 15:
                pxs.append(h('#888888'))   # outer gray border
            elif x == 1 or x == 14 or y == 1 or y == 14:
                pxs.append(h('#BBBBBB'))   # inner lighter border
            else:
                pxs.append(h('#F0F0F0'))   # white field
    save(make(pxs), "flag_neutral.png")


# ── flag_central (Iron Cross / Eisernes Kreuz) ────────────────────────────────
def flag_central():
    pxs = []
    for y in range(16):
        for x in range(16):
            # Bold Greek cross arms (4px wide, from x/y = 2..13)
            vert  = x in range(6, 10) and y in range(2, 14)
            horiz = y in range(6, 10) and x in range(2, 14)
            # Corner flare: widen arms at the ends by 1px
            tip_n = y == 2 and x in range(5, 11)
            tip_s = y == 13 and x in range(5, 11)
            tip_w = x == 2 and y in range(5, 11)
            tip_e = x == 13 and y in range(5, 11)
            if vert or horiz or tip_n or tip_s or tip_w or tip_e:
                pxs.append(h('#1A1A1A'))   # black iron cross
            else:
                pxs.append(h('#FFFFFF'))   # white field
    save(make(pxs), "flag_central.png")


if __name__ == "__main__":
    print(f"Writing to {OUT}")
    sandbag()
    gas_cloud()
    smoke_screen()
    mud_pit()
    supply_crate()
    shell_crater()
    trench_wall()
    artillery_platform()
    field_telephone()
    barbed_wire()
    barbed_wire_post()
    rusted_iron_bars()
    flag_allied()
    flag_neutral()
    flag_central()
    print("Done — 15 textures written.")
