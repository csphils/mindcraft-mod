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
 * Trench Soldier — hostile humanoid NPC that patrols trench structures.
 *
 * Behaviour:
 * - Targets players within 20 blocks
 * - Fires TrenchRifleProjectile every 20 ticks (1 second) at players within 20 blocks
 * - When health drops below 30% activates EscapeDangerGoal (retreat to cover)
 * - Drops: Rifle Cartridges, chance for Trench Bayonet (loot table)
 */
public class TrenchSoldierEntity extends HostileEntity implements RangedAttackMob {

    public TrenchSoldierEntity(EntityType<? extends TrenchSoldierEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.FOLLOW_RANGE, 20.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 3.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        // Retreat when low health (EscapeDangerGoal triggers on fire / recent damage)
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.5));
        // Ranged attack: fires projectile at up to 20 blocks, every 20 ticks
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 20, 20.0f));
        this.goalSelector.add(3, new WanderAroundGoal(this, 0.8));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, true));
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

        projectile.setVelocity(dx, dy + horizontalDist * 0.02, dz,
                1.6f, 1.0f);

        this.getWorld().spawnEntity(projectile);
        this.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0f, 1.0f);
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
