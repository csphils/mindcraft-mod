package com.mindcraftmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

/**
 * Trench Soldier — hostile humanoid NPC that patrols trench structures.
 *
 * Phase 4 will implement:
 * - Patrol goal: walks a configurable path within the trench structure
 * - Ranged attack: fires TrenchRifleProjectile at players within 20 blocks
 * - Retreat goal: moves to nearest Sandbag wall when health drops below 30%
 * - Drops: Rifle Cartridge x4-8, chance for Trench Bayonet or Bolt-Action Rifle
 * - Custom soldier uniform model and textures
 *
 * Phase 3 stub: minimal HostileEntity to satisfy ModEntities registration.
 */
public class TrenchSoldierEntity extends HostileEntity {

    public TrenchSoldierEntity(EntityType<? extends TrenchSoldierEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initGoals() {
        // TODO Phase 4: add patrol, fire rifle, retreat to cover goals
    }
}
