package com.mindcraftmod.item;

import com.mindcraftmod.entity.GasCanisteProjectile;
import com.mindcraftmod.entity.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Gas Canister — throwable item that places a 3x3 Gas Cloud on impact.
 *
 * Player-usable version of the Gas Grenadier's thrown weapon.
 * - Spawns GasCanisteProjectile which places gas cloud blocks on block hit
 * - Crafting: Glass Bottle + Gunpowder + Yellow Dye → 1x Gas Canister
 * - Stack size: 8 (set in ModItems)
 *
 * NOTE: Class name follows the typo in ModItems.java ("GasCanisteItem")
 * to maintain registry consistency.
 */
public class GasCanisteItem extends Item {

    public GasCanisteItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            GasCanisteProjectile projectile =
                    new GasCanisteProjectile(ModEntities.GAS_CANISTE_PROJECTILE, player, world);
            projectile.setVelocity(player, player.getPitch(), player.getYaw(), -20f, 1.0f, 1.0f);
            world.spawnEntity(projectile);

            // Hissing canister sound
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_LINGERING_POTION_THROW, SoundCategory.PLAYERS, 0.6f, 0.9f);

            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        return TypedActionResult.success(stack, world.isClient);
    }
}
