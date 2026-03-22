package com.mindcraftmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

/**
 * Sniper — rare hostile NPC positioned atop Observation Towers.
 *
 * Phase 4 will implement:
 * - Long-range attack: fires TrenchRifleProjectile at players up to 30 blocks away
 * - Crouch detection: player crouching reduces Sniper's detection range to 10 blocks
 * - Stays on the tower (no path-finding down); only fires when player enters range
 * - Drops: Bolt-Action Rifle (guaranteed), Rifle Cartridge x8-16
 * - Custom ghillie suit model
 *
 * Phase 3 stub: minimal HostileEntity to satisfy ModEntities registration.
 */
public class SniperEntity extends HostileEntity {

    /** Detection range in blocks (reduced to 10 if target is crouching). */
    public static final int DETECTION_RANGE = 30;
    public static final int CROUCH_DETECTION_RANGE = 10;

    public SniperEntity(EntityType<? extends SniperEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initGoals() {
        // TODO Phase 4: add look-at, fire rifle (range-limited), crouch-detection sensor
    }
}
