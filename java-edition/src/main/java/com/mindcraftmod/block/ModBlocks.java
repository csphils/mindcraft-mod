package com.mindcraftmod.block;

import com.mindcraftmod.MindcraftMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

/**
 * Central registry for all mod blocks.
 *
 * Pattern: declare public static final fields here, register them in register().
 * Block items are registered in ModItems.java after this runs.
 *
 * NOTE: In MC 1.21.4, AbstractBlock.<init> calls settings.getLootTableKey() which
 * requires the registry key to be set on Settings BEFORE construction. Use the
 * key() helper to set it on every block's settings.
 */
public class ModBlocks {

    // ── Pass-Through Blocks ─────────────────────────────────────────────────
    /** Barbed wire — carpet-style, no collision, slows and damages players. */
    public static final Block BARBED_WIRE = new BarbedWireBlock(
            key("barbed_wire", AbstractBlock.Settings.create()
                    .mapColor(MapColor.IRON_GRAY)
                    .sounds(BlockSoundGroup.METAL)
                    .noCollision()
                    .breakInstantly()
                    .nonOpaque()
            ));

    /** Gas cloud — full-block space, non-solid, applies Poison II. Spreads via random tick. */
    public static final Block GAS_CLOUD = new GasCloudBlock(
            key("gas_cloud", AbstractBlock.Settings.create()
                    .mapColor(MapColor.YELLOW)
                    .noCollision()
                    .nonOpaque()
                    .noBlockBreakParticles()
                    .ticksRandomly()
            ));

    /** Smoke screen — non-solid, applies Blindness. Dissipates via random tick. */
    public static final Block SMOKE_SCREEN = new SmokeScreenBlock(
            key("smoke_screen", AbstractBlock.Settings.create()
                    .mapColor(MapColor.GRAY)
                    .noCollision()
                    .nonOpaque()
                    .noBlockBreakParticles()
                    .ticksRandomly()
            ));

    /** Mud pit — solid, applies slowness like Soul Sand. */
    public static final Block MUD_PIT = new MudPitBlock(
            key("mud_pit", AbstractBlock.Settings.create()
                    .mapColor(MapColor.BROWN)
                    .sounds(BlockSoundGroup.MUD)
                    .strength(0.5f)
            ));

    // ── Functional Blocks ───────────────────────────────────────────────────
    /** Stackable sandbag layers (1-8), blast-absorbing fortification. */
    public static final Block SANDBAG = new SandbagBlock(
            key("sandbag", AbstractBlock.Settings.create()
                    .mapColor(MapColor.PALE_YELLOW)
                    .sounds(BlockSoundGroup.SAND)
                    .strength(1.0f, 6.0f)
                    .dynamicBounds()  // shape varies by LAYERS — must not be cached
            ));

    /** Directional trench wall — connects like fence, dirt+timber look. */
    public static final Block TRENCH_WALL = new TrenchWallBlock(
            key("trench_wall", AbstractBlock.Settings.create()
                    .mapColor(MapColor.DIRT_BROWN)
                    .sounds(BlockSoundGroup.WOOD)
                    .strength(1.5f, 3.0f)
            ));

    /** 3x3 raised stone slab — required base for placing Cannon entity. */
    public static final Block ARTILLERY_PLATFORM = new Block(
            key("artillery_platform", AbstractBlock.Settings.create()
                    .mapColor(MapColor.STONE_GRAY)
                    .sounds(BlockSoundGroup.STONE)
                    .strength(3.0f, 10.0f)
            ));

    /** Sends faction-scoped chat to players within 200 blocks. */
    public static final Block FIELD_TELEPHONE = new FieldTelephoneBlock(
            key("field_telephone", AbstractBlock.Settings.create()
                    .mapColor(MapColor.BLACK)
                    .sounds(BlockSoundGroup.METAL)
                    .strength(2.0f)
                    .nonOpaque()
            ));

    /** Chest-like supply container, pre-filled from loot table. */
    public static final Block SUPPLY_CRATE = new SupplyCrateBlock(
            key("supply_crate", AbstractBlock.Settings.create()
                    .mapColor(MapColor.OAK_TAN)
                    .sounds(BlockSoundGroup.WOOD)
                    .strength(2.5f)
            ));

    /** Multi-block shell crater — decorative depression left by explosions. */
    public static final Block SHELL_CRATER = new ShellCraterBlock(
            key("shell_crater", AbstractBlock.Settings.create()
                    .mapColor(MapColor.DIRT_BROWN)
                    .sounds(BlockSoundGroup.GRAVEL)
                    .strength(0.3f)
            ));

    // ── Decorative Blocks ───────────────────────────────────────────────────
    public static final Block BARBED_WIRE_POST = new Block(
            key("barbed_wire_post", AbstractBlock.Settings.create()
                    .mapColor(MapColor.IRON_GRAY)
                    .sounds(BlockSoundGroup.METAL)
                    .strength(1.0f)
            ));

    public static final Block RUSTED_IRON_BARS = new RustedIronBarsBlock(
            key("rusted_iron_bars", AbstractBlock.Settings.create()
                    .mapColor(MapColor.IRON_GRAY)
                    .sounds(BlockSoundGroup.METAL)
                    .strength(1.5f, 6.0f)
                    .nonOpaque()
            ));

    public static final Block WAR_POSTER = new WarPosterBlock(
            key("war_poster", AbstractBlock.Settings.create()
                    .mapColor(MapColor.PALE_YELLOW)
                    .sounds(BlockSoundGroup.WOOD)
                    .strength(0.5f)
                    .nonOpaque()
            ));

    public static final Block GRAVE_MARKER = new GraveMarkerBlock(
            key("grave_marker", AbstractBlock.Settings.create()
                    .mapColor(MapColor.WHITE_GRAY)
                    .sounds(BlockSoundGroup.WOOD)
                    .strength(1.0f)
                    .nonOpaque()
            ));

    public static final Block FLAG_BLOCK = new FlagBlock(
            key("flag_block", AbstractBlock.Settings.create()
                    .mapColor(MapColor.WHITE)
                    .sounds(BlockSoundGroup.WOOL)
                    .strength(1.0f)
                    .nonOpaque()
            ));

    // ────────────────────────────────────────────────────────────────────────

    public static void register() {
        registerBlock("barbed_wire",        BARBED_WIRE);
        registerBlock("gas_cloud",          GAS_CLOUD);
        registerBlock("smoke_screen",       SMOKE_SCREEN);
        registerBlock("mud_pit",            MUD_PIT);
        registerBlock("sandbag",            SANDBAG);
        registerBlock("trench_wall",        TRENCH_WALL);
        registerBlock("artillery_platform", ARTILLERY_PLATFORM);
        registerBlock("field_telephone",    FIELD_TELEPHONE);
        registerBlock("supply_crate",       SUPPLY_CRATE);
        registerBlock("shell_crater",       SHELL_CRATER);
        registerBlock("barbed_wire_post",   BARBED_WIRE_POST);
        registerBlock("rusted_iron_bars",   RUSTED_IRON_BARS);
        registerBlock("war_poster",         WAR_POSTER);
        registerBlock("grave_marker",       GRAVE_MARKER);
        registerBlock("flag_block",         FLAG_BLOCK);

        MindcraftMod.LOGGER.info("Blocks registered.");
    }

    /**
     * Applies the mod-namespaced registry key to block Settings before construction.
     * Required in MC 1.21.4: AbstractBlock.<init> calls getLootTableKey() which
     * requires the registry key to be set on Settings at construction time.
     */
    private static AbstractBlock.Settings key(String name, AbstractBlock.Settings settings) {
        return settings.registryKey(
                RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MindcraftMod.MOD_ID, name)));
    }

    private static void registerBlock(String name, Block block) {
        Registry.register(Registries.BLOCK, Identifier.of(MindcraftMod.MOD_ID, name), block);
    }
}
