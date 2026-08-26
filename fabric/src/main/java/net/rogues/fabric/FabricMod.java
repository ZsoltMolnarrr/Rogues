package net.rogues.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
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

        // Villager POI + trades — Fabric API registration (loader-specific; NeoForge does its own).
        PointOfInterestHelper.register(RogueVillagers.POI_ID,
                RogueVillagers.POI_TICKET_COUNT, RogueVillagers.POI_SEARCH_DISTANCE,
                RogueVillagers.poiBlockStates());
        RoguesMod.registerVillagers(); // registers the profession + builds RogueVillagers.TRADES
        RogueVillagers.TRADES.forEach((tier, factories) ->
                TradeOfferHelper.registerVillagerOffers(RogueVillagers.PROFESSION, tier,
                        list -> list.addAll(factories)));

        // Custom blocks into the Rogues creative tab — Fabric API.
        ItemGroupEvents.modifyEntriesEvent(Group.KEY).register(content -> {
            for (var entry : CustomBlocks.all) {
                content.accept(entry.item());
            }
        });
    }
}
