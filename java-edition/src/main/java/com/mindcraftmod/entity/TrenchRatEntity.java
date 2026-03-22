package com.mindcraftmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Trench Rat — tiny passive creature that scurries around trench structures.
 *
 * Phase 4 will implement:
 * - Small hitbox (0.4×0.25), ground-only movement
 * - Drops Leather Scrap on death
 * - Can be caught with an empty bucket (returns Bucket with Rat)
 * - Squeaking ambient sounds
 *
 * Phase 3 stub: extends AnimalEntity to satisfy ModEntities registration.
 */
public class TrenchRatEntity extends AnimalEntity {

    public TrenchRatEntity(EntityType<? extends TrenchRatEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initGoals() {
        // TODO Phase 4: add wander, escape player goals
    }

    @Override
    public @Nullable AnimalEntity createChild(ServerWorld world, PassiveEntity entity) {
        return new TrenchRatEntity(ModEntities.TRENCH_RAT, world);
    }
}
