package net.rogues.village;

import com.google.common.collect.ImmutableSet;
import net.rpg_foundation.structure_pool.api.StructurePoolAPI;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.rogues.RoguesMod;
import net.rogues.block.CustomBlocks;
import net.rogues.item.RogueWeapons;
import net.rogues.item.armor.RogueArmors;
import net.rogues.util.RogueSounds;
import net.spell_engine.Platform;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class RogueVillagers {
    public static final String MERCHANT = "arms_merchant";
    public static final Identifier POI_ID = Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, MERCHANT);
    public static final int POI_TICKET_COUNT = 1;
    public static final int POI_SEARCH_DISTANCE = 10;

    /// The martial-workbench workstation block states for the POI. Registration itself is loader-specific
    /// (Fabric: `PointOfInterestHelper`; NeoForge: a plain `Registry.register` of a `PointOfInterestType`,
    /// whose block-state mapping NeoForge wires via its POI registry callback) — done in each platform's
    /// entrypoint; this only exposes the shared state set.
    public static Set<BlockState> poiBlockStates() {
        return ImmutableSet.copyOf(CustomBlocks.WORKBENCH.block().getStateDefinition().getPossibleStates());
    }

    /// The registered arms-merchant profession, set by {@link #registerVillagers()}. Read by the
    /// loader-specific trade-offer registration (Fabric `TradeOfferHelper` / NeoForge `VillagerTradesEvent`).
    public static ResourceKey<VillagerProfession> PROFESSION;

    /// Trade offers per merchant tier (1..5), populated by {@link #registerVillagers()}. Actual registration
    /// with the game is loader-specific and lives in each platform's entrypoint.
    public static final LinkedHashMap<Integer, List<VillagerTrades.ItemListing>> TRADES = new LinkedHashMap<>();

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
                RogueSounds.WORKBENCH.soundEvent())
        );
        return key;
    }

//    private static class Offer {
//        int level;
//        ItemStack input;
//        ItemStack output;
//        int maxUses;
//        int experience;
//        float priceMultiplier;
//
//        public Offer(int level, ItemStack input, ItemStack output, int maxUses, int experience, float priceMultiplier) {
//            this.level = level;
//            this.input = input;
//            this.output = output;
//            this.maxUses = maxUses;
//            this.experience = experience;
//            this.priceMultiplier = priceMultiplier;
//        }
//
//        public static Offer buy(int level, ItemStack item, int price, int maxUses, int experience, float priceMultiplier) {
//            return new Offer(level, item, new ItemStack(Items.EMERALD, price), maxUses, experience, priceMultiplier);
//        }
//
//        public static Offer sell(int level, ItemStack item, int price, int maxUses, int experience, float priceMultiplier) {
//            return new Offer(level, new ItemStack(Items.EMERALD, price), item, maxUses, experience, priceMultiplier);
//        }
//    }

    public static void registerVillagers() {
        if (!Platform.util().isModLoaded("lithostitched")) {
            // Only inject the village if the Lithostitched is not present
            StructurePoolAPI.injectAll(RoguesMod.villagesConfig.value);
        }
        PROFESSION = registerProfession(
                MERCHANT,
                ResourceKey.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE.key(), POI_ID));

//        List<Offer> offers = List.of(
//                Offer.buy(1, new ItemStack(Items.LEATHER, 8), 5, 12, 4, 0.01f),
//                Offer.sell(1, Weapons.flint_dagger.item().getDefaultStack(), 6, 12, 3, 0.1f),
//                Offer.sell(1, Weapons.stone_double_axe.item().getDefaultStack(), 8, 12, 4, 0.1f),
//
//                Offer.buy(2, new ItemStack(Items.IRON_INGOT, 12), 8, 12, 5, 0.01f),
//                Offer.sell(2, Weapons.iron_sickle.item().getDefaultStack(), 12, 12, 10, 0.1f),
//                Offer.sell(2, Weapons.iron_glaive.item().getDefaultStack(), 18, 12, 10, 0.1f),
//                Offer.sell(2, Armors.RogueArmorSet_t1.head.getDefaultStack(), 15, 12, 13, 0.05f),
//                Offer.sell(2, Armors.WarriorArmorSet_t1.head.getDefaultStack(), 15, 12, 13, 0.05f),
//
//                Offer.sell(3, Weapons.iron_dagger.item().getDefaultStack(), 14, 12, 12, 0.1f),
//                Offer.sell(3, Weapons.iron_double_axe.item().getDefaultStack(), 18, 12, 12, 0.1f),
//                Offer.sell(3, Armors.RogueArmorSet_t1.feet.getDefaultStack(), 15, 12, 13, 0.05f),
//                Offer.sell(3, Armors.WarriorArmorSet_t1.feet.getDefaultStack(), 15, 12, 13, 0.05f),
//
//                Offer.sell(3, Armors.RogueArmorSet_t1.legs.getDefaultStack(), 15, 12, 13, 0.05f),
//                Offer.sell(3, Armors.WarriorArmorSet_t1.legs.getDefaultStack(), 15, 12, 13, 0.05f),
//
//                Offer.sell(4, Armors.RogueArmorSet_t1.chest.getDefaultStack(), 15, 12, 13, 0.05f),
//                Offer.sell(4, Armors.WarriorArmorSet_t1.chest.getDefaultStack(), 15, 12, 13, 0.05f),
//                Offer.sell(4, new ItemStack(Items.GOAT_HORN, 1), 15, 12, 5, 0.01f)
//            );

        TRADES.clear();
        TRADES.put(1, List.of(
                new VillagerTrades.EmeraldForItems(Items.LEATHER, 8, 12, 4, 5),
                new VillagerTrades.ItemsForEmeralds(RogueWeapons.flint_dagger.item(), 6, 1, 12, 3),
                new VillagerTrades.ItemsForEmeralds(RogueWeapons.stone_double_axe.item(), 8, 1, 12, 4)
        ));
        TRADES.put(2, List.of(
                new VillagerTrades.EmeraldForItems(Items.IRON_INGOT, 12, 12, 5, 8),
                new VillagerTrades.ItemsForEmeralds(RogueWeapons.iron_sickle.item(), 12, 1, 12, 10),
                new VillagerTrades.ItemsForEmeralds(RogueWeapons.iron_glaive.item(), 18, 1, 12, 10),
                new VillagerTrades.ItemsForEmeralds(RogueArmors.RogueArmorSet_t1.head, 15, 1, 12, 13),
                new VillagerTrades.ItemsForEmeralds(RogueArmors.WarriorArmorSet_t1.head, 15, 1, 12, 13)
        ));
        TRADES.put(3, List.of(
                new VillagerTrades.ItemsForEmeralds(RogueWeapons.iron_dagger.item(), 14, 1, 12, 15),
                new VillagerTrades.ItemsForEmeralds(RogueWeapons.iron_double_axe.item(), 18, 1, 12, 15),
                new VillagerTrades.ItemsForEmeralds(RogueArmors.RogueArmorSet_t1.feet, 15, 1, 12, 15),
                new VillagerTrades.ItemsForEmeralds(RogueArmors.WarriorArmorSet_t1.feet, 15, 1, 12, 15),
                new VillagerTrades.ItemsForEmeralds(RogueArmors.RogueArmorSet_t1.legs, 15, 1, 12, 15),
                new VillagerTrades.ItemsForEmeralds(RogueArmors.WarriorArmorSet_t1.legs, 15, 1, 12, 15)
        ));
        TRADES.put(4, List.of(
                new VillagerTrades.ItemsForEmeralds(RogueArmors.RogueArmorSet_t1.chest, 15, 1, 12, 15),
                new VillagerTrades.ItemsForEmeralds(RogueArmors.WarriorArmorSet_t1.chest, 15, 1, 12, 15),
                new VillagerTrades.ItemsForEmeralds(Items.GOAT_HORN, 15, 1, 12, 5)
        ));
        TRADES.put(5, List.of(
                (VillagerTrades.ItemListing) (world, entity, random) -> new VillagerTrades.EnchantedItemForEmeralds(
                        RogueWeapons.diamond_dagger.item(), 30, 3, 30, 0F).getOffer(world, entity, random),
                (VillagerTrades.ItemListing) (world, entity, random) -> new VillagerTrades.EnchantedItemForEmeralds(
                        RogueWeapons.diamond_sickle.item(), 30, 3, 30, 0F).getOffer(world, entity, random),
                (VillagerTrades.ItemListing) (world, entity, random) -> new VillagerTrades.EnchantedItemForEmeralds(
                        RogueWeapons.diamond_double_axe.item(), 40, 3, 30, 0F).getOffer(world, entity, random),
                (VillagerTrades.ItemListing) (world, entity, random) -> new VillagerTrades.EnchantedItemForEmeralds(
                        RogueWeapons.diamond_glaive.item(), 40, 3, 30, 0F).getOffer(world, entity, random)
        ));
    }
}
