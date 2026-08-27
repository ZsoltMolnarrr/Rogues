package net.rogues.village;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.rpg_foundation.structure_pool.api.StructurePoolAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.level.block.state.BlockState;
import net.rogues.RoguesMod;
import net.rogues.block.CustomBlocks;
import net.rogues.util.RogueSounds;
import net.spell_engine.Platform;

import java.util.Set;

public class RogueVillagers {
    public static final String MERCHANT = "arms_merchant";
    public static final Identifier POI_ID = Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, MERCHANT);
    public static final int POI_TICKET_COUNT = 1;
    public static final int POI_SEARCH_DISTANCE = 10;

    /// The martial-workbench workstation block states for the POI. Registration itself is loader-specific
    /// (Fabric: `PoiHelper`; NeoForge: a plain `Registry.register` of a `PointOfInterestType`,
    /// whose block-state mapping NeoForge wires via its POI registry callback) — done in each platform's
    /// entrypoint; this only exposes the shared state set.
    public static Set<BlockState> poiBlockStates() {
        return ImmutableSet.copyOf(CustomBlocks.WORKBENCH.block().getStateDefinition().getPossibleStates());
    }

    /// The registered arms-merchant profession, set by {@link #registerVillagers()}.
    public static ResourceKey<VillagerProfession> PROFESSION;

    /// 26.1: villager trades are data-driven. A profession carries one `TradeSet` key per merchant level;
    /// the sets themselves live in `data/rogues/trade_set/arms_merchant/level_<n>.json`, each pointing at the
    /// tag `#rogues:arms_merchant/level_<n>` (`data/rogues/tags/villager_trade/…`) which collects the
    /// individual `data/rogues/villager_trade/arms_merchant/<n>/*.json` entries.
    /// There is no Java trade registration any more (`VillagerTrades.ItemListing`, Fabric's
    /// `TradeOfferHelper` and NeoForge's `VillagerTradesEvent` list are all gone / unused).
    public static ResourceKey<TradeSet> tradeSet(int level) {
        return ResourceKey.create(Registries.TRADE_SET,
                Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, MERCHANT + "/level_" + level));
    }

    private static final Int2ObjectMap<ResourceKey<TradeSet>> TRADE_SETS = Int2ObjectMap.ofEntries(
            Int2ObjectMap.entry(1, tradeSet(1)),
            Int2ObjectMap.entry(2, tradeSet(2)),
            Int2ObjectMap.entry(3, tradeSet(3)),
            Int2ObjectMap.entry(4, tradeSet(4)),
            Int2ObjectMap.entry(5, tradeSet(5))
    );

    public static ResourceKey<VillagerProfession> registerProfession(String name, ResourceKey<PoiType> workStation) {
        var id = Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, name);
        var key = ResourceKey.create(BuiltInRegistries.VILLAGER_PROFESSION.key(), id);
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, key, new VillagerProfession(
                // 1.21.11: the profession record carries its display name as Text (vanilla derives it as
                // `entity.<namespace>.villager.<path>`); it used to be the plain id string.
                Component.translatable("entity." + id.getNamespace() + ".villager." + id.getPath()),
                (entry) -> {
                    return entry.is(workStation);
                },
                (entry) -> {
                    return entry.is(workStation);
                },
                ImmutableSet.of(),
                ImmutableSet.of(),
                RogueSounds.WORKBENCH.soundEvent(),
                // 26.1: the profession points at its data-driven trade sets, one per merchant level.
                TRADE_SETS)
        );
        return key;
    }

    public static void registerVillagers() {
        if (!Platform.util().isModLoaded("lithostitched")) {
            // Only inject the village if the Lithostitched is not present
            StructurePoolAPI.injectAll(RoguesMod.villagesConfig.value);
        }
        PROFESSION = registerProfession(
                MERCHANT,
                ResourceKey.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE.key(), POI_ID));
    }
}
