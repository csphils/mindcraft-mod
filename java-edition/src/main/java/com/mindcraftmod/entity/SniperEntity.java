package com.mindcraftmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

/**
 * Sniper — rare hostile NPC positioned atop Observation Towers.
 *
 * Behaviour:
 * - Stationary (no wander goal); fires from a fixed position
 * - Detection range: 30 blocks normally, reduced to 10 when target is crouching
 * - Fires TrenchRifleProjectile every 40 ticks; 8 damage
 * - Drops: Bolt-Action Rifle (guaranteed), Rifle Cartridge ×8–16 (loot table)
 */
public class SniperEntity extends HostileEntity implements RangedAttackMob {

    public static final int DETECTION_RANGE   = 30;
    public static final int CROUCH_RANGE      = 10;

    public SniperEntity(EntityType<? extends SniperEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.0)   // stationary
                .add(EntityAttributes.FOLLOW_RANGE, (double) DETECTION_RANGE)
                .add(EntityAttributes.ATTACK_DAMAGE, 8.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        // Long cooldown (40 ticks = 2 sec), long range, no wandering
        this.goalSelector.add(1, new ProjectileAttackGoal(this, 0.0, 40, (float) DETECTION_RANGE));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, DETECTION_RANGE));
        this.goalSelector.add(3, new LookAroundGoal(this));

        // Target players; predicate halves detection range for crouching players
        this.targetSelector.add(1, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, true,
                player -> {
                    double range = player.isSneaking() ? CROUCH_RANGE : DETECTION_RANGE;
                    return this.squaredDistanceTo(player) < range * range;
                }));
    }

    // ── RangedAttackMob ──────────────────────────────────────────────────────

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if (this.getWorld().isClient) return;

        TrenchRifleProjectile projectile = new TrenchRifleProjectile(ModEntities.TRENCH_RIFLE_PROJECTILE,
                this, this.getWorld());
        projectile.setOwner(this);

        double dx = target.getX() - this.getX();
        double dy = target.getBodyY(0.33) - projectile.getY();
        double dz = target.getZ() - this.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        // High-velocity shot with minimal arc — simulates flat rifle trajectory
        projectile.setVelocity(dx, dy + horizontalDist * 0.005, dz,
                2.5f, 0.5f);

        this.getWorld().spawnEntity(projectile);
        this.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0f, 0.8f);
    }

    // ── Sounds ───────────────────────────────────────────────────────────────

    @Override
    protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) {
        return SoundEvents.ENTITY_PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }
}
