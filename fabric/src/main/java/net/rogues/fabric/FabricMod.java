package net.rogues.fabric;

import net.fabricmc.api.ModInitializer;

import net.rogues.RoguesMod;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Run our common setup.
        RoguesMod.init();
        RoguesMod.registerSounds();
        RoguesMod.registerItems();
        RoguesMod.registerEffects();
        RoguesMod.registerPOI();
        RoguesMod.registerVillagers();
    }
}
