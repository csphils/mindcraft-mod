package com.mindcraftmod.entity;

import com.mindcraftmod.MindcraftMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
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
            register("war_horse", SpawnGroup.CREATURE, WarHorseEntity::new, 1.4f, 1.8f);

    public static final EntityType<CarrierPigeonEntity> CARRIER_PIGEON =
            register("carrier_pigeon", SpawnGroup.CREATURE, CarrierPigeonEntity::new, 0.3f, 0.3f);

    public static final EntityType<TrenchRatEntity> TRENCH_RAT =
            register("trench_rat", SpawnGroup.CREATURE, TrenchRatEntity::new, 0.4f, 0.25f);

    // ── Neutral ─────────────────────────────────────────────────────────────
    public static final EntityType<GuardDogEntity> GUARD_DOG =
            register("guard_dog", SpawnGroup.CREATURE, GuardDogEntity::new, 0.6f, 0.85f);

    // ── Hostile ─────────────────────────────────────────────────────────────
    public static final EntityType<TrenchSoldierEntity> TRENCH_SOLDIER =
            register("trench_soldier", SpawnGroup.MONSTER, TrenchSoldierEntity::new, 0.6f, 1.95f);

    public static final EntityType<SniperEntity> SNIPER =
            register("sniper", SpawnGroup.MONSTER, SniperEntity::new, 0.6f, 1.95f);

    public static final EntityType<GasGrenadierEntity> GAS_GRENADIER =
            register("gas_grenadier", SpawnGroup.MONSTER, GasGrenadierEntity::new, 0.6f, 1.95f);

    // ── Projectiles ─────────────────────────────────────────────────────────
    public static final EntityType<TrenchRifleProjectile> TRENCH_RIFLE_PROJECTILE =
            register("trench_rifle_projectile", SpawnGroup.MISC, TrenchRifleProjectile::new, 0.25f, 0.25f);

    public static final EntityType<GasCanisteProjectile> GAS_CANISTE_PROJECTILE =
            register("gas_caniste_projectile", SpawnGroup.MISC, GasCanisteProjectile::new, 0.25f, 0.25f);

    public static final EntityType<GrenadeEntity> GRENADE =
            register("grenade", SpawnGroup.MISC, GrenadeEntity::new, 0.25f, 0.25f);

    public static final EntityType<SignalFlareProjectile> SIGNAL_FLARE_PROJECTILE =
            register("signal_flare_projectile", SpawnGroup.MISC, SignalFlareProjectile::new, 0.25f, 0.25f);

    public static final EntityType<MudBallProjectile> MUD_BALL_PROJECTILE =
            register("mud_ball_projectile", SpawnGroup.MISC, MudBallProjectile::new, 0.25f, 0.25f);

    private static <T extends Entity> EntityType<T> register(
            String id, SpawnGroup group, EntityType.EntityFactory<T> factory,
            float width, float height) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(
                RegistryKeys.ENTITY_TYPE, Identifier.of(MindcraftMod.MOD_ID, id));
        return Registry.register(Registries.ENTITY_TYPE, key,
                EntityType.Builder.create(factory, group)
                        .dimensions(width, height)
                        .build(key));
    }

    public static void register() {
        // All registration happens in field initializers above.
        // This method exists so MindcraftMod.java can trigger class loading.
        MindcraftMod.LOGGER.info("Entities registered.");
    }
}
