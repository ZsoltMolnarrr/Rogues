package net.rogues.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.rogues.client.RoguesClient;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RoguesClient.init();
    }
}
