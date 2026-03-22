package com.mindcraftmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

/**
 * Gas Grenadier — hostile NPC that lobs Gas Canisters at players.
 *
 * Phase 4 will implement:
 * - Throw goal: lobs GasCanisteProjectile at players within 12 blocks
 * - Cooldown: 40 ticks between throws
 * - The projectile places a 3×3 Gas Cloud on impact (handled by GasCanisteProjectile)
 * - Drops: Gas Mask (20% chance), Gas Canister x1-3
 * - Custom gas-mask-wearing soldier model
 *
 * Phase 3 stub: minimal HostileEntity to satisfy ModEntities registration.
 */
public class GasGrenadierEntity extends HostileEntity {

    public GasGrenadierEntity(EntityType<? extends GasGrenadierEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initGoals() {
        // TODO Phase 4: add wander, throw gas canister, melee attack goals
    }
}
