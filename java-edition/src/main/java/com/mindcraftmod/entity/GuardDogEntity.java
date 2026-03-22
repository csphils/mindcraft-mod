package com.mindcraftmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Guard Dog — neutral mob that patrols structures and can be tamed with Field Rations.
 *
 * Phase 4 will implement:
 * - Neutral until player enters 5-block radius of its guarded structure
 * - Attack goal: chases and bites non-owner players in range
 * - Tameable with 3x Field Rations (FieldRationsItem); uses TameableEntity mechanics
 * - When tamed: follows owner, sits/stands on command
 * - When tamed + owner attacked: sends owner a chat alert ("Your dog barks at <mob>!")
 * - Custom bark sounds
 *
 * Phase 3 stub: minimal TameableEntity to satisfy ModEntities registration.
 */
public class GuardDogEntity extends TameableEntity {

    public GuardDogEntity(EntityType<? extends GuardDogEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initGoals() {
        // TODO Phase 4: add sit, follow owner, attack hostile, alert owner goals
    }

    @Override
    public @Nullable AnimalEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null; // Guard dogs do not breed
    }
}
