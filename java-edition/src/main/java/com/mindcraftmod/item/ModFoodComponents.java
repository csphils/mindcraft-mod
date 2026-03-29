package com.mindcraftmod.item;

import net.minecraft.component.type.FoodComponent;

/**
 * Food component definitions for all mod food items.
 *
 * FoodComponent defines: hunger restored, saturation multiplier, and optional effects.
 * Referenced from ModItems.java item field declarations.
 */
public class ModFoodComponents {

    /**
     * Field Rations — 4 hunger, moderate saturation.
     * Also used as taming material for Guard Dog.
     */
    public static final FoodComponent FIELD_RATIONS = new FoodComponent.Builder()
            .nutrition(4)
            .saturationModifier(1.2f)
            .build();
}
