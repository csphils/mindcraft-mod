package com.mindcraftmod.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

/**
 * Registers default attribute containers for all mod entities.
 *
 * Must be called AFTER ModEntities.register() in MindcraftMod.onInitialize().
 * Without this, entities will crash when spawned because Minecraft cannot
 * find their attribute container.
 */
public class ModEntityAttributes {

    public static void register() {
        // Passive
        FabricDefaultAttributeRegistry.register(ModEntities.WAR_HORSE,       WarHorseEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.CARRIER_PIGEON,  CarrierPigeonEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.TRENCH_RAT,      TrenchRatEntity.createAttributes());

        // Neutral / tameable
        FabricDefaultAttributeRegistry.register(ModEntities.GUARD_DOG,       GuardDogEntity.createAttributes());

        // Hostile
        FabricDefaultAttributeRegistry.register(ModEntities.TRENCH_SOLDIER,  TrenchSoldierEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.SNIPER,          SniperEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.GAS_GRENADIER,   GasGrenadierEntity.createAttributes());

        // Projectiles do not need attribute registration
    }
}
