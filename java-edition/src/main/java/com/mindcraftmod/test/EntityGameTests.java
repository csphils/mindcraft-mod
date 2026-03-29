package com.mindcraftmod.test;

import com.mindcraftmod.MindcraftMod;
import com.mindcraftmod.entity.ModEntities;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

/**
 * Fabric GameTest suite — in-game automated tests for mod entity attributes.
 *
 * These 1-tick tests confirm that ModEntityAttributes registered the correct
 * attribute containers for each hostile mob. Without correct registration,
 * entities crash on spawn with a missing attribute container error.
 *
 * Run via: ./gradlew runGametest  OR  /test runAll in-game.
 */
public class EntityGameTests {

    private static final String NAMESPACE = MindcraftMod.MOD_ID;
    private static final String ARENA_3X3 = NAMESPACE + ":empty_3x3";

    // ════════════════════════════════════════════════════════════════════════
    // TrenchSoldierEntity
    // ════════════════════════════════════════════════════════════════════════

    /**
     * TrenchSoldier has correct attribute values: MAX_HEALTH=20, FOLLOW_RANGE=20,
     * ATTACK_DAMAGE=3.
     */
    @GameTest(templateName = ARENA_3X3)
    public void trenchSoldierAttributes(TestContext ctx) {
        var soldier = ctx.spawnEntity(ModEntities.TRENCH_SOLDIER, 1, 1, 1);

        ctx.assertTrue(
                soldier.getAttributeValue(EntityAttributes.MAX_HEALTH) == 20.0,
                "TrenchSoldier MAX_HEALTH should be 20"
        );
        ctx.assertTrue(
                soldier.getAttributeValue(EntityAttributes.FOLLOW_RANGE) == 20.0,
                "TrenchSoldier FOLLOW_RANGE should be 20"
        );
        ctx.assertTrue(
                soldier.getAttributeValue(EntityAttributes.ATTACK_DAMAGE) == 3.0,
                "TrenchSoldier ATTACK_DAMAGE should be 3"
        );
        ctx.complete();
    }

    // ════════════════════════════════════════════════════════════════════════
    // SniperEntity
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Sniper has correct attribute values: MAX_HEALTH=20, FOLLOW_RANGE=30 (DETECTION_RANGE),
     * MOVEMENT_SPEED=0 (stationary), ATTACK_DAMAGE=8.
     */
    @GameTest(templateName = ARENA_3X3)
    public void sniperAttributes(TestContext ctx) {
        var sniper = ctx.spawnEntity(ModEntities.SNIPER, 1, 1, 1);

        ctx.assertTrue(
                sniper.getAttributeValue(EntityAttributes.MAX_HEALTH) == 20.0,
                "Sniper MAX_HEALTH should be 20"
        );
        ctx.assertTrue(
                sniper.getAttributeValue(EntityAttributes.FOLLOW_RANGE) == 30.0,
                "Sniper FOLLOW_RANGE should be 30 (DETECTION_RANGE)"
        );
        ctx.assertTrue(
                sniper.getAttributeValue(EntityAttributes.MOVEMENT_SPEED) == 0.0,
                "Sniper MOVEMENT_SPEED should be 0 (stationary)"
        );
        ctx.assertTrue(
                sniper.getAttributeValue(EntityAttributes.ATTACK_DAMAGE) == 8.0,
                "Sniper ATTACK_DAMAGE should be 8"
        );
        ctx.complete();
    }

    // ════════════════════════════════════════════════════════════════════════
    // GasGrenadierEntity
    // ════════════════════════════════════════════════════════════════════════

    /**
     * GasGrenadier has correct attribute values: MAX_HEALTH=18, FOLLOW_RANGE=12 (THROW_RANGE),
     * ATTACK_DAMAGE=2.
     */
    @GameTest(templateName = ARENA_3X3)
    public void gasGrenadierAttributes(TestContext ctx) {
        var grenadier = ctx.spawnEntity(ModEntities.GAS_GRENADIER, 1, 1, 1);

        ctx.assertTrue(
                grenadier.getAttributeValue(EntityAttributes.MAX_HEALTH) == 18.0,
                "GasGrenadier MAX_HEALTH should be 18"
        );
        ctx.assertTrue(
                grenadier.getAttributeValue(EntityAttributes.FOLLOW_RANGE) == 12.0,
                "GasGrenadier FOLLOW_RANGE should be 12 (THROW_RANGE)"
        );
        ctx.assertTrue(
                grenadier.getAttributeValue(EntityAttributes.ATTACK_DAMAGE) == 2.0,
                "GasGrenadier ATTACK_DAMAGE should be 2"
        );
        ctx.complete();
    }

    // ════════════════════════════════════════════════════════════════════════
    // WarHorseEntity
    // ════════════════════════════════════════════════════════════════════════

    /**
     * WarHorse has correct attribute values: MAX_HEALTH=30, FOLLOW_RANGE=16, ARMOR=2.
     */
    @GameTest(templateName = ARENA_3X3)
    public void warHorseAttributes(TestContext ctx) {
        var horse = ctx.spawnEntity(ModEntities.WAR_HORSE, 1, 1, 1);

        ctx.assertTrue(
                horse.getAttributeValue(EntityAttributes.MAX_HEALTH) == 30.0,
                "WarHorse MAX_HEALTH should be 30"
        );
        ctx.assertTrue(
                horse.getAttributeValue(EntityAttributes.FOLLOW_RANGE) == 16.0,
                "WarHorse FOLLOW_RANGE should be 16"
        );
        ctx.assertTrue(
                horse.getAttributeValue(EntityAttributes.ARMOR) == 2.0,
                "WarHorse ARMOR should be 2 (base, without plate)"
        );
        ctx.complete();
    }
}
