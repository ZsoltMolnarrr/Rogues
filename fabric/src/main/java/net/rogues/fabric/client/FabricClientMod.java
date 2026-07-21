package net.rogues.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.rogues.block.CustomBlocks;
import net.rogues.client.RoguesClientMod;
import net.rogues.client.entity.BearTrapEntityModel;
import net.rogues.client.entity.BearTrapEntityRenderer;
import net.rogues.entity.RogueEntities;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RoguesClientMod.init();

        // Entity model layers + renderers (Fabric API)
        EntityModelLayerRegistry.registerModelLayer(BearTrapEntityModel.LAYER, BearTrapEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(RogueEntities.BEAR_TRAP.type, BearTrapEntityRenderer::new);

        // Fabric-specific render layer registration
        BlockRenderLayerMap.INSTANCE.putBlock(CustomBlocks.WORKBENCH.block(), RenderLayer.getCutout());
    }
}
