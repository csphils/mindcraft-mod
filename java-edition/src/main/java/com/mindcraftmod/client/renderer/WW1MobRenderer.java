package com.mindcraftmod.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BipedEntityModel;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

/**
 * Placeholder renderer used for all WW1 mob entity types.
 *
 * Renders every mob as a zombie-shaped biped using a swapped vanilla texture, so
 * entities are at least visible in-game while proper custom models are being made.
 *
 * Replace in Sprint 9 (Art Pass) when:
 *   - Custom CuboidEntityModel subclasses are ready for each mob type
 *   - Real WW1 16×16 entity textures exist in assets/mindcraftmod/textures/entity/
 *
 * All vanilla stub textures used here live inside minecraft.jar — no new PNG files
 * are required for this renderer to work.
 *
 * @param <T> any MobEntity subtype — covers all hostile NPCs, animals, and tameable mobs
 */
@Environment(EnvType.CLIENT)
public class WW1MobRenderer<T extends MobEntity> extends BipedEntityRenderer<T, BipedEntityModel<T>> {

    private final Identifier texture;

    /**
     * @param ctx     entity renderer factory context (provided by EntityRendererRegistry)
     * @param texture identifier for the vanilla stub texture, e.g.
     *                {@code Identifier.of("minecraft", "textures/entity/zombie/zombie.png")}
     */
    public WW1MobRenderer(EntityRendererFactory.Context ctx, Identifier texture) {
        super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.5f);
        this.texture = texture;
    }

    @Override
    public Identifier getTexture(T entity) {
        return texture;
    }
}
