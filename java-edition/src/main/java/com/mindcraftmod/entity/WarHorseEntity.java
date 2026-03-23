package com.mindcraftmod.entity;

import com.mindcraftmod.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * War Horse — a larger, stronger horse with a cavalry charge mechanic.
 *
 * Behaviour:
 * - Rideable by players (right-click without item)
 * - When ridden and sprinting, damages nearby entities for 10 HP (cavalry charge)
 *   — fires once per sprint burst (debounced with {@code chargeCooldown})
 * - Equippable with Horse Armor Plate (+4 armor): right-click with the item
 * - Drops 0–2 Leather on death; equipped armor is dropped separately
 */
public class WarHorseEntity extends AnimalEntity {

    /** Cooldown between consecutive cavalry charges (in ticks). */
    private static final int CHARGE_COOLDOWN_TICKS = 40;
    /** Radius in blocks for the cavalry charge AoE. */
    private static final double CHARGE_RADIUS = 2.0;

    private int chargeCooldown = 0;
    private boolean armorPlateEquipped = false;

    public WarHorseEntity(EntityType<? extends WarHorseEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 30.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.225)
                .add(EntityAttributes.FOLLOW_RANGE, 16.0)
                .add(EntityAttributes.ARMOR, 2.0); // base armor; +4 with plate
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    // ── Cavalry charge ───────────────────────────────────────────────────────

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (chargeCooldown > 0) chargeCooldown--;

        if (!this.getWorld().isClient
                && this.hasPassengers()
                && this.isSprinting()
                && chargeCooldown == 0) {
            performCavalryCharge();
            chargeCooldown = CHARGE_COOLDOWN_TICKS;
        }
    }

    private void performCavalryCharge() {
        ServerWorld serverWorld = (ServerWorld) this.getWorld();
        DamageSource chargeSource = this.getDamageSources().mobAttack(this);
        for (Entity nearby : this.getWorld().getOtherEntities(this,
                this.getBoundingBox().expand(CHARGE_RADIUS))) {
            if (nearby instanceof LivingEntity living && !(nearby instanceof PlayerEntity rider
                    && this.hasPassenger(rider))) {
                living.damage(serverWorld, chargeSource, 10.0f);
                this.playSound(SoundEvents.ENTITY_HORSE_ANGRY, 1.0f, 1.0f);
            }
        }
    }

    // ── Horse Armor Plate equip ───────────────────────────────────────────────

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient) return ActionResult.SUCCESS;

        ItemStack held = player.getStackInHand(hand);
        if (held.isOf(ModItems.HORSE_ARMOR_PLATE) && !armorPlateEquipped) {
            armorPlateEquipped = true;
            if (!player.getAbilities().creativeMode) held.decrement(1);
            // Add +4 armor on top of base
            this.getAttributeInstance(EntityAttributes.ARMOR)
                    .setBaseValue(6.0); // 2 base + 4 plate
            player.sendMessage(net.minecraft.text.Text.literal("Horse armored up!"), true);
            this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_IRON.value(), 1.0f, 1.0f);
            return ActionResult.SUCCESS;
        }

        // Right-click with empty hand / saddle slot → attempt to ride
        if (held.isEmpty() && !this.hasPassengers()) {
            player.startRiding(this);
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    // ── Travel input (steer while riding) ────────────────────────────────────

    @Override
    public boolean isImmobile() {
        return false;
    }

    protected boolean isControlledByRider() {
        return this.getFirstPassenger() instanceof PlayerEntity;
    }

    // ── Breeding ─────────────────────────────────────────────────────────────

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(net.minecraft.item.Items.GOLDEN_APPLE);
    }

    @Override
    public @Nullable AnimalEntity createChild(ServerWorld world, PassiveEntity entity) {
        return new WarHorseEntity(ModEntities.WAR_HORSE, world);
    }

    // ── Sounds ───────────────────────────────────────────────────────────────

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HORSE_DEATH;
    }
}
