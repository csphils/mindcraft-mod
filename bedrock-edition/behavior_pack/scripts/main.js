/**
 * Mindcraft Mod — WW1 Warfare
 * Bedrock Edition Script API (v2.0 / @minecraft/server 1.15+)
 *
 * Handles:
 *  - Faction system (scoreboard-based)
 *  - World event scheduling (Artillery Barrage, Gas Attack, Supply Drop)
 *  - Signal Flare event triggering
 *  - Gas Cloud / Smoke Screen effects on player contact
 *  - Guard Dog alert system
 *  - Territory control (Flag Block capture timer)
 */

import * as mc from "@minecraft/server";

const world = mc.world;
const system = mc.system;

// ── Constants ────────────────────────────────────────────────────────────────

const MOD_NS = "mindcraft";

const FACTIONS = {
  ALLIES: "allies",
  CENTRAL: "central_powers",
  NONE: "none",
};

// Scoreboard objectives
const OBJ_FACTION      = "mc_faction";      // 0=none, 1=allies, 2=central
const OBJ_TERRITORY    = "mc_territory";    // track captures
const OBJ_FLARE_COOL   = "mc_flare_cool";  // signal flare cooldown (ticks)

// World event intervals (in ticks — 20 ticks/sec)
const BARRAGE_MIN_TICKS = 20 * 60 * 20;  // 20 minutes
const BARRAGE_MAX_TICKS = 20 * 60 * 40;  // 40 minutes

// ── Initialization ───────────────────────────────────────────────────────────

system.run(() => {
  initScoreboards();
});

function initScoreboards() {
  for (const objId of [OBJ_FACTION, OBJ_TERRITORY, OBJ_FLARE_COOL]) {
    try {
      world.scoreboard.addObjective(objId, "dummy", objId);
    } catch {
      // Already exists — fine
    }
  }
  scheduleNextBarrage();
}

// ── Faction System ────────────────────────────────────────────────────────────

/** Returns "allies", "central_powers", or "none" for a player. */
function getPlayerFaction(player) {
  const obj = world.scoreboard.getObjective(OBJ_FACTION);
  const score = obj?.getScore(player.scoreboardIdentity) ?? 0;
  if (score === 1) return FACTIONS.ALLIES;
  if (score === 2) return FACTIONS.CENTRAL;
  return FACTIONS.NONE;
}

function setPlayerFaction(player, faction) {
  const obj = world.scoreboard.getObjective(OBJ_FACTION);
  const val = faction === FACTIONS.ALLIES ? 1 : faction === FACTIONS.CENTRAL ? 2 : 0;
  obj?.setScore(player.scoreboardIdentity, val);
}

// Listen for chat command "/faction join allies|central"
world.beforeEvents.chatSend.subscribe((event) => {
  const msg = event.message.trim().toLowerCase();
  if (!msg.startsWith("/faction")) return;

  event.cancel = true;
  const player = event.sender;
  const parts = msg.split(" ");

  if (parts[1] === "join") {
    const faction = parts[2];
    if (faction === "allies") {
      setPlayerFaction(player, FACTIONS.ALLIES);
      player.sendMessage("§9You have joined the Allies!");
    } else if (faction === "central" || faction === "central_powers") {
      setPlayerFaction(player, FACTIONS.CENTRAL);
      player.sendMessage("§cYou have joined the Central Powers!");
    } else {
      player.sendMessage("§eUsage: /faction join allies|central");
    }
  } else if (parts[1] === "info") {
    const f = getPlayerFaction(player);
    player.sendMessage(`§eYour faction: §f${f}`);
  }
});

// ── Gas Cloud / Smoke Screen Effects ──────────────────────────────────────────

// Apply effects every second to players standing in gas/smoke blocks
system.runInterval(() => {
  for (const player of world.getAllPlayers()) {
    const block = player.dimension.getBlock(player.location);
    if (!block) continue;
    const id = block.typeId;

    if (id === `${MOD_NS}:gas_cloud`) {
      // Check if wearing gas mask
      const helmet = player.getComponent("minecraft:equippable")?.getEquipment("Head");
      if (helmet?.typeId !== `${MOD_NS}:gas_mask`) {
        player.addEffect("minecraft:poison", 40, { amplifier: 1 });
      }
    }

    if (id === `${MOD_NS}:smoke_screen`) {
      player.addEffect("minecraft:blindness", 60, { amplifier: 0 });
    }
  }
}, 20); // every 20 ticks = 1 second

// ── World Events ──────────────────────────────────────────────────────────────

let barrageTimer = BARRAGE_MIN_TICKS;

function scheduleNextBarrage() {
  barrageTimer = BARRAGE_MIN_TICKS +
    Math.floor(Math.random() * (BARRAGE_MAX_TICKS - BARRAGE_MIN_TICKS));
}

system.runInterval(() => {
  barrageTimer--;
  if (barrageTimer <= 0) {
    fireArtilleryBarrage();
    scheduleNextBarrage();
  }
}, 1);

function fireArtilleryBarrage() {
  const players = world.getAllPlayers();
  if (players.length === 0) return;

  // Pick a random player's location as barrage center
  const target = players[Math.floor(Math.random() * players.length)];
  const cx = Math.floor(target.location.x) + Math.floor(Math.random() * 60 - 30);
  const cz = Math.floor(target.location.z) + Math.floor(Math.random() * 60 - 30);

  world.sendMessage(
    `§c[WAR EVENT] Artillery barrage incoming at ${cx}, ${cz}! Seek cover in 10 seconds!`
  );

  // Delay 10 seconds, then place craters and damage players
  system.runTimeout(() => {
    const dim = target.dimension;
    const craterCount = 6 + Math.floor(Math.random() * 5);
    for (let i = 0; i < craterCount; i++) {
      const bx = cx + Math.floor(Math.random() * 30 - 15);
      const bz = cz + Math.floor(Math.random() * 30 - 15);
      // Find surface Y
      try {
        dim.runCommand(`fill ${bx} ~ ${bz} ${bx} ~-2 ${bz} ${MOD_NS}:shell_crater`);
        dim.runCommand(`particle minecraft:huge_explosion_emitter ${bx} ~ ${bz}`);
      } catch { /* ignore out-of-bounds */ }
    }

    // Damage players in barrage zone
    for (const p of world.getAllPlayers()) {
      const dx = p.location.x - cx;
      const dz = p.location.z - cz;
      if (Math.sqrt(dx * dx + dz * dz) <= 20) {
        p.applyDamage(3, { cause: mc.EntityDamageCause.fireworks });
      }
    }
  }, 200); // 200 ticks = 10 seconds
}

// ── Signal Flare Handling ─────────────────────────────────────────────────────

world.afterEvents.projectileHitBlock.subscribe((event) => {
  const proj = event.source;
  if (!proj || !proj.typeId) return;

  const loc = event.location;

  if (proj.typeId === `${MOD_NS}:signal_flare_red`) {
    // Gas Attack event
    world.sendMessage(
      `§2[WAR EVENT] Gas attack at ${Math.floor(loc.x)}, ${Math.floor(loc.z)}! Equip your gas mask!`
    );
    system.runTimeout(() => {
      placeGasCloud(event.dimension, loc, 7);
    }, 60); // 3-second warning

  } else if (proj.typeId === `${MOD_NS}:signal_flare_green`) {
    // Supply Drop
    world.sendMessage(
      `§a[WAR EVENT] Supply drop incoming at ${Math.floor(loc.x)}, ${Math.floor(loc.z)}!`
    );
    system.runTimeout(() => {
      event.dimension.runCommand(
        `summon ${MOD_NS}:supply_crate_falling ${loc.x} 255 ${loc.z}`
      );
    }, 40);

  } else if (proj.typeId === `${MOD_NS}:signal_flare_gray`) {
    // Smoke Screen — no broadcast, tactical
    placeSmokeScreen(event.dimension, loc, 3);
  }
});

function placeGasCloud(dimension, center, radius) {
  for (let dx = -radius; dx <= radius; dx++) {
    for (let dz = -radius; dz <= radius; dz++) {
      if (dx * dx + dz * dz <= radius * radius) {
        try {
          dimension.runCommand(
            `setblock ${Math.floor(center.x + dx)} ${Math.floor(center.y)} ${Math.floor(center.z + dz)} ${MOD_NS}:gas_cloud`
          );
        } catch { /* ignore */ }
      }
    }
  }
}

function placeSmokeScreen(dimension, center, radius) {
  for (let dx = -radius; dx <= radius; dx++) {
    for (let dy = 0; dy <= radius; dy++) {
      for (let dz = -radius; dz <= radius; dz++) {
        try {
          dimension.runCommand(
            `setblock ${Math.floor(center.x + dx)} ${Math.floor(center.y + dy)} ${Math.floor(center.z + dz)} ${MOD_NS}:smoke_screen`
          );
        } catch { /* ignore */ }
      }
    }
  }
}

// ── Territory Control ─────────────────────────────────────────────────────────

/**
 * Capture timers: Map<blockLocationKey, { faction, playerUUID, ticksHeld }>
 * Block location key: "x,y,z"
 */
const captureTimers = new Map();
const CAPTURE_TICKS = 20 * 60 * 2; // 2 minutes

system.runInterval(() => {
  for (const player of world.getAllPlayers()) {
    const faction = getPlayerFaction(player);
    if (faction === FACTIONS.NONE) continue;

    // Check if player is near a flag block
    const loc = player.location;
    const dim = player.dimension;

    for (let dx = -3; dx <= 3; dx++) {
      for (let dy = -1; dy <= 1; dy++) {
        for (let dz = -3; dz <= 3; dz++) {
          const block = dim.getBlock({
            x: Math.floor(loc.x + dx),
            y: Math.floor(loc.y + dy),
            z: Math.floor(loc.z + dz),
          });
          if (!block || block.typeId !== `${MOD_NS}:flag_block`) continue;

          const key = `${block.location.x},${block.location.y},${block.location.z}`;
          const entry = captureTimers.get(key) ?? { faction, playerUUID: player.id, ticksHeld: 0 };

          // Reset if contested or different player
          if (entry.faction !== faction) {
            captureTimers.set(key, { faction, playerUUID: player.id, ticksHeld: 0 });
            player.onScreenDisplay.setActionBar("§eContesting flag...");
            continue;
          }

          entry.ticksHeld++;
          const pct = Math.floor((entry.ticksHeld / CAPTURE_TICKS) * 100);
          player.onScreenDisplay.setActionBar(`§aClaiming flag: ${pct}%`);

          if (entry.ticksHeld >= CAPTURE_TICKS) {
            // Captured!
            const factionName = faction === FACTIONS.ALLIES ? "§9Allies§r" : "§cCentral Powers§r";
            world.sendMessage(`§e[TERRITORY] §f${player.name} has captured a trench for the ${factionName}!`);
            captureTimers.delete(key);
            // TODO: Change flag block color (Phase 6 full implementation)
          } else {
            captureTimers.set(key, entry);
          }
        }
      }
    }
  }
}, 1);

// ── Startup Log ───────────────────────────────────────────────────────────────

world.afterEvents.worldLoad?.subscribe?.(() => {
  world.sendMessage("§6[Mindcraft Mod] WW1 Warfare Add-on loaded. Good luck, soldier.");
});
