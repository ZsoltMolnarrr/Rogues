package net.rogues.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.rogues.RoguesMod;
import net.rogues.block.CustomBlocks;
import net.rogues.item.Group;
import net.rogues.village.RogueVillagers;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Run our common setup.
        RoguesMod.init();
        RoguesMod.registerSounds();
        RoguesMod.registerItems();
        RoguesMod.registerEffects();
        RoguesMod.registerEntities();

        // Villager POI — Fabric API registration (loader-specific; NeoForge does its own).
        // 26.1: `PointOfInterestHelper` was renamed `PoiHelper` (same signature).
        PoiHelper.register(RogueVillagers.POI_ID,
                RogueVillagers.POI_TICKET_COUNT, RogueVillagers.POI_SEARCH_DISTANCE,
                RogueVillagers.poiBlockStates());
        // Registers the profession; its trades are data-driven (`data/rogues/{villager_trade,trade_set}`).
        RoguesMod.registerVillagers();

        // Custom blocks into the Rogues creative tab — Fabric API.
        CreativeModeTabEvents.modifyOutputEvent(Group.KEY).register(content -> {
            for (var entry : CustomBlocks.all) {
                content.accept(entry.item());
            }
        });
    }
}
