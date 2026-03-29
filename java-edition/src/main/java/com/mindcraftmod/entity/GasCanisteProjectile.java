package com.mindcraftmod.entity;

import com.mindcraftmod.block.ModBlocks;
import com.mindcraftmod.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Gas Canister Projectile — thrown by GasCanisteItem and GasGrenadierEntity.
 *
 * On block impact, places a 3×3 patch of Gas Cloud blocks at the hit surface.
 * The gas cloud blocks then spread and dissipate on their own schedule.
 *
 * NOTE: Class name follows the typo in ModEntities.java ("GasCanisteProjectile").
 */
public class GasCanisteProjectile extends ThrownItemEntity {

    public GasCanisteProjectile(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public GasCanisteProjectile(EntityType<? extends ThrownItemEntity> entityType,
                                 LivingEntity owner, World world) {
        super(entityType, owner, world, new net.minecraft.item.ItemStack(ModItems.GAS_CANISTER));
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.GAS_CANISTER;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.getWorld().isClient) {
            // Place 3x3 Gas Cloud at and above the impact surface
            BlockPos center = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
            BlockState gas = ModBlocks.GAS_CLOUD.getDefaultState();

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.add(dx, 0, dz);
                    if (this.getWorld().getBlockState(pos).isAir()) {
                        this.getWorld().setBlockState(pos, gas);
                    }
                    // Also fill one block above so the cloud is 2 blocks tall
                    BlockPos above = pos.up();
                    if (this.getWorld().getBlockState(above).isAir()) {
                        this.getWorld().setBlockState(above, gas);
                    }
                }
            }
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // Gas canisters pass through entities and hit the block behind them
    }
}
