package com.mindcraftmod.entity;

import com.mindcraftmod.block.ModBlocks;
import com.mindcraftmod.item.ModItems;
import com.mindcraftmod.item.SignalFlareItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Signal Flare Projectile — carries a SignalFlareItem.Type and triggers world events on impact.
 *
 * - RED:   Spawns a 9×9 patch of Gas Cloud blocks at the landing site
 * - GREEN: Drops a Supply Crate item from 20 blocks above the landing site
 * - GRAY:  Places a 5×5 patch of Smoke Screen blocks at impact
 *
 * The full Phase-6 world-event scheduler will hook into these impacts to add
 * server-wide broadcasts, Artillery Barrage events, and Reinforcements. For now,
 * the blocks/items are placed directly.
 */
public class SignalFlareProjectile extends ThrownItemEntity {

    private SignalFlareItem.Type flareType = SignalFlareItem.Type.GRAY;

    public SignalFlareProjectile(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public SignalFlareProjectile(EntityType<? extends ThrownItemEntity> entityType,
                                  LivingEntity owner, World world,
                                  SignalFlareItem.Type type) {
        super(entityType, owner, world, new ItemStack(ModItems.SIGNAL_FLARE_GRAY));
        this.flareType = type;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SIGNAL_FLARE_GRAY;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (this.getWorld().isClient) return;

        BlockPos impact = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
        World world = this.getWorld();

        switch (flareType) {
            case RED -> triggerGasAttack(world, impact);
            case GREEN -> triggerSupplyDrop(world, impact);
            case GRAY -> triggerSmoke(world, impact);
        }
        this.discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Flares pass through entities
    }

    // ── Event implementations ───────────────────────────────────────────────

    /** RED — 9x9 Gas Cloud patch, 2 blocks tall. */
    private void triggerGasAttack(World world, BlockPos center) {
        BlockState gas = ModBlocks.GAS_CLOUD.getDefaultState();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                BlockPos pos = center.add(dx, 0, dz);
                if (world.getBlockState(pos).isAir()) world.setBlockState(pos, gas);
                BlockPos above = pos.up();
                if (world.getBlockState(above).isAir()) world.setBlockState(above, gas);
            }
        }
    }

    /** GREEN — Drop a Supply Crate item from 20 blocks above. */
    private void triggerSupplyDrop(World world, BlockPos center) {
        ItemStack crate = new ItemStack(ModBlocks.SUPPLY_CRATE.asItem());
        ItemEntity drop = new ItemEntity(world,
                center.getX() + 0.5, center.getY() + 20, center.getZ() + 0.5,
                crate);
        drop.setVelocity(0, 0, 0);
        world.spawnEntity(drop);
    }

    /** GRAY — 5x5 Smoke Screen patch. */
    private void triggerSmoke(World world, BlockPos center) {
        BlockState smoke = ModBlocks.SMOKE_SCREEN.getDefaultState();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos pos = center.add(dx, 0, dz);
                if (world.getBlockState(pos).isAir()) world.setBlockState(pos, smoke);
                BlockPos above = pos.up();
                if (world.getBlockState(above).isAir()) world.setBlockState(above, smoke);
            }
        }
    }
}
