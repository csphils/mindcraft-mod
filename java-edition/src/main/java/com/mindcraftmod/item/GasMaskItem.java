package com.mindcraftmod.item;

import net.minecraft.item.Item;

/**
 * Gas Mask — helmet armor that negates Gas Cloud and Smoke Screen effects.
 *
 * - Slot: Helmet
 * - Armor: 2 defense (same as iron helmet)
 * - Special: GasCloudBlock and SmokeScreenBlock check instanceof GasMaskItem to skip effects
 * - Crafting: 2x Iron Ingot + 2x Glass Pane + 1x String
 */
public class GasMaskItem extends Item {

    public GasMaskItem(Settings settings) {
        super(settings);
    }
}
