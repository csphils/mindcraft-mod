package com.mindcraftmod.entity;

import com.mindcraftmod.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

/**
 * Grenade Entity — physics-driven grenade with a 3-second fuse.
 *
 * Thrown by GrenadeItem. Ticks down a 60-tick (3 second) fuse regardless of
 * whether it is in flight or has landed. When the fuse expires, it detonates
 * with a 3-block power explosion (destroys blocks, damages entities in radius).
 *
 * The grenade does NOT explode on contact — only on fuse expiry.
 */
public class GrenadeEntity extends ThrownItemEntity {

    private static final int FUSE_TICKS = 60; // 3 seconds
    private static final float EXPLOSION_POWER = 3.0f;

    private int fuse = FUSE_TICKS;

    public GrenadeEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public GrenadeEntity(EntityType<? extends ThrownItemEntity> entityType,
                          LivingEntity owner, World world) {
        super(entityType, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.GRENADE;
    }

    @Override
    public void tick() {
        super.tick();
        fuse--;
        if (fuse <= 0 && !this.getWorld().isClient) {
            this.getWorld().createExplosion(
                    this,
                    this.getX(), this.getY(), this.getZ(),
                    EXPLOSION_POWER,
                    false,
                    World.ExplosionSourceType.TNT
            );
            this.discard();
        }
    }

    /**
     * Do NOT explode on entity contact — grenade bounces or stops, fuse continues.
     */
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Grenade passes through / knocks off entities without detonating early
    }

    /**
     * Do NOT discard on block hit — let the grenade land and wait for fuse.
     * ThrownItemEntity default behaviour would discard it; we override to prevent that.
     */
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        // Land and wait — fuse continues ticking
    }
}
