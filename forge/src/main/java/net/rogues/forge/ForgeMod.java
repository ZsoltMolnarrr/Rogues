package net.rogues.forge;

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
import net.rogues.block.CustomBlocks;
import net.rogues.effect.RogueEffects;
import net.rogues.entity.RogueEntities;
import net.rogues.forge.client.ForgeClientMod;
import net.rogues.item.Group;
import net.rogues.item.RogueWeapons;
import net.rogues.item.armor.RogueArmors;
import net.rogues.util.RogueSounds;
import net.rogues.village.RogueVillagers;
import net.spell_engine.api.effect.Effects;

/// Forge 47 entrypoint (1.20.1 port of the NeoForge entrypoint).
///
/// Forge locks every vanilla registry outside its own `RegisterEvent` window, and a plain
/// `Registry.register` stays blocked even inside it before 47.4.0 — so this entrypoint registers
/// through the helper `RegisterEvent` hands out, iterating the content `common` exposes through its
/// `…ToRegister()` methods. `common` keeps its vanilla-shaped `registerX()` methods for Fabric.
/// See {@link #register(RegisterEvent)}.
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

    /// One listener for every registry; `RegisterEvent#register(key, consumer)` only runs the consumer when
    /// the event is for that key, so each block below executes inside exactly its own registry's window.
    ///
    /// The loops are **duplicated here on purpose** rather than delegated to `common`'s `registerX()`
    /// methods: a plain `Registry.register` is not usable on this loader, because Forge only clears the
    /// vanilla registry's own lock from 47.4.0 onwards — on 47.0–47.3 and NeoForge 1.20.1 it throws
    /// "Can not register to a locked registry" even inside the correct `RegisterEvent` window, and our
    /// `mods.toml` declares `loaderVersion = "[47,)"`. The helper this event hands out is the API every
    /// build of `[47,)` sanctions, so Forge iterates the same content `common` exposes through its
    /// `…ToRegister()` methods and registers it itself. `common` keeps its vanilla-shaped `registerX()`
    /// for Fabric, which is untouched.
    ///
    /// Two rules govern the grouping. `event.register` has no `else` and no throw, so content filed under a
    /// key that does not match the event **vanishes silently** — hence `creative_mode_tab` (event 65) gets
    /// its own block instead of riding along with `item` (event 7), as it used to. And `Item`, `Block` and
    /// `EntityType` take an intrusive registry holder in their *constructor*, so each `…ToRegister()` that
    /// builds one must stay inside its own registry's window.
    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, helper -> {
            RogueSounds.soundsToRegister().forEach(helper::register);
            // RogueArmors' materials hold `Entry#entry()`; only the register-reference path fills it in.
            RogueSounds.linkEntries();
        });
        event.register(RegistryKeys.BLOCK, helper ->
                CustomBlocks.blocksToRegister().forEach(helper::register));
        event.register(RegistryKeys.STATUS_EFFECT, helper -> {
            Effects.effectsToRegister(RogueEffects.entries, RoguesMod.effectsConfig.value.effects)
                    .forEach(helper::register);
            // Fills `Effects.Entry#entry` from the registry — must precede any behaviour wiring that
            // reads it. Nothing in `installBehaviours()` does today (1.20.1 APIs take the raw effect),
            // but the ordering is the recipe's and keeps it true if that changes.
            Effects.linkEntries(RogueEffects.entries);
            RogueEffects.installBehaviours();
            RoguesMod.effectsConfig.save();
        });
        event.register(RegistryKeys.ITEM, helper -> {
            RoguesMod.createItemGroup();
            CustomBlocks.blockItemsToRegister().forEach(helper::register);
            // Installed before the SpellEngine item-group listeners the two calls below install, so the
            // blocks come first in the tab.
            RoguesMod.installItemGroupContents();
            // RogueWeapons.itemsToRegister also appends the entries gated on optional mods
            // (BetterNether / BetterEnd / Aether), which `Weapon.itemsToRegister` alone would miss.
            RogueWeapons.itemsToRegister(RoguesMod.itemConfig.value.weapons).forEach(helper::register);
            RogueArmors.itemsToRegister(RoguesMod.itemConfig.value.armor_sets).forEach(helper::register);
            RoguesMod.itemConfig.save();
        });
        event.register(RegistryKeys.ENTITY_TYPE, helper ->
                RogueEntities.entityTypesToRegister().forEach(helper::register));
        event.register(RegistryKeys.VILLAGER_PROFESSION, helper -> {
            RogueVillagers.PROFESSION = RogueVillagers.createProfession(
                    RogueVillagers.MERCHANT, RogueVillagers.workStationKey());
            helper.register(RogueVillagers.PROFESSION_ID, RogueVillagers.PROFESSION);
            RogueVillagers.buildTrades();
        });
        event.register(RegistryKeys.POINT_OF_INTEREST_TYPE, helper ->
                // Forge's POI registry callback (PointOfInterestTypeCallbacks) fills the block-state ->
                // POI map from the type's own block states, so no extra wiring is needed.
                helper.register(RogueVillagers.POI_ID,
                        new PointOfInterestType(RogueVillagers.poiBlockStates(),
                                RogueVillagers.POI_TICKET_COUNT, RogueVillagers.POI_SEARCH_DISTANCE)));
        // `creative_mode_tab` is event 65, 58 events after `item` — its own window, or the write is dropped.
        event.register(RegistryKeys.ITEM_GROUP, helper -> {
            RoguesMod.createItemGroup();
            helper.register(Group.ID, Group.ROGUES);
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
