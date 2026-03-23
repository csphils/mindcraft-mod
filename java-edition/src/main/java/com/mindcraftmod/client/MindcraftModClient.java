package com.mindcraftmod.client;

import com.mindcraftmod.client.renderer.WW1MobRenderer;
import com.mindcraftmod.entity.ModEntities;
import com.mindcraftmod.network.FactionSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.projectile.ThrownItemEntityRenderer;
import net.minecraft.util.Identifier;

/**
 * Client-side entrypoint.
 * Handles: entity renderers, HUD overlay, server→client networking.
 *
 * Entity renderer strategy (Sprint 8 — placeholder):
 *   All mobs use WW1MobRenderer<T>, which wraps the vanilla zombie biped model and
 *   substitutes a vanilla texture appropriate to each mob's shape/role.
 *   Replace in Sprint 9 (Art Pass) with custom CuboidEntityModel subclasses.
 *
 * All projectile types extend ThrownItemEntity so ThrownItemEntityRenderer works
 * out of the box — it renders the item returned by getDefaultItem().
 */
@Environment(EnvType.CLIENT)
public class MindcraftModClient implements ClientModInitializer {

    // ── Vanilla stub texture identifiers (all live in minecraft.jar) ──────────

    private static final Identifier ZOMBIE   = Identifier.of("minecraft", "textures/entity/zombie/zombie.png");
    private static final Identifier SKELETON = Identifier.of("minecraft", "textures/entity/skeleton/skeleton.png");
    private static final Identifier WOLF     = Identifier.of("minecraft", "textures/entity/wolf/wolf.png");
    private static final Identifier HORSE    = Identifier.of("minecraft", "textures/entity/horse/horse_white.png");
    private static final Identifier PARROT   = Identifier.of("minecraft", "textures/entity/parrot/parrot_blue.png");
    private static final Identifier BAT      = Identifier.of("minecraft", "textures/entity/bat.png");

    @Override
    public void onInitializeClient() {
        registerEntityRenderers();
        registerNetworkHandlers();
        HudOverlay.register();
    }

    // ── Entity renderers ──────────────────────────────────────────────────────

    private void registerEntityRenderers() {
        // Hostile humanoids — zombie biped shape + appropriate stub texture
        EntityRendererRegistry.register(ModEntities.TRENCH_SOLDIER,
                ctx -> new WW1MobRenderer<>(ctx, ZOMBIE));
        EntityRendererRegistry.register(ModEntities.SNIPER,
                ctx -> new WW1MobRenderer<>(ctx, SKELETON));
        EntityRendererRegistry.register(ModEntities.GAS_GRENADIER,
                ctx -> new WW1MobRenderer<>(ctx, ZOMBIE));

        // Neutral / tameable / passive — closest vanilla shape
        EntityRendererRegistry.register(ModEntities.GUARD_DOG,
                ctx -> new WW1MobRenderer<>(ctx, WOLF));
        EntityRendererRegistry.register(ModEntities.WAR_HORSE,
                ctx -> new WW1MobRenderer<>(ctx, HORSE));
        EntityRendererRegistry.register(ModEntities.CARRIER_PIGEON,
                ctx -> new WW1MobRenderer<>(ctx, PARROT));
        EntityRendererRegistry.register(ModEntities.TRENCH_RAT,
                ctx -> new WW1MobRenderer<>(ctx, BAT));

        // Projectiles — ThrownItemEntityRenderer renders the held item model
        EntityRendererRegistry.register(ModEntities.TRENCH_RIFLE_PROJECTILE,
                ctx -> new ThrownItemEntityRenderer<>(ctx));
        EntityRendererRegistry.register(ModEntities.GAS_CANISTE_PROJECTILE,
                ctx -> new ThrownItemEntityRenderer<>(ctx));
        EntityRendererRegistry.register(ModEntities.GRENADE,
                ctx -> new ThrownItemEntityRenderer<>(ctx));
        EntityRendererRegistry.register(ModEntities.SIGNAL_FLARE_PROJECTILE,
                ctx -> new ThrownItemEntityRenderer<>(ctx));
        EntityRendererRegistry.register(ModEntities.MUD_BALL_PROJECTILE,
                ctx -> new ThrownItemEntityRenderer<>(ctx));
    }

    // ── Networking ────────────────────────────────────────────────────────────

    private void registerNetworkHandlers() {
        // Receive faction sync from server; update HUD on the render thread
        ClientPlayNetworking.registerGlobalReceiver(FactionSyncPayload.ID,
                (payload, context) ->
                        context.client().execute(() ->
                                HudOverlay.setFaction(payload.factionName())));
    }
}
