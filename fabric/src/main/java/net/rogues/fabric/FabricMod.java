package net.rogues.fabric;

import net.fabricmc.api.ModInitializer;

import net.rogues.RoguesMod;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        RoguesMod.init();
    }
}
