package com.mindcraftmod.entity;

import com.mindcraftmod.item.FieldRationsItem;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Guard Dog — neutral mob that patrols structures and can be tamed with Field Rations.
 *
 * Behaviour:
 * - Neutral until a player approaches within 6 blocks (untamed); then attacks
 * - Tameable with 3× Field Rations (33% chance per use)
 * - When tamed: follows owner, sits/stands on command (right-click)
 * - When tamed and owner is attacked: sends owner a chat alert and pursues attacker
 * - Drops 0–1 Leather Scrap on death
 */
public class GuardDogEntity extends TameableEntity {

    /** Tracks how many rations have been fed toward taming. */
    private int rationsFed = 0;

    public GuardDogEntity(EntityType<? extends GuardDogEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 20.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.30)
                .add(EntityAttributes.ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.add(3, new FollowOwnerGoal(this, 1.0, 10.0f, 2.0f));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        // Attack targets
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        // Untamed: attack players who come too close (range enforced by follow-range attr)
        this.targetSelector.add(3, new ActiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, false, false) {
            @Override public boolean canStart()       { return !GuardDogEntity.this.isTamed() && super.canStart(); }
            @Override public boolean shouldContinue() { return !GuardDogEntity.this.isTamed() && super.shouldContinue(); }
        });
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    // ── Taming mechanic ──────────────────────────────────────────────────────

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient) return ActionResult.SUCCESS;

        ItemStack held = player.getStackInHand(hand);
        if (held.getItem() instanceof FieldRationsItem && !this.isTamed()) {
            if (!player.getAbilities().creativeMode) held.decrement(1);
            rationsFed++;
            // 33% chance per ration; guaranteed after 3
            if (rationsFed >= 3 || this.getRandom().nextFloat() < 0.33f) {
                this.setOwner(player);
                this.setSitting(true);
                player.sendMessage(Text.literal("The guard dog is now loyal to you!"), true);
                rationsFed = 0;
            } else {
                player.sendMessage(Text.literal("The dog sniffs the rations... (" + rationsFed + "/3)"), true);
            }
            return ActionResult.SUCCESS;
        }

        // Owner right-clicks to toggle sit/stand
        if (this.isTamed() && this.isOwner(player)) {
            this.setSitting(!this.isSitting());
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    // ── Alert owner when the dog attacks something ───────────────────────────

    @Override
    public void onAttacking(net.minecraft.entity.Entity target) {
        super.onAttacking(target);
        if (this.isTamed()) {
            LivingEntity owner = this.getOwner();
            if (owner instanceof PlayerEntity playerOwner) {
                playerOwner.sendMessage(
                        Text.literal("Your dog barks at " + target.getName().getString() + "!"), false);
            }
        }
    }

    @Override
    public @Nullable AnimalEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null; // Guard dogs do not breed
    }

    // ── Sounds ───────────────────────────────────────────────────────────────

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WOLF_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) {
        return SoundEvents.ENTITY_WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WOLF_DEATH;
    }
}
