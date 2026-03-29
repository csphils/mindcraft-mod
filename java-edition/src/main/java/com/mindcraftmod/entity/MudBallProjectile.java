package com.mindcraftmod.entity;

import com.mindcraftmod.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

/**
 * Mud Ball Projectile — applies Slowness I on hit.
 *
 * Thrown by MudBallItem. Behaves like a Snowball but instead of zero damage
 * it applies Slowness I (amplifier 0) for 3 seconds (60 ticks) to the target.
 * No block damage.
 */
public class MudBallProjectile extends ThrownItemEntity {

    public MudBallProjectile(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public MudBallProjectile(EntityType<? extends ThrownItemEntity> entityType,
                              LivingEntity owner, World world) {
        super(entityType, owner, world, new net.minecraft.item.ItemStack(ModItems.MUD_BALL));
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.MUD_BALL;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity target = entityHitResult.getEntity();
        if (target instanceof LivingEntity living) {
            // Slowness I for 3 seconds (60 ticks)
            living.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOWNESS, 60, 0, false, true));
        }
        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.discard();
    }
}
