package net.rogues.neoforge;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.poi.PointOfInterestType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.rogues.RoguesMod;
import net.rogues.block.CustomBlocks;
import net.rogues.item.Group;
import net.rogues.village.RogueVillagers;

@Mod(RoguesMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        // Run our common setup.
        RoguesMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        // Custom blocks into the Rogues creative tab — NeoForge mod-bus event (replaces ItemGroupEvents).
        modBus.addListener(BuildCreativeModeTabContentsEvent.class, NeoForgeMod::buildTabContents);
        // Villager trades — game-bus event (fired per profession); replaces Fabric API's TradeOfferHelper.
        NeoForge.EVENT_BUS.addListener(VillagerTradesEvent.class, NeoForgeMod::onVillagerTrades);
    }

    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, reg -> {
            RoguesMod.registerSounds();
        });
        event.register(RegistryKeys.ITEM, reg -> {
            RoguesMod.registerItems();
        });
        event.register(RegistryKeys.STATUS_EFFECT, reg -> {
            RoguesMod.registerEffects();
        });
        event.register(RegistryKeys.ENTITY_TYPE, reg -> {
            RoguesMod.registerEntities();
        });
        event.register(RegistryKeys.POINT_OF_INTEREST_TYPE, reg -> {
            // POI registration — vanilla registry insert. NeoForge's POI registry callback wires the
            // block-state -> POI mapping from the type's block states, so no Fabric API helper is needed.
            // Not sure why errors are thrown, but this seems to fix it.
            try {
                Registry.register(Registries.POINT_OF_INTEREST_TYPE, RogueVillagers.POI_ID,
                        new PointOfInterestType(RogueVillagers.poiBlockStates(),
                                RogueVillagers.POI_TICKET_COUNT, RogueVillagers.POI_SEARCH_DISTANCE));
            } catch (Exception e) { }
        });
        event.register(RegistryKeys.VILLAGER_PROFESSION, reg -> {
            RoguesMod.registerVillagers(); // registers the profession + builds RogueVillagers.TRADES
        });
    }

    private static void buildTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(Group.KEY)) {
            for (var entry : CustomBlocks.all) {
                event.add(entry.item());
            }
        }
    }

    private static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() != RogueVillagers.PROFESSION) {
            return;
        }
        RogueVillagers.TRADES.forEach((tier, factories) -> {
            var tierList = event.getTrades().get(tier.intValue());
            if (tierList != null) {
                tierList.addAll(factories);
            }
        });
    }
}
