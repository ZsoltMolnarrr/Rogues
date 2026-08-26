package net.rogues.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.rogues.RoguesMod;
import net.rogues.entity.BearTrapEntity;

public class BearTrapEntityRenderer<T extends BearTrapEntity> extends EntityRenderer<T, BearTrapEntityRenderer.State> {
    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "textures/entity/bear_trap.png");

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

    public BearTrapEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BearTrapEntityModel(context.bakeLayer(BearTrapEntityModel.LAYER));
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T entity, State state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.yaw = entity.getYRot();
        state.sprung = entity.isSprung();
        var cloudData = entity.getCloudData();
        state.despawnTicks = cloudData != null ? cloudData.despawn_ticks : 0;
        state.spawnAnimationState.copyFrom(entity.spawnAnimationState);
        state.idleAnimationState.copyFrom(entity.idleAnimationState);
        state.despawnAnimationState.copyFrom(entity.despawnAnimationState);
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        super.submit(state, matrices, queue, cameraState);
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(-state.yaw + 180F));
        // Standard entity-model space: y-down and x-mirrored, ground plane at y = 1.5
        matrices.scale(-1F, -1F, 1F);
        matrices.translate(0, -1.5, 0);
        queue.submitModel(model, state, matrices, model.renderType(TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        matrices.popPose();
    }
}
