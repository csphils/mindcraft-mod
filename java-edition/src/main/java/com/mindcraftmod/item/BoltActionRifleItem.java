package com.mindcraftmod.item;

import com.mindcraftmod.entity.ModEntities;
import com.mindcraftmod.entity.TrenchRifleProjectile;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Bolt-Action Rifle — single-shot ranged weapon.
 *
 * - Damage: 8 per shot (dealt by TrenchRifleProjectile)
 * - Range: unlimited (fast straight projectile)
 * - Reload: 1-second cooldown enforced via item cooldown manager (20 ticks)
 * - Ammo: consumes 1x Rifle Cartridge per shot from inventory
 * - No ammo → click sound, no projectile
 * - Durability: 384 uses (set in ModItems)
 * - Not craftable — found in Supply Crates and Trench Soldier drops
 */
public class BoltActionRifleItem extends Item {

    /** 1 second between shots. */
    private static final int COOLDOWN_TICKS = 20;

    public BoltActionRifleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Client predicts the click sound; server does everything else
        if (world.isClient) {
            return ActionResult.PASS;
        }

        // Check cooldown
        if (player.getItemCooldownManager().isCoolingDown(stack)) {
            return ActionResult.PASS;
        }

        // Try to consume ammo
        boolean hasAmmo = tryConsumeAmmo(player);

        if (!hasAmmo) {
            // Click sound — empty chamber
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.PLAYERS, 0.5f, 2.0f);
            return ActionResult.FAIL;
        }

        // Fire — spawn projectile
        TrenchRifleProjectile projectile =
                new TrenchRifleProjectile(ModEntities.TRENCH_RIFLE_PROJECTILE, player, world);
        projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0f, 3.5f, 0.5f);
        world.spawnEntity(projectile);

        // Gunshot sound
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.8f, 1.8f);

        // Damage the rifle
        stack.damage(1, player, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

        // Apply 1-second cooldown before next shot
        player.getItemCooldownManager().set(stack, COOLDOWN_TICKS);

        return ActionResult.SUCCESS;
    }

    /**
     * Searches the player's inventory for Rifle Cartridges and consumes one.
     * In creative mode, ammo is unlimited.
     *
     * @return true if ammo was consumed (or player is in creative mode)
     */
    private boolean tryConsumeAmmo(PlayerEntity player) {
        if (player.getAbilities().creativeMode) return true;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack slot = player.getInventory().getStack(i);
            if (slot.isOf(ModItems.RIFLE_CARTRIDGE)) {
                slot.decrement(1);
                return true;
            }
        }
        return false;
    }

    /**
     * Maps the hand to the appropriate equipment slot for durability damage.
     */
    private net.minecraft.entity.EquipmentSlot LivingEntityEquipmentSlot(Hand hand) {
        return hand == Hand.MAIN_HAND
                ? net.minecraft.entity.EquipmentSlot.MAINHAND
                : net.minecraft.entity.EquipmentSlot.OFFHAND;
    }
}
