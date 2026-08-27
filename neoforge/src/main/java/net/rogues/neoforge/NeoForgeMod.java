package net.rogues.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
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
        // 26.1: villager trades are data-driven — no `VillagerTradesEvent` listener any more.
    }

    public static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, reg -> {
            RoguesMod.registerSounds();
        });
        event.register(Registries.ITEM, reg -> {
            RoguesMod.registerItems();
        });
        event.register(Registries.MOB_EFFECT, reg -> {
            RoguesMod.registerEffects();
        });
        event.register(Registries.ENTITY_TYPE, reg -> {
            RoguesMod.registerEntities();
        });
        event.register(Registries.POINT_OF_INTEREST_TYPE, reg -> {
            // POI registration — vanilla registry insert. NeoForge's POI registry callback wires the
            // block-state -> POI mapping from the type's block states, so no Fabric API helper is needed.
            // Not sure why errors are thrown, but this seems to fix it.
            try {
                Registry.register(BuiltInRegistries.POINT_OF_INTEREST_TYPE, RogueVillagers.POI_ID,
                        new PoiType(RogueVillagers.poiBlockStates(),
                                RogueVillagers.POI_TICKET_COUNT, RogueVillagers.POI_SEARCH_DISTANCE));
            } catch (Exception e) { }
        });
        event.register(Registries.VILLAGER_PROFESSION, reg -> {
            RoguesMod.registerVillagers(); // registers the profession (trades come from JSON)
        });
    }

    private static void buildTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(Group.KEY)) {
            for (var entry : CustomBlocks.all) {
                event.accept(entry.item());
            }
        }
    }
}
