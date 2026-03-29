package com.mindcraftmod.item;

import com.mindcraftmod.MindcraftMod;
import com.mindcraftmod.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Central registry for all mod items.
 *
 * Run AFTER ModBlocks.register() so block items can reference registered blocks.
 *
 * NOTE: In MC 1.21.4, Item.<init> calls settings.getTranslationKey() which requires
 * the registry key to be set on Settings BEFORE construction. Use the itemKey()
 * helper on every item's settings.
 */
public class ModItems {

    // ── Weapons ─────────────────────────────────────────────────────────────
    public static final Item BOLT_ACTION_RIFLE = new BoltActionRifleItem(
            itemKey("bolt_action_rifle", new Item.Settings().maxCount(1).maxDamage(384)));

    public static final Item TRENCH_BAYONET = new TrenchBayonetItem(
            itemKey("trench_bayonet", new Item.Settings().maxCount(1).maxDamage(300)));

    public static final Item GRENADE = new GrenadeItem(
            itemKey("grenade", new Item.Settings().maxCount(16)));

    // ── Armor ────────────────────────────────────────────────────────────────
    public static final Item GAS_MASK = new GasMaskItem(
            itemKey("gas_mask", new Item.Settings().maxCount(1).maxDamage(165).equippable(EquipmentSlot.HEAD)));

    public static final Item TRENCH_COAT = new TrenchCoatItem(
            itemKey("trench_coat", new Item.Settings().maxCount(1).maxDamage(240).equippable(EquipmentSlot.CHEST)));

    public static final Item HORSE_ARMOR_PLATE = new Item(
            itemKey("horse_armor_plate", new Item.Settings().maxCount(1)));

    // ── Utility ──────────────────────────────────────────────────────────────
    public static final Item FIELD_RATIONS = new FieldRationsItem(
            itemKey("field_rations", new Item.Settings().maxCount(16).food(ModFoodComponents.FIELD_RATIONS)));

    public static final Item SIGNAL_FLARE_RED = new SignalFlareItem(
            SignalFlareItem.Type.RED, itemKey("signal_flare_red", new Item.Settings().maxCount(16)));

    public static final Item SIGNAL_FLARE_GREEN = new SignalFlareItem(
            SignalFlareItem.Type.GREEN, itemKey("signal_flare_green", new Item.Settings().maxCount(16)));

    public static final Item SIGNAL_FLARE_GRAY = new SignalFlareItem(
            SignalFlareItem.Type.GRAY, itemKey("signal_flare_gray", new Item.Settings().maxCount(16)));

    public static final Item RIFLE_CARTRIDGE = new Item(
            itemKey("rifle_cartridge", new Item.Settings().maxCount(64)));

    public static final Item GAS_CANISTER = new GasCanisteItem(
            itemKey("gas_canister", new Item.Settings().maxCount(8)));

    public static final Item MUD_BALL = new MudBallItem(
            itemKey("mud_ball", new Item.Settings().maxCount(16)));

    public static final Item LEATHER_SCRAP = new Item(
            itemKey("leather_scrap", new Item.Settings().maxCount(64)));

    // ── Block Items (auto-generated from blocks) ──────────────────────────────
    // Block items let players hold mod blocks in inventory.

    public static void register() {
        // Weapons
        registerItem("bolt_action_rifle", BOLT_ACTION_RIFLE);
        registerItem("trench_bayonet",    TRENCH_BAYONET);
        registerItem("grenade",           GRENADE);

        // Armor
        registerItem("gas_mask",          GAS_MASK);
        registerItem("trench_coat",       TRENCH_COAT);
        registerItem("horse_armor_plate", HORSE_ARMOR_PLATE);

        // Utility
        registerItem("field_rations",       FIELD_RATIONS);
        registerItem("signal_flare_red",    SIGNAL_FLARE_RED);
        registerItem("signal_flare_green",  SIGNAL_FLARE_GREEN);
        registerItem("signal_flare_gray",   SIGNAL_FLARE_GRAY);
        registerItem("rifle_cartridge",     RIFLE_CARTRIDGE);
        registerItem("gas_canister",        GAS_CANISTER);
        registerItem("mud_ball",            MUD_BALL);
        registerItem("leather_scrap",       LEATHER_SCRAP);

        // Block items
        registerBlockItem("barbed_wire",        ModBlocks.BARBED_WIRE);
        registerBlockItem("gas_cloud",          ModBlocks.GAS_CLOUD);
        registerBlockItem("smoke_screen",       ModBlocks.SMOKE_SCREEN);
        registerBlockItem("mud_pit",            ModBlocks.MUD_PIT);
        registerBlockItem("sandbag",            ModBlocks.SANDBAG);
        registerBlockItem("trench_wall",        ModBlocks.TRENCH_WALL);
        registerBlockItem("artillery_platform", ModBlocks.ARTILLERY_PLATFORM);
        registerBlockItem("field_telephone",    ModBlocks.FIELD_TELEPHONE);
        registerBlockItem("supply_crate",       ModBlocks.SUPPLY_CRATE);
        registerBlockItem("shell_crater",       ModBlocks.SHELL_CRATER);
        registerBlockItem("barbed_wire_post",   ModBlocks.BARBED_WIRE_POST);
        registerBlockItem("rusted_iron_bars",   ModBlocks.RUSTED_IRON_BARS);
        registerBlockItem("flag_block",         ModBlocks.FLAG_BLOCK);

        MindcraftMod.LOGGER.info("Items registered.");
    }

    /**
     * Applies the mod-namespaced registry key to Item.Settings before construction.
     * Required in MC 1.21.4: Item.<init> calls getTranslationKey() which requires
     * the registry key to be set on Settings at construction time.
     */
    private static Item.Settings itemKey(String name, Item.Settings settings) {
        return settings.registryKey(
                RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MindcraftMod.MOD_ID, name)));
    }

    private static void registerItem(String name, Item item) {
        Registry.register(Registries.ITEM, Identifier.of(MindcraftMod.MOD_ID, name), item);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM,
                Identifier.of(MindcraftMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings().registryKey(
                        RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MindcraftMod.MOD_ID, name)))));
    }
}
