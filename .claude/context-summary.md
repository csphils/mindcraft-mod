# WW1 Warfare Mod — Project Context Summary

Update this file at the start of each phase. It is injected into every subagent and after context compaction.

---

## Current Phase
**Phase 1 complete. Phases 2–6 partially implemented (~65%). Phase 7 not started.**

## What Is Done
- All 11 items implemented
- 10/13 blocks implemented (missing: BarbiWirePost, RustedIronBars, WarPoster, GraveMarker class files)
- All 7 mobs registered with correct attributes
- All 7 structure classes + worldgen JSON registered (but NBT content files are empty placeholders)
- Faction system, territory control, world events, sync packets implemented
- Testing infrastructure: 25 unit tests + 28 GameTests

## Critical Constraints
- Minecraft 1.21.4, Fabric Loader 0.16.10, fabric-api 0.114.0, Java 21
- Mod ID: `mindcraftmod` — used in all resource paths
- All blocks/items/entities must be registered in ModBlocks / ModItems / ModEntities
- GameTests require SNBT structure files in `data/mindcraftmod/gametest/structures/`
- Two texture scripts exist — `scripts/generate_textures.py` is the procedural (higher quality) one
- Hardcoded path in `scripts/generate_textures.py` must be fixed before it works cross-machine

## What NOT to Change Without a Plan
- `MindcraftMod.java` — main mod init, only change when adding new registries
- `ModBlocks / ModItems / ModEntities` — always register new things here, never elsewhere
- `build.gradle` — only change for dependency or test config updates
- `fabric.mod.json` — version and entrypoints only

## Biggest Open Gaps
1. Structure NBT files — all 7 structure types generate nothing (empty placeholders)
2. Decorative block Java classes missing (BarbiWirePost, RustedIronBars, WarPoster, GraveMarker)
3. Mob renderers are all generic (no custom models)
4. Bedrock edition is ~15% complete
5. No multiplayer tests

## SDLC Agent Workflow
```
phase-planner → art-reviewer → tdd-guide → implement → plan-monitor (continuous) → commit
```
