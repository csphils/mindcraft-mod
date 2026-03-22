package com.mindcraftmod.item;

import com.mindcraftmod.entity.ModEntities;
import com.mindcraftmod.entity.MudBallProjectile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Mud Ball — throwable item dropped when breaking Mud Pit.
 *
 * - On hit: Applies Slowness I for 3 seconds (60 ticks) to the target
 * - No damage
 * - Thrown like a snowball
 * - Stack size: 16 (set in ModItems)
 */
public class MudBallItem extends Item {

    public MudBallItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            MudBallProjectile mudBall =
                    new MudBallProjectile(ModEntities.MUD_BALL_PROJECTILE, player, world);
            mudBall.setVelocity(player, player.getPitch(), player.getYaw(), 0f, 1.5f, 1.0f);
            world.spawnEntity(mudBall);

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.4f, 0.8f);

            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        return TypedActionResult.success(stack, world.isClient);
    }
}
