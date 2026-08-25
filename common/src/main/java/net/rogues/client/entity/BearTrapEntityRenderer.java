package net.rogues.client.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.rogues.RoguesMod;
import net.rogues.entity.BearTrapEntity;

public class BearTrapEntityRenderer<T extends BearTrapEntity> extends EntityRenderer<T, BearTrapEntityRenderer.State> {
    public static final Identifier TEXTURE =
            Identifier.of(RoguesMod.NAMESPACE, "textures/entity/bear_trap.png");

    /// 1.21.11 render states are extracted on the tick thread and consumed on the render thread, so the
    /// entity is never touched from `render`/`setAngles` — everything the model needs is copied here.
    public static class State extends EntityRenderState {
        public float yaw;
        /// Whether the trap wound down because it caught something (drives `attack` over `despawn`).
        public boolean sprung;
        /// The cloud's configured wind-down length, sizing the `despawn` clip. 0 = no cloud data yet.
        public int despawnTicks;
        public final AnimationState spawnAnimationState = new AnimationState();
        public final AnimationState idleAnimationState = new AnimationState();
        public final AnimationState despawnAnimationState = new AnimationState();
    }

    private final BearTrapEntityModel model;

    public BearTrapEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new BearTrapEntityModel(context.getPart(BearTrapEntityModel.LAYER));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void updateRenderState(T entity, State state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.yaw = entity.getYaw();
        state.sprung = entity.isSprung();
        var cloudData = entity.getCloudData();
        state.despawnTicks = cloudData != null ? cloudData.despawn_ticks : 0;
        state.spawnAnimationState.copyFrom(entity.spawnAnimationState);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.despawnAnimationState.copyFrom(entity.despawnAnimationState);
    }

    @Override
    public void render(State state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        super.render(state, matrices, queue, cameraState);
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.yaw + 180F));
        // Standard entity-model space: y-down and x-mirrored, ground plane at y = 1.5
        matrices.scale(-1F, -1F, 1F);
        matrices.translate(0, -1.5, 0);
        queue.submitModel(model, state, matrices, model.getLayer(TEXTURE),
                state.light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);
        matrices.pop();
    }
}
