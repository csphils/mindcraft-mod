package com.mindcraftmod.entity;

import com.mindcraftmod.MindcraftMod;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Central registry for all mod entity types.
 *
 * Renderer registration happens client-side in MindcraftModClient.
 * Spawn conditions registered in ModWorldGen.
 */
public class ModEntities {

    // ── Passive ─────────────────────────────────────────────────────────────
    public static final EntityType<WarHorseEntity> WAR_HORSE =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "war_horse"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WarHorseEntity::new)
                            .dimensions(EntityDimensions.fixed(1.4f, 1.8f))
                            .build());

    public static final EntityType<CarrierPigeonEntity> CARRIER_PIGEON =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "carrier_pigeon"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CarrierPigeonEntity::new)
                            .dimensions(EntityDimensions.fixed(0.3f, 0.3f))
                            .build());

    public static final EntityType<TrenchRatEntity> TRENCH_RAT =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "trench_rat"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TrenchRatEntity::new)
                            .dimensions(EntityDimensions.fixed(0.4f, 0.25f))
                            .build());

    // ── Neutral ─────────────────────────────────────────────────────────────
    public static final EntityType<GuardDogEntity> GUARD_DOG =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "guard_dog"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, GuardDogEntity::new)
                            .dimensions(EntityDimensions.fixed(0.6f, 0.85f))
                            .build());

    // ── Hostile ─────────────────────────────────────────────────────────────
    public static final EntityType<TrenchSoldierEntity> TRENCH_SOLDIER =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "trench_soldier"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, TrenchSoldierEntity::new)
                            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                            .build());

    public static final EntityType<SniperEntity> SNIPER =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "sniper"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SniperEntity::new)
                            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                            .build());

    public static final EntityType<GasGrenadierEntity> GAS_GRENADIER =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "gas_grenadier"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, GasGrenadierEntity::new)
                            .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                            .build());

    // ── Projectiles ─────────────────────────────────────────────────────────
    public static final EntityType<TrenchRifleProjectile> TRENCH_RIFLE_PROJECTILE =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "trench_rifle_projectile"),
                    FabricEntityTypeBuilder.<TrenchRifleProjectile>create(SpawnGroup.MISC,
                                    TrenchRifleProjectile::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .build());

    public static final EntityType<GasCanisteProjectile> GAS_CANISTE_PROJECTILE =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "gas_caniste_projectile"),
                    FabricEntityTypeBuilder.<GasCanisteProjectile>create(SpawnGroup.MISC,
                                    GasCanisteProjectile::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .build());

    // ── Phase-3 throwables ───────────────────────────────────────────────────

    public static final EntityType<GrenadeEntity> GRENADE =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "grenade"),
                    FabricEntityTypeBuilder.<GrenadeEntity>create(SpawnGroup.MISC,
                                    GrenadeEntity::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .build());

    public static final EntityType<SignalFlareProjectile> SIGNAL_FLARE_PROJECTILE =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "signal_flare_projectile"),
                    FabricEntityTypeBuilder.<SignalFlareProjectile>create(SpawnGroup.MISC,
                                    SignalFlareProjectile::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .build());

    public static final EntityType<MudBallProjectile> MUD_BALL_PROJECTILE =
            Registry.register(Registries.ENTITY_TYPE,
                    Identifier.of(MindcraftMod.MOD_ID, "mud_ball_projectile"),
                    FabricEntityTypeBuilder.<MudBallProjectile>create(SpawnGroup.MISC,
                                    MudBallProjectile::new)
                            .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                            .build());

    public static void register() {
        // All registration happens in field initializers above.
        // This method exists so MindcraftMod.java can trigger class loading.
        MindcraftMod.LOGGER.info("Entities registered.");
    }
}
