package com.mindcraftmod.item;

import com.mindcraftmod.MindcraftMod;
import com.mindcraftmod.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Registers the "War Supplies" creative tab containing all mod items.
 */
public class ModItemGroups {

    public static final ItemGroup WAR_SUPPLIES = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.BOLT_ACTION_RIFLE))
            .displayName(Text.translatable("itemgroup.mindcraftmod.war_supplies"))
            .entries((context, entries) -> {
                // Weapons
                entries.add(ModItems.BOLT_ACTION_RIFLE);
                entries.add(ModItems.TRENCH_BAYONET);
                entries.add(ModItems.GRENADE);
                entries.add(ModItems.GAS_CANISTER);
                entries.add(ModItems.MUD_BALL);
                entries.add(ModItems.RIFLE_CARTRIDGE);
                // Armor
                entries.add(ModItems.GAS_MASK);
                entries.add(ModItems.TRENCH_COAT);
                entries.add(ModItems.HORSE_ARMOR_PLATE);
                // Utility
                entries.add(ModItems.FIELD_RATIONS);
                entries.add(ModItems.SIGNAL_FLARE_RED);
                entries.add(ModItems.SIGNAL_FLARE_GREEN);
                entries.add(ModItems.SIGNAL_FLARE_GRAY);
                entries.add(ModItems.LEATHER_SCRAP);
                // Blocks
                entries.add(ModBlocks.BARBED_WIRE);
                entries.add(ModBlocks.GAS_CLOUD);
                entries.add(ModBlocks.SMOKE_SCREEN);
                entries.add(ModBlocks.MUD_PIT);
                entries.add(ModBlocks.SANDBAG);
                entries.add(ModBlocks.TRENCH_WALL);
                entries.add(ModBlocks.ARTILLERY_PLATFORM);
                entries.add(ModBlocks.FIELD_TELEPHONE);
                entries.add(ModBlocks.SUPPLY_CRATE);
                entries.add(ModBlocks.SHELL_CRATER);
                entries.add(ModBlocks.FLAG_BLOCK);
                entries.add(ModBlocks.BARBED_WIRE_POST);
                entries.add(ModBlocks.RUSTED_IRON_BARS);
            })
            .build();

    public static void register() {
        Registry.register(Registries.ITEM_GROUP,
                Identifier.of(MindcraftMod.MOD_ID, "war_supplies"),
                WAR_SUPPLIES);
        MindcraftMod.LOGGER.info("Item groups registered.");
    }
}
