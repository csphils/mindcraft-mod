package com.mindcraftmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * War Horse — a larger, stronger horse with cavalry charge capability.
 *
 * Phase 4 will implement:
 * - Equippable Horse Armor Plate (+4 armor)
 * - Cavalry charge: 10 damage when sprinting while ridden
 * - Larger hitbox than vanilla horse (1.4×1.8)
 * - Custom sounds, textures, and models
 *
 * Phase 3 stub: extends AnimalEntity to satisfy the ModEntities registration.
 * Full implementation in Phase 4.
 */
public class WarHorseEntity extends AnimalEntity {

    public WarHorseEntity(EntityType<? extends WarHorseEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initGoals() {
        // TODO Phase 4: add wander, follow player, cavalry charge goal
    }

    @Override
    public @Nullable AnimalEntity createChild(ServerWorld world, PassiveEntity entity) {
        return new WarHorseEntity(ModEntities.WAR_HORSE, world);
    }
}
