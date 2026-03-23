package com.mindcraftmod.item;

import net.minecraft.item.Item;

/**
 * Trench Coat — chestplate armor that reduces Mud Pit slowness.
 *
 * - Slot: Chestplate
 * - Armor: 5 defense (same as iron chestplate)
 * - Special: MudPitBlock checks instanceof TrenchCoatItem to give Slowness I instead of II
 * - Crafting: 7x Leather + 1x Iron Ingot (center, standard chestplate pattern)
 */
public class TrenchCoatItem extends Item {

    public TrenchCoatItem(Settings settings) {
        super(settings);
    }
}
