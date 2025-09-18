package net.rogues.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.rogues.client.RoguesClientMod;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RoguesClientMod.init();
    }
}
