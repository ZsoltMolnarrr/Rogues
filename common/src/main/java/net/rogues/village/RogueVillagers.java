package net.rogues.village;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.rogues.RoguesMod;
import net.rogues.block.CustomBlocks;
import net.rogues.item.RogueWeapons;
import net.rogues.item.armor.RogueArmors;
import net.rogues.util.RogueSounds;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class RogueVillagers {
    public static final String MERCHANT = "arms_merchant";
    /// The id the profession registers under (the POI shares the same path).
    public static final Identifier PROFESSION_ID = new Identifier(RoguesMod.NAMESPACE, MERCHANT);
    public static final Identifier POI_ID = new Identifier(RoguesMod.NAMESPACE, MERCHANT);
    public static final int POI_TICKET_COUNT = 1;
    public static final int POI_SEARCH_DISTANCE = 10;

    /// The martial-workbench workstation block states for the POI. Registration itself is loader-specific
    /// (Fabric: `PointOfInterestHelper`; Forge: a `PointOfInterestType` through the `RegisterEvent` helper,
    /// whose block-state mapping Forge wires via its POI registry callback) — done in each platform's
    /// entrypoint; this only exposes the shared state set.
    public static Set<BlockState> poiBlockStates() {
        return ImmutableSet.copyOf(CustomBlocks.WORKBENCH.block().getStateManager().getStates());
    }

    /// The registered arms-merchant profession, set by {@link #registerVillagers()}. Read by the
    /// loader-specific trade-offer registration (Fabric `TradeOfferHelper` / Forge `VillagerTradesEvent`).
    public static VillagerProfession PROFESSION;

    /// Trade offers per merchant tier (1..5), populated by {@link #registerVillagers()}. Actual registration
    /// with the game is loader-specific and lives in each platform's entrypoint.
    public static final LinkedHashMap<Integer, List<TradeOffers.Factory>> TRADES = new LinkedHashMap<>();

    /// Builds the profession. Creation only — nothing is written into the registry, so a loader that
    /// registers villager professions itself (Forge) calls this and registers the result under
    /// {@link #PROFESSION_ID}.
    public static VillagerProfession createProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = new Identifier(RoguesMod.NAMESPACE, name);
        return new VillagerProfession(
                id.toString(),
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                ImmutableSet.of(),
                ImmutableSet.of(),
                RogueSounds.WORKBENCH.soundEvent()
        );
    }

    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        return Registry.register(Registries.VILLAGER_PROFESSION,
                new Identifier(RoguesMod.NAMESPACE, name),
                createProfession(name, workStation));
    }

    /// The registry key of the martial workbench POI, as the profession's workstation predicate needs it.
    public static RegistryKey<PointOfInterestType> workStationKey() {
        return RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), POI_ID);
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

    /// 1.20.1 only ships `BuyForOneEmeraldFactory` (always 1 emerald); the 1.21 `BuyItemFactory`
    /// (item, count, maxUses, experience, emeraldAmount) is rebuilt here on the raw `TradeOffer` ctor.
    private static TradeOffers.Factory buyForEmeralds(Item item, int count, int maxUses, int experience, int emeralds) {
        return (entity, random) -> new TradeOffer(
                new ItemStack(item, count), new ItemStack(Items.EMERALD, emeralds), maxUses, experience, 0.05F);
    }

    /// Vanilla's own price multiplier for the sell factories, reproduced by the helpers below.
    private static final float PRICE_MULTIPLIER = 0.05F;

    /// Hand-rolled stand-in for `TradeOffers.SellItemFactory`: that class is **package-private** in
    /// vanilla 1.20.1 and stays so after Forge's access transformer, so touching it throws
    /// `IllegalAccessError` in production even though `common` compiles against a classpath where some
    /// other mod's access widener opens it (see `jewelry-port-notes.md` §4). The raw `TradeOffer`
    /// constructor always works. Argument order, the `new ItemStack(item, count)` sold stack and the
    /// 0.05F multiplier reproduce vanilla's 5-argument `Item` overload exactly.
    private static TradeOffers.Factory sellForEmeralds(Item item, int price, int count, int maxUses, int experience) {
        return (entity, random) -> new TradeOffer(
                new ItemStack(Items.EMERALD, price), new ItemStack(item, count),
                maxUses, experience, PRICE_MULTIPLIER);
    }

    /// Hand-rolled stand-in for `TradeOffers.SellEnchantedToolFactory` — package-private for the same
    /// reason as {@link #sellForEmeralds}. Reproduces vanilla exactly: a random enchantment level of
    /// `5 + random.nextInt(15)`, that level added to the base price and capped at 64 emeralds.
    private static TradeOffers.Factory sellEnchanted(Item item, int basePrice, int maxUses, int experience, float multiplier) {
        return (entity, random) -> {
            var level = 5 + random.nextInt(15);
            var tool = EnchantmentHelper.enchant(random, new ItemStack(item), level, false);
            var price = Math.min(basePrice + level, 64);
            return new TradeOffer(new ItemStack(Items.EMERALD, price), tool, maxUses, experience, multiplier);
        };
    }

    public static void registerVillagers() {
        PROFESSION = registerProfession(MERCHANT, workStationKey());
        buildTrades();
    }

    /// Fills {@link #TRADES}. Creation only — no registry write, and no registration with the game
    /// (that is loader-specific: Fabric `TradeOfferHelper` / Forge `VillagerTradesEvent`).
    public static void buildTrades() {
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
                buyForEmeralds(Items.LEATHER, 8, 12, 4, 5),
                sellForEmeralds(RogueWeapons.flint_dagger.item(), 6, 1, 12, 3),
                sellForEmeralds(RogueWeapons.stone_double_axe.item(), 8, 1, 12, 4)
        ));
        TRADES.put(2, List.of(
                buyForEmeralds(Items.IRON_INGOT, 12, 12, 5, 8),
                sellForEmeralds(RogueWeapons.iron_sickle.item(), 12, 1, 12, 10),
                sellForEmeralds(RogueWeapons.iron_glaive.item(), 18, 1, 12, 10),
                sellForEmeralds(RogueArmors.RogueArmorSet_t1.head, 15, 1, 12, 13),
                sellForEmeralds(RogueArmors.WarriorArmorSet_t1.head, 15, 1, 12, 13)
        ));
        TRADES.put(3, List.of(
                sellForEmeralds(RogueWeapons.iron_dagger.item(), 14, 1, 12, 15),
                sellForEmeralds(RogueWeapons.iron_double_axe.item(), 18, 1, 12, 15),
                sellForEmeralds(RogueArmors.RogueArmorSet_t1.feet, 15, 1, 12, 15),
                sellForEmeralds(RogueArmors.WarriorArmorSet_t1.feet, 15, 1, 12, 15),
                sellForEmeralds(RogueArmors.RogueArmorSet_t1.legs, 15, 1, 12, 15),
                sellForEmeralds(RogueArmors.WarriorArmorSet_t1.legs, 15, 1, 12, 15)
        ));
        TRADES.put(4, List.of(
                sellForEmeralds(RogueArmors.RogueArmorSet_t1.chest, 15, 1, 12, 15),
                sellForEmeralds(RogueArmors.WarriorArmorSet_t1.chest, 15, 1, 12, 15),
                sellForEmeralds(Items.GOAT_HORN, 15, 1, 12, 5)
        ));
        TRADES.put(5, List.of(
                sellEnchanted(RogueWeapons.diamond_dagger.item(), 30, 3, 30, 0F),
                sellEnchanted(RogueWeapons.diamond_sickle.item(), 30, 3, 30, 0F),
                sellEnchanted(RogueWeapons.diamond_double_axe.item(), 40, 3, 30, 0F),
                sellEnchanted(RogueWeapons.diamond_glaive.item(), 40, 3, 30, 0F)
        ));
    }
}
