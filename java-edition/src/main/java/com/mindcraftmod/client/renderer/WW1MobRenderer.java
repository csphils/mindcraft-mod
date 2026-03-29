package com.mindcraftmod.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

/**
 * Placeholder renderer used for all WW1 mob entity types.
 *
 * Renders every mob as a zombie-shaped biped using a swapped vanilla texture, so
 * entities are at least visible in-game while proper custom models are being made.
 *
 * In 1.21.4 the rendering pipeline uses render states (BipedEntityRenderState)
 * to decouple entity data from rendering.
 *
 * @param <T> any MobEntity subtype
 */
@Environment(EnvType.CLIENT)
public class WW1MobRenderer<T extends MobEntity>
        extends BipedEntityRenderer<T, BipedEntityRenderState, BipedEntityModel<BipedEntityRenderState>> {

    private final Identifier texture;

    public WW1MobRenderer(EntityRendererFactory.Context ctx, Identifier texture) {
        super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.5f);
        this.texture = texture;
    }

    @Override
    public BipedEntityRenderState createRenderState() {
        return new BipedEntityRenderState();
    }

    @Override
    public Identifier getTexture(BipedEntityRenderState state) {
        return texture;
    }
}
