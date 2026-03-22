package com.mindcraftmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Carrier Pigeon — small passive bird that can deliver items to faction members.
 *
 * Phase 4 will implement:
 * - Flying AI (uses Parrot-like navigation)
 * - Right-click with Written Book → assigns delivery target (another faction player)
 * - Delivery route: flies to target player and drops the book
 * - Custom pigeon model and cooing sound
 *
 * Phase 3 stub: extends AnimalEntity to satisfy the ModEntities registration.
 */
public class CarrierPigeonEntity extends AnimalEntity {

    public CarrierPigeonEntity(EntityType<? extends CarrierPigeonEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initGoals() {
        // TODO Phase 4: add fly, wander, follow player goals
    }

    @Override
    public @Nullable AnimalEntity createChild(ServerWorld world, PassiveEntity entity) {
        return new CarrierPigeonEntity(ModEntities.CARRIER_PIGEON, world);
    }
}
