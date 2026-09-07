package net.rogues.forge;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import net.rogues.RoguesMod;
import net.rogues.forge.client.ForgeClientMod;
import net.rogues.village.RogueVillagers;

/// Forge 47 entrypoint (1.20.1 port of the NeoForge entrypoint).
///
/// Forge locks every vanilla registry outside its own `RegisterEvent` window, so each `registerX()`
/// call sits inside the window of the registry it writes to — which is why block and block-item
/// registration are split (`CustomBlocks.registerBlocks()` / `registerItems()`).
@Mod(RoguesMod.ID)
public final class ForgeMod {
    // FMLJavaModLoadingContext.get() is flagged for removal by late 47.x builds, but the
    // constructor-injected replacement doesn't exist on early 47.x; get() works on all of [47,).
    @SuppressWarnings("removal")
    public ForgeMod() {
        // Run our common setup.
        RoguesMod.init();

        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Explicit event classes: Forge 47's plain addListener(Consumer) infers the event type from the
        // lambda via TypeTools, which is fragile; the 4-arg overload takes it directly.
        modBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, ForgeMod::register);
        // Creative-tab placement for the custom blocks is loader-neutral — RoguesMod.registerItems()
        // installs it through SpellEngine's PlatformEvents.onItemGroupModify, ahead of the weapon/armor
        // listeners. It must NOT be a Rogues-owned BuildCreativeModeTabContentsEvent listener: Forge posts
        // that event per mod container in mod-load order, so anything added here would always land after
        // SpellEngine's contributions and the blocks could never come first.
        // Villager trades — game-bus event (fired per profession); replaces Fabric API's TradeOfferHelper.
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, VillagerTradesEvent.class, ForgeMod::onVillagerTrades);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ForgeClientMod.register(modBus);
        }
    }

    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, reg -> RoguesMod.registerSounds());
        event.register(RegistryKeys.BLOCK, reg -> RoguesMod.registerBlocks());
        event.register(RegistryKeys.STATUS_EFFECT, reg -> RoguesMod.registerEffects());
        event.register(RegistryKeys.ENTITY_TYPE, reg -> RoguesMod.registerEntities());
        event.register(RegistryKeys.ITEM, reg -> {
            // Also registers the `rogues:generic` item group: ITEM_GROUP is a vanilla-only registry
            // (not Forge-wrapped), unfrozen for the whole RegisterEvent phase.
            RoguesMod.registerItems();
        });
        event.register(RegistryKeys.POINT_OF_INTEREST_TYPE, reg -> {
            // Plain vanilla registry insert. Forge's POI registry callback (PointOfInterestTypeCallbacks)
            // fills the block-state -> POI map from the type's own block states, so no helper is needed.
            Registry.register(Registries.POINT_OF_INTEREST_TYPE, RogueVillagers.POI_ID,
                    new PointOfInterestType(RogueVillagers.poiBlockStates(),
                            RogueVillagers.POI_TICKET_COUNT, RogueVillagers.POI_SEARCH_DISTANCE));
        });
        event.register(RegistryKeys.VILLAGER_PROFESSION, reg -> {
            RoguesMod.registerVillagers(); // registers the profession + builds RogueVillagers.TRADES
        });
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
