#!/usr/bin/env python3
"""Generate WW1 pixel art textures for the Mindcraft mod (16x16 RGBA)."""

from PIL import Image
import os

# ---------------------------------------------------------------------------
# Palette  (all fully opaque unless noted)
# ---------------------------------------------------------------------------
T       = (0,   0,   0,   0)    # transparent
SHADOW  = (26,  26,  26,  255)
KHAKI   = (139, 125, 82,  255)
KHAKI_L = (169, 155, 112, 255)
OLIVE   = (107, 107, 58,  255)
OLIVE_L = (137, 137, 88,  255)
OLIVE_D = (77,  77,  38,  255)
MUD     = (92,  74,  42,  255)
MUD_L   = (122, 104, 72,  255)
MUD_D   = (62,  44,  22,  255)
STEEL   = (122, 122, 122, 255)
STEEL_L = (162, 162, 162, 255)
IRON    = (61,  61,  61,  255)
RUST    = (139, 69,  19,  255)
CANVAS  = (200, 184, 138, 255)
LEATH   = (123, 94,  58,  255)
LEATH_L = (153, 124, 88,  255)
RED     = (192, 57,  43,  255)
RED_L   = (231, 76,  60,  255)
GREEN   = (39,  174, 96,  255)
GREEN_L = (46,  204, 113, 255)
BONE    = (232, 222, 200, 255)
GAS     = (180, 200, 20,  140)   # translucent
SMOKE_C = (180, 180, 180, 160)   # translucent
BRASS   = (184, 134, 11,  255)
BRASS_L = (214, 164, 41,  255)
BLOOD   = (139, 0,   0,   255)
STONE   = (130, 120, 110, 255)

# ---------------------------------------------------------------------------
# Primitives
# ---------------------------------------------------------------------------
def new16():
    return Image.new('RGBA', (16, 16), T)

def px(img, x, y, c):
    if 0 <= x < img.width and 0 <= y < img.height:
        img.putpixel((x, y), c)

def hline(img, y, x0, x1, c):
    for x in range(x0, x1+1):
        px(img, x, y, c)

def vline(img, x, y0, y1, c):
    for y in range(y0, y1+1):
        px(img, x, y, c)

def rect(img, x0, y0, x1, y1, c):
    for y in range(y0, y1+1):
        for x in range(x0, x1+1):
            px(img, x, y, c)

def orect(img, x0, y0, x1, y1, c):
    hline(img, y0, x0, x1, c); hline(img, y1, x0, x1, c)
    vline(img, x0, y0, y1, c); vline(img, x1, y0, y1, c)

# ---------------------------------------------------------------------------
# ITEMS
# ---------------------------------------------------------------------------

def bolt_action_rifle():
    img = new16()
    # wood stock (right)
    rect(img, 9, 7, 15, 9, KHAKI)
    rect(img, 12, 9, 15, 11, KHAKI)
    # highlight
    hline(img, 7, 9, 15, KHAKI_L)
    # receiver (centre)
    rect(img, 6, 6, 9, 9, IRON)
    # barrel (left, 2px tall)
    rect(img, 0, 7, 6, 8, STEEL)
    hline(img, 7, 0, 6, STEEL_L)
    # bolt handle nub
    px(img, 10, 5, STEEL); px(img, 11, 5, STEEL); px(img, 11, 6, STEEL)
    # trigger guard
    px(img, 8, 10, SHADOW); px(img, 9, 11, SHADOW)
    px(img, 10, 11, SHADOW); px(img, 11, 10, SHADOW)
    # outline
    hline(img, 6, 0, 9, SHADOW)
    hline(img, 9, 0, 9, SHADOW)
    px(img, 0, 7, SHADOW); px(img, 0, 8, SHADOW)
    vline(img, 15, 6, 11, SHADOW)
    hline(img, 6, 6, 15, SHADOW)
    hline(img, 12, 12, 15, SHADOW)
    return img

def trench_bayonet():
    img = new16()
    blade  = [(3,12),(4,11),(5,10),(6,9),(7,8),(8,7),(9,6),(10,5),(11,4),(12,3)]
    edge   = [(4,12),(5,11),(6,10),(7,9),(8,8),(9,7),(10,6),(11,5),(12,4),(13,3)]
    for x,y in blade: px(img, x, y, STEEL)
    for x,y in edge:  px(img, x, y, STEEL_L)
    # tip
    px(img, 13, 2, BLOOD); px(img, 14, 1, BLOOD)
    # guard
    for dy in range(3):
        px(img, 3, 12-dy, IRON); px(img, 4, 12-dy, IRON)
    # handle
    for bx, by in [(1,14),(2,15),(2,14),(1,15),(3,14)]:
        px(img, bx, by, LEATH)
    # wrap lines on handle
    px(img, 2, 13, MUD_D); px(img, 3, 13, MUD_D)
    return img

def rifle_cartridge():
    img = new16()
    # tip (steel, narrow)
    vline(img, 7, 1, 3, STEEL); vline(img, 8, 1, 3, STEEL)
    px(img, 6, 2, STEEL_L); px(img, 9, 2, STEEL_L)
    # neck
    rect(img, 6, 3, 9, 5, BRASS_L)
    # body
    rect(img, 5, 5, 10, 12, BRASS)
    vline(img, 5, 5, 12, BRASS_L)   # highlight
    vline(img, 10, 5, 12, IRON)     # shadow
    # rim
    rect(img, 4, 12, 11, 13, BRASS)
    hline(img, 13, 4, 11, IRON)
    # outlines
    orect(img, 4, 0, 11, 14, SHADOW)
    hline(img, 4, 5, 10, SHADOW)    # neck→body seam
    return img

def grenade():
    img = new16()
    # stick handle
    rect(img, 6, 9, 9, 15, CANVAS)
    vline(img, 5, 9, 15, SHADOW); vline(img, 10, 9, 15, SHADOW)
    hline(img, 15, 5, 10, SHADOW)
    # head
    rect(img, 3, 1, 12, 9, OLIVE)
    # segments
    hline(img, 4, 3, 12, OLIVE_D)
    hline(img, 7, 3, 12, OLIVE_D)
    vline(img, 7, 1, 9, OLIVE_D)
    # highlight top
    hline(img, 2, 4, 11, OLIVE_L)
    vline(img, 4, 1, 9, OLIVE_L)
    # pin loop
    px(img, 7, 0, STEEL); px(img, 8, 0, STEEL)
    # outline head
    orect(img, 3, 0, 12, 9, SHADOW)
    return img

def gas_canister():
    img = new16()
    # body
    rect(img, 4, 4, 11, 14, OLIVE)
    vline(img, 4, 4, 14, OLIVE_L); vline(img, 5, 4, 14, OLIVE_L)
    vline(img, 10, 4, 14, OLIVE_D); vline(img, 11, 4, 14, OLIVE_D)
    # gas tint ring at top of body
    hline(img, 4, 4, 11, (180, 200, 20, 255))
    # top cap
    rect(img, 3, 3, 12, 4, STEEL)
    # nozzle
    rect(img, 6, 1, 9, 3, STEEL)
    px(img, 7, 0, STEEL); px(img, 8, 0, STEEL)
    # bottom base
    rect(img, 3, 14, 12, 15, IRON)
    # skull mark (centre)
    px(img, 7, 7, SHADOW); px(img, 8, 7, SHADOW)
    px(img, 6, 8, SHADOW); px(img, 9, 8, SHADOW)
    px(img, 6, 9, SHADOW); px(img, 7, 9, SHADOW)
    px(img, 8, 9, SHADOW); px(img, 9, 9, SHADOW)
    px(img, 7, 10, SHADOW); px(img, 8, 10, SHADOW)
    # outline
    orect(img, 3, 0, 12, 15, SHADOW)
    return img

def mud_ball():
    img = new16()
    # circle via row spans
    spans = [
        (3,12, 4), (2,13, 3), (2,13, 2), (2,13, 2),
        (2,13, 2), (2,13, 2), (2,13, 2), (2,13, 2),
        (2,13, 3), (3,12, 4),
    ]
    for i, (x0, x1, y_off) in enumerate(spans):
        hline(img, i+3, x0, x1, MUD)
    # proper circular fill
    cx, cy, r = 7.5, 7.5, 5.5
    for y in range(16):
        for x in range(16):
            if (x-cx)**2 + (y-cy)**2 < r*r:
                px(img, x, y, MUD)
    # highlight
    for x,y in [(4,4),(5,4),(4,5),(5,5),(4,6)]:
        px(img, x, y, MUD_L)
    # shadow
    for x,y in [(10,10),(11,10),(10,11),(11,11),(10,9)]:
        px(img, x, y, MUD_D)
    return img

def _flare(flame_c, flame_l):
    img = new16()
    # tube body
    rect(img, 5, 5, 10, 13, KHAKI)
    vline(img, 5, 5, 13, KHAKI_L)
    vline(img, 10, 5, 13, IRON)
    # metal bands
    hline(img, 7, 5, 10, IRON)
    hline(img, 11, 5, 10, IRON)
    # bottom cap
    rect(img, 4, 13, 11, 15, STEEL)
    # flame / smoke
    rect(img, 6, 1, 9, 5, flame_c)
    px(img, 7, 0, flame_l); px(img, 8, 0, flame_l)
    px(img, 5, 2, flame_l); px(img, 10, 2, flame_l)
    # outline tube
    orect(img, 4, 4, 11, 15, SHADOW)
    return img

def signal_flare_red():   return _flare(RED,   RED_L)
def signal_flare_green(): return _flare(GREEN, GREEN_L)
def signal_flare_gray():  return _flare(BONE,  (255,255,255,255))

def gas_mask():
    img = new16()
    # shell
    rect(img, 3, 2, 12, 13, OLIVE)
    vline(img, 3, 2, 13, OLIVE_L)
    hline(img, 2, 3, 12, OLIVE_L)
    # left lens
    rect(img, 3, 5, 6, 8, IRON)
    rect(img, 4, 6, 5, 7, STEEL_L)
    # right lens
    rect(img, 9, 5, 12, 8, IRON)
    rect(img, 10, 6, 11, 7, STEEL_L)
    # filter / snout
    rect(img, 6, 10, 9, 13, IRON)
    hline(img, 13, 6, 9, STEEL)
    # strap hooks
    for y in (5, 8):
        px(img, 2, y, STEEL); px(img, 13, y, STEEL)
    # outline
    orect(img, 2, 1, 13, 14, SHADOW)
    return img

def trench_coat():
    img = new16()
    # body
    rect(img, 2, 2, 13, 15, KHAKI)
    # shoulder highlight
    hline(img, 2, 2, 13, KHAKI_L)
    hline(img, 3, 2, 13, KHAKI_L)
    # lapels
    rect(img, 5, 2, 10, 7, CANVAS)
    px(img, 7, 3, SHADOW); px(img, 8, 3, SHADOW)  # collar notch
    # belt
    hline(img, 10, 2, 13, IRON)
    # buttons
    for by in (8, 10, 12, 14):
        px(img, 7, by, SHADOW); px(img, 8, by, SHADOW)
    # outline
    orect(img, 1, 1, 14, 15, SHADOW)
    return img

def horse_armor_plate():
    img = new16()
    rect(img, 2, 2, 13, 13, STEEL)
    # ridge highlight
    vline(img, 7, 2, 13, STEEL_L); vline(img, 8, 2, 13, STEEL_L)
    # shadow edge
    vline(img, 12, 2, 13, IRON); vline(img, 13, 2, 13, IRON)
    # rivets
    for rx, ry in ((3,4),(3,11),(12,4),(12,11)):
        px(img, rx, ry, IRON)
    # strap slots
    px(img, 5, 1, SHADOW); px(img, 10, 1, SHADOW)
    orect(img, 1, 1, 14, 14, SHADOW)
    return img

def field_rations():
    img = new16()
    # can body
    rect(img, 3, 4, 12, 13, CANVAS)
    vline(img, 3, 4, 13, (220,204,158,255))   # highlight
    vline(img, 12, 4, 13, LEATH)              # shadow
    # rims
    rect(img, 2, 3, 13, 4, STEEL)
    rect(img, 2, 13, 13, 14, STEEL)
    # key tab
    rect(img, 8, 1, 11, 3, STEEL)
    px(img, 12, 2, STEEL)
    # label line
    hline(img, 8, 3, 12, KHAKI)
    # outline
    orect(img, 2, 1, 13, 15, SHADOW)
    return img

def leather_scrap():
    img = new16()
    # Irregular torn shape - defined pixel by pixel
    shape = [
        (3,2),(4,2),(5,2),(6,2),(7,2),(8,2),
        (3,3),(4,3),(5,3),(6,3),(7,3),(8,3),(9,3),(10,3),
        (2,4),(3,4),(4,4),(5,4),(6,4),(7,4),(8,4),(9,4),(10,4),(11,4),
        (2,5),(3,5),(4,5),(5,5),(6,5),(7,5),(8,5),(9,5),(10,5),(11,5),(12,5),
        (3,6),(4,6),(5,6),(6,6),(7,6),(8,6),(9,6),(10,6),(11,6),(12,6),
        (4,7),(5,7),(6,7),(7,7),(8,7),(9,7),(10,7),(11,7),(12,7),(13,7),
        (4,8),(5,8),(6,8),(7,8),(8,8),(9,8),(10,8),(11,8),(12,8),
        (5,9),(6,9),(7,9),(8,9),(9,9),(10,9),(11,9),(12,9),
        (5,10),(6,10),(7,10),(8,10),(9,10),(10,10),(11,10),
        (6,11),(7,11),(8,11),(9,11),(10,11),
        (7,12),(8,12),(9,12),(10,12),
        (8,13),(9,13),
    ]
    highlight = {(3,2),(4,2),(3,3),(4,3),(3,4),(2,4),(2,5),(3,5)}
    dark      = {(11,5),(12,5),(12,6),(11,6),(13,7),(12,7),(12,8),(11,9),(12,9)}
    for x, y in shape:
        c = LEATH_L if (x,y) in highlight else (MUD if (x,y) in dark else LEATH)
        px(img, x, y, c)
    return img

# ---------------------------------------------------------------------------
# BLOCKS
# ---------------------------------------------------------------------------

def sandbag():
    img = new16()
    rect(img, 0, 0, 15, 15, CANVAS)
    # hessian weave
    for y in range(16):
        for x in range(16):
            if (x % 4 == 0 and y % 2 == 0) or (x % 4 == 2 and y % 2 == 1):
                px(img, x, y, KHAKI)
            elif (x % 4 == 1 and y % 2 == 1) or (x % 4 == 3 and y % 2 == 0):
                px(img, x, y, MUD_L)
    # horizontal seam
    hline(img, 7, 0, 15, LEATH)
    hline(img, 8, 0, 15, LEATH)
    return img

def trench_wall():
    img = new16()
    rect(img, 0, 0, 15, 15, MUD)
    # timber planks
    for y0, y1 in ((0,2),(6,8),(12,14)):
        rect(img, 0, y0, 15, y1, KHAKI)
        hline(img, y0, 0, 15, KHAKI_L)
        # grain lines
        for x in range(0, 16, 5):
            vline(img, x, y0, y1, KHAKI_L if x % 10 == 0 else MUD_L)
    # nail dots
    for nx in (1, 14):
        for ny in (1, 7, 13):
            px(img, nx, ny, SHADOW)
    return img

def barbed_wire():
    img = Image.new('RGBA', (16, 16), T)
    # horizontal wire
    hline(img, 7, 0, 15, RUST)
    hline(img, 8, 0, 15, RUST)
    # barbs
    for bx in (2, 6, 10, 14):
        for dy in (-2,-1, 1, 2):
            px(img, bx, 7+dy, RUST)
        # diagonal spurs
        px(img, bx-1, 6, RUST); px(img, bx+1, 6, RUST)
        px(img, bx-1, 9, RUST); px(img, bx+1, 9, RUST)
    return img

def gas_cloud():
    img = Image.new('RGBA', (16, 16), T)
    GC  = (180, 200, 20, 140)
    GCL = (210, 230, 60, 100)
    cx, cy, r = 7.5, 7.5, 5.5
    for y in range(16):
        for x in range(16):
            if (x-cx)**2 + (y-cy)**2 < r*r:
                px(img, x, y, GC)
    for x,y in [(5,5),(6,5),(7,5),(5,6),(6,6),(7,7),(8,6),(8,7)]:
        px(img, x, y, GCL)
    return img

def smoke_screen():
    img = Image.new('RGBA', (16, 16), T)
    SC  = (180, 180, 180, 160)
    SCL = (220, 220, 220, 100)
    cx, cy, r = 7.5, 7.5, 5.5
    for y in range(16):
        for x in range(16):
            if (x-cx)**2 + (y-cy)**2 < r*r:
                px(img, x, y, SC)
    for x,y in [(5,5),(6,5),(6,6),(7,6),(8,7),(7,5)]:
        px(img, x, y, SCL)
    return img

def mud_pit():
    img = new16()
    rect(img, 0, 0, 15, 15, MUD_D)
    # lighter mud at top
    hline(img, 0, 0, 15, MUD); hline(img, 1, 0, 15, MUD)
    # crack lines
    for x,y in [(3,3),(4,3),(5,3),(5,4),(5,5),(6,5),(6,6)]:
        px(img, x, y, MUD)
    for x,y in [(11,4),(10,4),(10,5),(10,6),(11,6),(11,7),(10,8)]:
        px(img, x, y, MUD)
    # puddle patch (very dark)
    rect(img, 5, 8, 10, 12, (45, 32, 14, 255))
    # rim highlight
    for x,y in [(5,7),(6,7),(7,7),(5,8),(4,9)]:
        px(img, x, y, MUD_L)
    return img

def artillery_platform():
    img = new16()
    rect(img, 0, 0, 15, 15, STONE)
    rect(img, 2, 2, 13, 13, STEEL)
    hline(img, 7, 2, 13, IRON)
    vline(img, 7, 2, 13, IRON)
    hline(img, 8, 2, 13, IRON)
    vline(img, 8, 2, 13, IRON)
    # bolts
    for bx, by in ((2,2),(13,2),(2,13),(13,13)):
        px(img, bx, by, SHADOW)
    orect(img, 0, 0, 15, 15, SHADOW)
    return img

def field_telephone():
    img = new16()
    rect(img, 2, 3, 13, 13, OLIVE)
    rect(img, 3, 4, 12, 12, OLIVE_L)
    # handset body (two ear pads + grip)
    rect(img, 4, 5, 6, 7, IRON)    # earpiece
    rect(img, 9, 9, 11, 11, IRON)  # mouthpiece
    vline(img, 7, 7, 9, IRON)      # handle
    hline(img, 7, 7, 9, IRON)
    hline(img, 9, 7, 9, IRON)
    # dial suggestion
    rect(img, 7, 6, 9, 8, SHADOW)
    px(img, 8, 7, STEEL)
    orect(img, 1, 2, 14, 14, SHADOW)
    return img

def supply_crate():
    img = new16()
    rect(img, 0, 0, 15, 15, KHAKI)
    # plank lines
    for y in (4, 5, 10, 11):
        hline(img, y, 0, 15, MUD_D)
    vline(img, 5, 0, 15, MUD)
    vline(img, 10, 0, 15, MUD)
    # iron bands top/bottom
    for y in (0, 1, 14, 15):
        hline(img, y, 0, 15, IRON)
    # highlight top plank
    hline(img, 2, 0, 15, KHAKI_L)
    hline(img, 7, 0, 15, KHAKI_L)
    return img

def shell_crater():
    img = new16()
    rect(img, 0, 0, 15, 15, MUD)
    # crater bowl
    rect(img, 4, 4, 11, 11, MUD_D)
    rect(img, 6, 6, 9, 9, (40, 28, 12, 255))
    # radiating cracks
    for i in range(1, 4):
        px(img, 4-i, 4-i, MUD_D); px(img, 11+i, 4-i, MUD_D)
        px(img, 4-i, 11+i, MUD_D); px(img, 11+i, 11+i, MUD_D)
        px(img, 7, 4-i, MUD_D); px(img, 8, 4-i, MUD_D)
        px(img, 7, 11+i, MUD_D); px(img, 8, 11+i, MUD_D)
    # rim highlight
    for x,y in ((4,3),(5,3),(3,4),(3,5),(4,4)):
        px(img, x, y, MUD_L)
    return img

def flag_block():
    img = new16()
    # pole
    vline(img, 2, 0, 15, IRON)
    vline(img, 3, 0, 15, STEEL)
    px(img, 3, 0, STEEL_L)
    # flag panel (khaki with two stripes)
    rect(img, 4, 1, 15, 9, KHAKI)
    hline(img, 3, 4, 15, CANVAS)
    hline(img, 6, 4, 15, MUD)
    # flag shading
    vline(img, 4, 1, 9, KHAKI_L)
    hline(img, 1, 4, 15, KHAKI_L)
    # outline
    vline(img, 1, 0, 15, SHADOW)
    orect(img, 4, 0, 15, 10, SHADOW)
    return img

def barbed_wire_post():
    img = Image.new('RGBA', (16, 16), T)
    # post
    vline(img, 7, 2, 15, RUST)
    vline(img, 8, 2, 15, RUST)
    vline(img, 6, 2, 15, SHADOW)
    vline(img, 9, 2, 15, SHADOW)
    # crossbar
    hline(img, 3, 3, 12, RUST)
    hline(img, 4, 3, 12, RUST)
    hline(img, 2, 3, 12, SHADOW)
    hline(img, 5, 3, 12, SHADOW)
    # wire hook dots
    px(img, 3, 2, STEEL); px(img, 12, 2, STEEL)
    # ground plate
    rect(img, 5, 13, 10, 15, MUD_D)
    return img

def rusted_iron_bars():
    img = Image.new('RGBA', (16, 16), T)
    # vertical bars
    for bx in (0, 4, 8, 12):
        vline(img, bx,   0, 15, IRON)
        vline(img, bx+1, 0, 15, RUST)
    # horizontal connectors
    hline(img, 3,  0, 15, IRON)
    hline(img, 4,  0, 15, RUST)
    hline(img, 11, 0, 15, IRON)
    hline(img, 12, 0, 15, RUST)
    # rust splotches
    for x,y in ((2,6),(6,9),(10,5),(3,13),(9,11),(14,8)):
        px(img, x, y, RUST); px(img, x, y+1, RUST)
    return img

# ---------------------------------------------------------------------------
# Save
# ---------------------------------------------------------------------------
BASE   = '/home/user/mindcraft-mod/java-edition/src/main/resources/assets/mindcraftmod'
ITEMS  = BASE + '/textures/item'
BLOCKS = BASE + '/textures/block'

def save(img, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path)
    print(f'  {os.path.relpath(path, BASE)}')

if __name__ == '__main__':
    print('=== Items ===')
    save(bolt_action_rifle(),   f'{ITEMS}/bolt_action_rifle.png')
    save(trench_bayonet(),      f'{ITEMS}/trench_bayonet.png')
    save(rifle_cartridge(),     f'{ITEMS}/rifle_cartridge.png')
    save(grenade(),             f'{ITEMS}/grenade.png')
    save(gas_canister(),        f'{ITEMS}/gas_canister.png')
    save(mud_ball(),            f'{ITEMS}/mud_ball.png')
    save(signal_flare_red(),    f'{ITEMS}/signal_flare_red.png')
    save(signal_flare_green(),  f'{ITEMS}/signal_flare_green.png')
    save(signal_flare_gray(),   f'{ITEMS}/signal_flare_gray.png')
    save(gas_mask(),            f'{ITEMS}/gas_mask.png')
    save(trench_coat(),         f'{ITEMS}/trench_coat.png')
    save(horse_armor_plate(),   f'{ITEMS}/horse_armor_plate.png')
    save(field_rations(),       f'{ITEMS}/field_rations.png')
    save(leather_scrap(),       f'{ITEMS}/leather_scrap.png')

    print('=== Blocks ===')
    save(sandbag(),             f'{BLOCKS}/sandbag.png')
    save(trench_wall(),         f'{BLOCKS}/trench_wall.png')
    save(barbed_wire(),         f'{BLOCKS}/barbed_wire.png')
    save(gas_cloud(),           f'{BLOCKS}/gas_cloud.png')
    save(smoke_screen(),        f'{BLOCKS}/smoke_screen.png')
    save(mud_pit(),             f'{BLOCKS}/mud_pit.png')
    save(artillery_platform(),  f'{BLOCKS}/artillery_platform.png')
    save(field_telephone(),     f'{BLOCKS}/field_telephone.png')
    save(supply_crate(),        f'{BLOCKS}/supply_crate.png')
    save(shell_crater(),        f'{BLOCKS}/shell_crater.png')
    save(flag_block(),          f'{BLOCKS}/flag_block.png')
    save(barbed_wire_post(),    f'{BLOCKS}/barbed_wire_post.png')
    save(rusted_iron_bars(),    f'{BLOCKS}/rusted_iron_bars.png')

    print('Done.')
