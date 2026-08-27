package net.rogues.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.rogues.client.RoguesClientMod;
import net.rogues.client.entity.BearTrapEntityModel;
import net.rogues.client.entity.BearTrapEntityRenderer;
import net.rogues.entity.RogueEntities;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RoguesClientMod.init();

        // Entity model layers + renderers (Fabric API)
        // 26.1: `EntityModelLayerRegistry` was renamed `ModelLayerRegistry` (same method name/shape).
        ModelLayerRegistry.registerModelLayer(BearTrapEntityModel.LAYER, BearTrapEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(RogueEntities.BEAR_TRAP.type, BearTrapEntityRenderer::new);

        // 26.1: `BlockRenderLayerMap` is gone — the chunk section layer is derived from the block model.
    }
}
