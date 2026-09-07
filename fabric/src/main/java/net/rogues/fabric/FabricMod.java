package net.rogues.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.rogues.RoguesMod;
import net.rogues.village.RogueVillagers;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Run our common setup (also queues the vanilla-village structure injection).
        RoguesMod.init();
        RoguesMod.registerSounds();
        RoguesMod.registerBlocks();
        RoguesMod.registerItems();
        RoguesMod.registerEffects();
        RoguesMod.registerEntities();

        // Villager POI + trades — Fabric API registration (loader-specific; Forge does its own).
        PointOfInterestHelper.register(RogueVillagers.POI_ID,
                RogueVillagers.POI_TICKET_COUNT, RogueVillagers.POI_SEARCH_DISTANCE,
                RogueVillagers.poiBlockStates());
        RoguesMod.registerVillagers(); // registers the profession + builds RogueVillagers.TRADES
        RogueVillagers.TRADES.forEach((tier, factories) ->
                TradeOfferHelper.registerVillagerOffers(RogueVillagers.PROFESSION, tier,
                        list -> list.addAll(factories)));

        // Creative-tab placement for the custom blocks is loader-neutral — RoguesMod.registerItems()
        // installs it through SpellEngine's PlatformEvents.onItemGroupModify, ahead of the weapon/armor
        // listeners, so the blocks come first in the tab on both loaders.
    }
}
