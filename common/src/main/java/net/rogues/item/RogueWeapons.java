package net.rogues.item;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.spell_engine.api.config.WeaponConfig;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_engine.rpg_series.item.Weapons;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

public class RogueWeapons {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry add(Weapon.Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString, boolean requirement, Item fallback) {
        var id = Identifier.of(idString);
        if (requirement) {
            return () -> {
                return Ingredient.ofItems(fallback);
            };
        } else {
            return () -> {
                var item = Registries.ITEM.get(id);
                var ingredient = item != null ? item : fallback;
                return Ingredient.ofItems(ingredient);
            };
        }
    }

    private static final String AETHER = "aether";
    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";

    // MARK: Daggers

    public static final Weapon.Entry flint_dagger = add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "flint_dagger", Equipment.Tier.TIER_0, () -> Ingredient.ofItems(Items.FLINT)));
    public static final Weapon.Entry iron_dagger = add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "iron_dagger", Equipment.Tier.TIER_1, () -> Ingredient.ofItems(Items.IRON_INGOT)));
    public static final Weapon.Entry golden_dagger = add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "golden_dagger", Equipment.Tier.GOLDEN, () -> Ingredient.ofItems(Items.GOLD_INGOT)));
    public static final Weapon.Entry diamond_dagger = add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "diamond_dagger", Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.DIAMOND)));
    public static final Weapon.Entry netherite_dagger = add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "netherite_dagger", Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)));

    // MARK: Sickles

    public static final Weapon.Entry iron_sickle = add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "iron_sickle", Equipment.Tier.TIER_1, () -> Ingredient.ofItems(Items.IRON_INGOT)));
    public static final Weapon.Entry golden_sickle = add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "golden_sickle", Equipment.Tier.GOLDEN, () -> Ingredient.ofItems(Items.GOLD_INGOT)));
    public static final Weapon.Entry diamond_sickle = add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "diamond_sickle", Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.DIAMOND)));
    public static final Weapon.Entry netherite_sickle = add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "netherite_sickle", Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)));

    // MARK: Double Axe

    public static final Weapon.Entry stone_double_axe = add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "stone_double_axe", Equipment.Tier.TIER_0, () -> Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS)));
    public static final Weapon.Entry iron_double_axe = add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "iron_double_axe", Equipment.Tier.TIER_1, () -> Ingredient.ofItems(Items.IRON_INGOT)));
    public static final Weapon.Entry golden_double_axe = add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "golden_double_axe", Equipment.Tier.GOLDEN, () -> Ingredient.ofItems(Items.GOLD_INGOT)));
    public static final Weapon.Entry diamond_double_axe = add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "diamond_double_axe", Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.DIAMOND)));
    public static final Weapon.Entry netherite_double_axe = add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "netherite_double_axe", Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)));

    // MARK: Glaives

    public static final Weapon.Entry iron_glaive = add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "iron_glaive", Equipment.Tier.TIER_1, () -> Ingredient.ofItems(Items.IRON_INGOT)));
    public static final Weapon.Entry golden_glaive = add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "golden_glaive", Equipment.Tier.GOLDEN, () -> Ingredient.ofItems(Items.GOLD_INGOT)));
    public static final Weapon.Entry diamond_glaive = add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "diamond_glaive", Equipment.Tier.TIER_2, () -> Ingredient.ofItems(Items.DIAMOND)));
    public static final Weapon.Entry netherite_glaive = add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "netherite_glaive", Equipment.Tier.TIER_3, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)));

    // MARK: Register

    public static void register(Map<String, WeaponConfig> configs) {
        if (RoguesMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_NETHER)) {
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_NETHER), Items.NETHERITE_INGOT);
            add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "ruby_dagger", Equipment.Tier.TIER_4, repair));
            add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "ruby_sickle", Equipment.Tier.TIER_4, repair));
            add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "ruby_double_axe", Equipment.Tier.TIER_4, repair));
            add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "ruby_glaive", Equipment.Tier.TIER_4, repair));
        }
        if (RoguesMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_END)) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "aeternium_dagger", Equipment.Tier.TIER_4, repair));
            add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "aeternium_sickle", Equipment.Tier.TIER_4, repair));
            add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "aeternium_double_axe", Equipment.Tier.TIER_4, repair));
            add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "aeternium_glaive", Equipment.Tier.TIER_4, repair));
        }
        if (RoguesMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(AETHER)) {
            var repair = ingredient("aether:ambrosium_shard", FabricLoader.getInstance().isModLoaded(AETHER), Items.NETHERITE_INGOT);
            add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "aether_dagger", Equipment.Tier.TIER_4, repair)
                    .loot(Equipment.LootProperties.of("aether")));
            add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "aether_sickle", Equipment.Tier.TIER_4, repair)
                    .loot(Equipment.LootProperties.of("aether")));
            add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "aether_double_axe", Equipment.Tier.TIER_4, repair)
                    .loot(Equipment.LootProperties.of("aether")));
            add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "aether_glaive", Equipment.Tier.TIER_4, repair)
                    .loot(Equipment.LootProperties.of("aether")));
        }
        Weapon.register(configs, entries, Group.KEY);
    }
}
