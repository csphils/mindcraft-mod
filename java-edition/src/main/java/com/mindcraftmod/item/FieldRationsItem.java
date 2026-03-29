package com.mindcraftmod.item;

import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

/**
 * Field Rations — a food item that restores 4 hunger.
 *
 * Also serves as the taming item for Guard Dog (3 uses required).
 * The taming logic lives in GuardDogEntity (Phase 4).
 *
 * No custom eat behavior — the FoodComponent set in ModItems handles hunger/saturation.
 */
public class FieldRationsItem extends Item {

    public FieldRationsItem(Settings settings) {
        super(settings);
    }
}
