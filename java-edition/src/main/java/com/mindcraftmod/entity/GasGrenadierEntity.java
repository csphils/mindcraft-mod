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
 * Gas Grenadier — hostile NPC that lobs Gas Canisters at players.
 *
 * Behaviour:
 * - Throws GasCanisteProjectile at players within 12 blocks; 40-tick cooldown
 * - The projectile places a 3×3 Gas Cloud on impact (handled by GasCanisteProjectile)
 * - Falls back to melee when targets are within 2 blocks
 * - Drops: Gas Mask (20% chance), Gas Canister ×1–3 (loot table)
 */
public class GasGrenadierEntity extends HostileEntity implements RangedAttackMob {

    /** Throw range in blocks. */
    public static final float THROW_RANGE = 12.0f;

    public GasGrenadierEntity(EntityType<? extends GasGrenadierEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 18.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.22)
                .add(EntityAttributes.FOLLOW_RANGE, (double) THROW_RANGE)
                .add(EntityAttributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
        // Ranged: throw gas canister, 40-tick cooldown, 12-block range
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 40, THROW_RANGE));
        // Fallback melee
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, true));
    }

    // ── RangedAttackMob ──────────────────────────────────────────────────────

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if (this.getWorld().isClient) return;

        GasCanisteProjectile canister = new GasCanisteProjectile(
                ModEntities.GAS_CANISTE_PROJECTILE, this, this.getWorld());
        canister.setOwner(this);

        double dx = target.getX() - this.getX();
        double dy = target.getBodyY(0.33) - canister.getY();
        double dz = target.getZ() - this.getZ();
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        // Lob arc — higher vertical component than a rifle shot
        canister.setVelocity(dx, dy + horizontalDist * 0.2, dz,
                0.7f, 2.0f);

        this.getWorld().spawnEntity(canister);
        this.playSound(SoundEvents.ENTITY_SNOWBALL_THROW, 1.0f, 0.75f);
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
