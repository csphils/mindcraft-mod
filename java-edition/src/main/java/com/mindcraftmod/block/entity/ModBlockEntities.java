package com.mindcraftmod.block.entity;

import com.mindcraftmod.MindcraftMod;
import com.mindcraftmod.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Registry for all mod block entity types.
 * Must be registered AFTER ModBlocks.register().
 */
public class ModBlockEntities {

    public static final BlockEntityType<SupplyCrateBlockEntity> SUPPLY_CRATE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "supply_crate"),
                    FabricBlockEntityTypeBuilder
                            .create(SupplyCrateBlockEntity::new, ModBlocks.SUPPLY_CRATE)
                            .build());

    public static void register() {
        // Field initializers trigger registration.
        MindcraftMod.LOGGER.info("Block entities registered.");
    }
}
