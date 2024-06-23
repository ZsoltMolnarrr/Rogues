package net.rogues.village;

import com.google.common.collect.ImmutableSet;
import net.fabric_extras.structure_pool.api.StructurePoolAPI;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
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
import net.rogues.item.Weapons;
import net.rogues.item.armor.Armors;
import net.rogues.util.SoundHelper;

import java.util.List;

public class RogueVillagers {
    public static final String MERCHANT = "arms_merchant";

    public static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(new Identifier(RoguesMod.NAMESPACE, name),
                1, 10, ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> workStation) {
        var id = new Identifier(RoguesMod.NAMESPACE, name);
        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(RoguesMod.NAMESPACE, name), new VillagerProfession(
                id.toString(),
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                (entry) -> {
                    return entry.matchesKey(workStation);
                },
                ImmutableSet.of(),
                ImmutableSet.of(),
                SoundHelper.WORKBENCH)
        );
    }

    private static class Offer {
        int level;
        ItemStack input;
        ItemStack output;
        int maxUses;
        int experience;
        float priceMultiplier;

        public Offer(int level, ItemStack input, ItemStack output, int maxUses, int experience, float priceMultiplier) {
            this.level = level;
            this.input = input;
            this.output = output;
            this.maxUses = maxUses;
            this.experience = experience;
            this.priceMultiplier = priceMultiplier;
        }

        public static Offer buy(int level, ItemStack item, int price, int maxUses, int experience, float priceMultiplier) {
            return new Offer(level, item, new ItemStack(Items.EMERALD, price), maxUses, experience, priceMultiplier);
        }

        public static Offer sell(int level, ItemStack item, int price, int maxUses, int experience, float priceMultiplier) {
            return new Offer(level, new ItemStack(Items.EMERALD, price), item, maxUses, experience, priceMultiplier);
        }
    }

    public static void register() {
        StructurePoolAPI.injectAll(RoguesMod.villagesConfig.value);
        var poi = registerPOI(MERCHANT, CustomBlocks.WORKBENCH.block());
        var profession = registerProfession(
                MERCHANT,
                RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), new Identifier(RoguesMod.NAMESPACE, MERCHANT)));

        List<Offer> offers = List.of(
                Offer.buy(1, new ItemStack(Items.LEATHER, 8), 5, 12, 4, 0.01f),
                Offer.sell(1, Weapons.flint_dagger.item().getDefaultStack(), 6, 12, 3, 0.1f),
                Offer.sell(1, Weapons.stone_double_axe.item().getDefaultStack(), 8, 12, 4, 0.1f),

                Offer.buy(2, new ItemStack(Items.IRON_INGOT, 12), 8, 12, 5, 0.01f),
                Offer.sell(2, Weapons.iron_sickle.item().getDefaultStack(), 12, 12, 10, 0.1f),
                Offer.sell(2, Weapons.iron_glaive.item().getDefaultStack(), 18, 12, 10, 0.1f),
                Offer.sell(2, Armors.RogueArmorSet_t1.head.getDefaultStack(), 15, 12, 13, 0.05f),
                Offer.sell(2, Armors.WarriorArmorSet_t1.head.getDefaultStack(), 15, 12, 13, 0.05f),

                Offer.sell(3, Weapons.iron_dagger.item().getDefaultStack(), 14, 12, 12, 0.1f),
                Offer.sell(3, Weapons.iron_double_axe.item().getDefaultStack(), 18, 12, 12, 0.1f),
                Offer.sell(3, Armors.RogueArmorSet_t1.feet.getDefaultStack(), 15, 12, 13, 0.05f),
                Offer.sell(3, Armors.WarriorArmorSet_t1.feet.getDefaultStack(), 15, 12, 13, 0.05f),

                Offer.sell(3, Armors.RogueArmorSet_t1.legs.getDefaultStack(), 15, 12, 13, 0.05f),
                Offer.sell(3, Armors.WarriorArmorSet_t1.legs.getDefaultStack(), 15, 12, 13, 0.05f),

                Offer.sell(4, Armors.RogueArmorSet_t1.chest.getDefaultStack(), 15, 12, 13, 0.05f),
                Offer.sell(4, Armors.WarriorArmorSet_t1.chest.getDefaultStack(), 15, 12, 13, 0.05f),
                Offer.sell(4, new ItemStack(Items.GOAT_HORN, 1), 15, 12, 5, 0.01f)
            );

        for(var offer: offers) {
            TradeOfferHelper.registerVillagerOffers(profession, offer.level, factories -> {
                factories.add(((entity, random) -> new TradeOffer(
                        offer.input,
                        offer.output,
                        offer.maxUses, offer.experience, offer.priceMultiplier)
                ));
            });
        }
        TradeOfferHelper.registerVillagerOffers(profession, 5, factories -> {
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.diamond_dagger.item(),
                    30,
                    3,
                    30,
                    0F).create(entity, random)
            ));
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.diamond_sickle.item(),
                    30,
                    3,
                    30,
                    0F).create(entity, random)
            ));
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.diamond_double_axe.item(),
                    40,
                    3,
                    30,
                    0F).create(entity, random)
            ));
            factories.add(((entity, random) -> new TradeOffers.SellEnchantedToolFactory(
                    Weapons.diamond_glaive.item(),
                    40,
                    3,
                    30,
                    0F).create(entity, random)
            ));
        });
    }
}
