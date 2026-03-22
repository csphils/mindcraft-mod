package com.mindcraftmod.item;

import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;

/**
 * Trench Bayonet — a close-quarters melee weapon.
 *
 * - Damage: 6 (iron sword equivalent)
 * - Attack speed: 1.6 (slightly slower than sword, -2.4 modifier)
 * - Has sweep attack (inherited from SwordItem)
 * - Durability: 300 (set in ModItems)
 * - Crafting: Iron Ingot + Iron Ingot + Stick (vertical column)
 */
public class TrenchBayonetItem extends SwordItem {

    public TrenchBayonetItem(Settings settings) {
        // IRON ToolMaterial gives 6 total attack damage for swords at -2.4 attack speed
        super(ToolMaterials.IRON, settings);
    }
}
