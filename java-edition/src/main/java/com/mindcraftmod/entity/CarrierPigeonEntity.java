package com.mindcraftmod.entity;

import com.mindcraftmod.world.FactionManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Carrier Pigeon — small passive bird that can deliver written books to faction members.
 *
 * Behaviour:
 * - Flies using BirdNavigation; wanders overhead
 * - Right-click with a Written Book → pigeon enters delivery mode, flies to the
 *   nearest online same-faction player and delivers the book
 * - If no faction-matched player online: drops the book at current position
 * - Drops 0-1 Feather on death
 */
public class CarrierPigeonEntity extends AnimalEntity {

    /** Whether this pigeon is currently carrying a delivery. */
    private @Nullable ItemStack deliveryStack = null;
    /** Target player UUID for delivery. Resolved server-side. */
    private @Nullable java.util.UUID deliveryTarget = null;

    public CarrierPigeonEntity(EntityType<? extends CarrierPigeonEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.MAX_HEALTH, 6.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.FLYING_SPEED, 0.6)
                .add(EntityAttributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation nav = new BirdNavigation(this, world);
        nav.setCanPathThroughDoors(false);
        return nav;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(6, new LookAroundGoal(this));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    // ── Delivery mechanic ────────────────────────────────────────────────────

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient) return ActionResult.SUCCESS;

        ItemStack held = player.getStackInHand(hand);
        if (held.isOf(Items.WRITTEN_BOOK) && deliveryStack == null) {
            // Accept the book for delivery
            deliveryStack = held.copy();
            if (!player.getAbilities().creativeMode) held.decrement(1);

            // Find a same-faction player online to deliver to
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            FactionManager factions = FactionManager.get(serverWorld.getServer());
            FactionManager.Faction senderFaction = factions.getFaction(player.getUuid());

            ServerPlayerEntity target = null;
            double nearest = Double.MAX_VALUE;
            for (ServerPlayerEntity candidate : serverWorld.getServer().getPlayerManager().getPlayerList()) {
                if (candidate == player) continue;
                if (factions.getFaction(candidate.getUuid()) == senderFaction
                        && senderFaction != FactionManager.Faction.NONE) {
                    double dist = this.squaredDistanceTo(candidate);
                    if (dist < nearest) {
                        nearest = dist;
                        target = candidate;
                    }
                }
            }

            if (target != null) {
                deliveryTarget = target.getUuid();
                player.sendMessage(Text.literal("The pigeon takes the book and flies toward " + target.getName().getString() + "!"), true);
            } else {
                player.sendMessage(Text.literal("No faction-mate online — the pigeon drops the book nearby."), true);
                this.dropStack(serverWorld, deliveryStack);
                deliveryStack = null;
            }
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && deliveryStack != null && deliveryTarget != null) {
            ServerWorld world = (ServerWorld) this.getWorld();
            ServerPlayerEntity target = world.getServer().getPlayerManager().getPlayer(deliveryTarget);
            if (target != null) {
                // Move toward target
                this.getNavigation().startMovingTo(target, 1.5);
                // Deliver when close
                if (this.squaredDistanceTo(target) < 4.0) {
                    target.giveItemStack(deliveryStack.copy());
                    target.sendMessage(Text.literal("A carrier pigeon delivered a book from a faction-mate!"), false);
                    deliveryStack = null;
                    deliveryTarget = null;
                }
            } else {
                // Target logged off — drop book
                this.dropStack(world, deliveryStack);
                deliveryStack = null;
                deliveryTarget = null;
            }
        }
    }

    // ── Sounds ──────────────────────────────────────────────────────────────

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PARROT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) {
        return SoundEvents.ENTITY_PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PARROT_DEATH;
    }

    @Override
    public @Nullable AnimalEntity createChild(ServerWorld world, PassiveEntity entity) {
        return new CarrierPigeonEntity(ModEntities.CARRIER_PIGEON, world);
    }
}
