package com.mindcraftmod.item;

import com.mindcraftmod.entity.GrenadeEntity;
import com.mindcraftmod.entity.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Grenade — a thrown explosive with a 3-second fuse.
 *
 * - Right-click to throw immediately
 * - GrenadeEntity handles the 60-tick fuse and 3-block-power explosion
 * - Crafting: Iron Ingot (top) + Gunpowder (middle) + Iron Ingot (bottom) → 1x Grenade
 * - Stack size: 16 (set in ModItems)
 */
public class GrenadeItem extends Item {

    public GrenadeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            // Spawn grenade entity (handles fuse + explosion)
            GrenadeEntity grenade = new GrenadeEntity(ModEntities.GRENADE, player, world);
            grenade.setVelocity(player, player.getPitch(), player.getYaw(), -20f, 0.8f, 1.0f);
            world.spawnEntity(grenade);

            // Pin-pull sound
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.7f, 1.2f);

            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        return stack;
    }
}
