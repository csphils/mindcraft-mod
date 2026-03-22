package com.mindcraftmod.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentType;

/**
 * Gas Mask — helmet armor that negates Gas Cloud and Smoke Screen effects.
 *
 * - Slot: Helmet
 * - Armor: 2 defense (same as iron helmet)
 * - Special: GasCloudBlock and SmokeScreenBlock check instanceof GasMaskItem to skip effects
 * - Crafting: 2x Iron Ingot + 2x Glass Pane + 1x String
 */
public class GasMaskItem extends ArmorItem {

    public GasMaskItem(Settings settings) {
        super(ArmorMaterials.IRON, EquipmentType.HELMET, settings);
    }
}
