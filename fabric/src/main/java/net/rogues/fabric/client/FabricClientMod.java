package net.rogues.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.rogues.block.CustomBlocks;
import net.rogues.client.RoguesClientMod;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RoguesClientMod.init();

        // Fabric-specific render layer registration
        BlockRenderLayerMap.INSTANCE.putBlock(CustomBlocks.WORKBENCH.block(), RenderLayer.getCutout());
    }
}
