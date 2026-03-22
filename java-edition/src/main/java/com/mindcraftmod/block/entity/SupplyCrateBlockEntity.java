package com.mindcraftmod.block.entity;

import com.mindcraftmod.MindcraftMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Block entity for the Supply Crate — a 27-slot chest that auto-populates
 * from a loot table on first open (standard Minecraft loot chest pattern).
 *
 * Loot table: data/mindcraftmod/loot_tables/supply_crate.json
 */
public class SupplyCrateBlockEntity extends LootableContainerBlockEntity {

    private static final RegistryKey<net.minecraft.loot.LootTable> LOOT_TABLE_KEY =
            RegistryKey.of(RegistryKeys.LOOT_TABLE,
                    Identifier.of(MindcraftMod.MOD_ID, "supply_crate"));

    private DefaultedList<ItemStack> inventory =
            DefaultedList.ofSize(27, ItemStack.EMPTY);

    public SupplyCrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SUPPLY_CRATE, pos, state);
    }

    // ── LootableContainerBlockEntity ─────────────────────────────────────────

    @Override
    protected RegistryKey<net.minecraft.loot.LootTable> getLootTableKey() {
        return LOOT_TABLE_KEY;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    protected int getInvSize() {
        return 27;
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.mindcraftmod.supply_crate");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    // ── NBT serialization ────────────────────────────────────────────────────

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (!hasLootTable()) {
            Inventories.readNbt(nbt, inventory, getWorld() != null
                    ? getWorld().getRegistryManager()
                    : null);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!hasLootTable()) {
            Inventories.writeNbt(nbt, inventory, false);
        }
    }
}
