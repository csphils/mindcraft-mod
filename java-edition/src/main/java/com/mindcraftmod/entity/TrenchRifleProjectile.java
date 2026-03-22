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
 * Trench Rifle Projectile — fast, straight, no-gravity bullet.
 *
 * - Fired by BoltActionRifleItem
 * - Deals 8 damage to hit entity
 * - No gravity (travels straight regardless of distance)
 * - Discards on entity hit or block hit
 */
public class TrenchRifleProjectile extends ThrownItemEntity {

    public TrenchRifleProjectile(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public TrenchRifleProjectile(EntityType<? extends ThrownItemEntity> entityType,
                                  LivingEntity owner, World world) {
        super(entityType, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BOLT_ACTION_RIFLE;
    }

    /** Override to disable gravity — rifle bullet travels in a straight line. */
    @Override
    protected void applyGravity() {
        // No gravity
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getWorld().isClient) {
            entityHitResult.getEntity().damage(
                    this.getWorld().getDamageSources().thrown(this, this.getOwner()),
                    8.0f
            );
            this.discard();
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.discard();
    }
}
