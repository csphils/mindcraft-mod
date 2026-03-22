package com.mindcraftmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Mud Pit — solid block that slows movement like Soul Sand.
 *
 * - Full solid block
 * - Applies Slowness II while on top
 * - Reduces jump height by capping upward velocity to 0.1
 * - Drops Mud Ball x2 when broken (see loot table)
 */
public class MudPitBlock extends Block {

    public MudPitBlock(Settings settings) {
        super(settings);
    }

    /**
     * Called each tick while an entity is on top of this block.
     * Mirrors Soul Sand behavior: slow horizontal movement and cap jump velocity.
     */
    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (world.isClient) return;
        if (!(entity instanceof LivingEntity living)) return;

        // Slowness II — refreshed each tick while standing on mud
        living.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS, 40, 1, false, false));

        // Cap upward velocity to simulate reduced jump height
        Vec3d vel = entity.getVelocity();
        if (vel.y > 0.1) {
            entity.setVelocity(vel.x, 0.1, vel.z);
        }
    }
}
