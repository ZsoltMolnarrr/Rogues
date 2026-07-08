package net.rogues.client.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.rogues.RoguesMod;
import net.rogues.entity.BearTrapEntity;

public class BearTrapEntityRenderer<T extends BearTrapEntity> extends EntityRenderer<T> {
    public static final Identifier TEXTURE =
            Identifier.of(RoguesMod.NAMESPACE, "textures/entity/bear_trap.png");

    private final BearTrapEntityModel model;

    public BearTrapEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new BearTrapEntityModel(context.getPart(BearTrapEntityModel.LAYER));
    }

    @Override
    public Identifier getTexture(T entity) {
        return TEXTURE;
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getYaw() + 180F));
        // Standard entity-model space: y-down and x-mirrored, ground plane at y = 1.5
        matrices.scale(-1F, -1F, 1F);
        matrices.translate(0, -1.5, 0);
        model.setAngles(entity, 0F, 0F, entity.age + tickDelta, 0F, 0F);
        var vertices = vertexConsumers.getBuffer(model.getLayer(TEXTURE));
        model.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV, -1);
        matrices.pop();
    }
}
