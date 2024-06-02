package net.rogues.item;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.weapon.MeleeWeaponItem;
import net.spell_engine.api.item.weapon.SpellWeaponItem;
import net.spell_engine.api.item.weapon.Weapon;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

public class Weapons {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry entry(String name, Weapon.CustomMaterial material, Item item, ItemConfig.Weapon defaults) {
        return entry(null, name, material, item, defaults);
    }

    private static Weapon.Entry entry(String requiredMod, String name, Weapon.CustomMaterial material, Item item, ItemConfig.Weapon defaults) {
        var entry = new Weapon.Entry(RoguesMod.NAMESPACE, name, material, item, defaults, null);
        if (entry.isRequiredModInstalled()) {
            entries.add(entry);
        }
        return entry;
    }

    private static Supplier<Ingredient> ingredient(String idString, boolean requirement, Item fallback) {
        var id = new Identifier(idString);
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

    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";

    // MARK: Daggers

    private static Weapon.Entry dagger(String name, Weapon.CustomMaterial material, float damage) {
        return dagger(null, name, material, damage);
    }

    private static Weapon.Entry dagger(String requiredMod, String name, Weapon.CustomMaterial material, float damage) {
        var settings = new Item.Settings();
        var item = new MeleeWeaponItem(material, settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(damage, -1.6F));
    }

    public static final Weapon.Entry flint_dagger = dagger("flint_dagger",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.FLINT)), 2.6F);
    public static final Weapon.Entry iron_dagger = dagger("iron_dagger",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 3.3F);
    public static final Weapon.Entry golden_dagger = dagger("golden_dagger",
            Weapon.CustomMaterial.matching(ToolMaterials.GOLD, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 1.8F);
    public static final Weapon.Entry diamond_dagger = dagger("diamond_dagger",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 4F);
    public static final Weapon.Entry netherite_dagger = dagger("netherite_dagger",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 4.7F);

    private static Weapon.Entry sickle(String name, Weapon.CustomMaterial material, float damage) {
        return sickle(null, name, material, damage);
    }

    private static Weapon.Entry sickle(String requiredMod, String name, Weapon.CustomMaterial material, float damage) {
        var settings = new Item.Settings();
        var item = new MeleeWeaponItem(material, settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(damage, -2F));
    }

    public static final Weapon.Entry iron_sickle = sickle("iron_sickle",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 4.1F);
    public static final Weapon.Entry golden_sickle = sickle("golden_sickle",
            Weapon.CustomMaterial.matching(ToolMaterials.GOLD, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 2.4F);
    public static final Weapon.Entry diamond_sickle = sickle("diamond_sickle",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 5F);
    public static final Weapon.Entry netherite_sickle = sickle("netherite_sickle",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 5.9F);

    // MARK: Double Axe

    private static Weapon.Entry axe(String name, Weapon.CustomMaterial material, float damage) {
        return axe(null, name, material, damage);
    }

    private static Weapon.Entry axe(String requiredMod, String name, Weapon.CustomMaterial material, float damage) {
        var settings = new Item.Settings();
        var item = new MeleeWeaponItem(material, settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(damage, -3.2F));
    }

//    public static final Weapon.Entry wooden_double_axe = axe("wooden_double_axe",
//            Weapon.CustomMaterial.matching(ToolMaterials.WOOD, () -> Ingredient.fromTag(ItemTags.PLANKS)), 5F);
    public static final Weapon.Entry stone_double_axe = axe("stone_double_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.STONE, () -> Ingredient.fromTag(ItemTags.STONE_TOOL_MATERIALS)), 8F);
    public static final Weapon.Entry iron_double_axe = axe("iron_double_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 10F);
    public static final Weapon.Entry golden_double_axe = axe("golden_double_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.GOLD, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 10F);
    public static final Weapon.Entry diamond_double_axe = axe("diamond_double_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 11F);
    public static final Weapon.Entry netherite_double_axe = axe("netherite_double_axe",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 13F);

    // MARK: Glaives

    private static Weapon.Entry glaive(String name, Weapon.CustomMaterial material, float damage) {
        return glaive(null, name, material, damage);
    }

    private static Weapon.Entry glaive(String requiredMod, String name, Weapon.CustomMaterial material, float damage) {
        var settings = new Item.Settings();
        var item = new SpellWeaponItem(material, settings);
        return entry(requiredMod, name, material, item, new ItemConfig.Weapon(damage, -3F));
    }

    public static final Weapon.Entry iron_glaive = glaive("iron_glaive",
            Weapon.CustomMaterial.matching(ToolMaterials.IRON, () -> Ingredient.ofItems(Items.IRON_INGOT)), 8F);
    public static final Weapon.Entry golden_glaive = glaive("golden_glaive",
            Weapon.CustomMaterial.matching(ToolMaterials.GOLD, () -> Ingredient.ofItems(Items.GOLD_INGOT)), 5F);
    public static final Weapon.Entry diamond_glaive = glaive("diamond_glaive",
            Weapon.CustomMaterial.matching(ToolMaterials.DIAMOND, () -> Ingredient.ofItems(Items.DIAMOND)), 9.5F);
    public static final Weapon.Entry netherite_glaive = glaive("netherite_glaive",
            Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, () -> Ingredient.ofItems(Items.NETHERITE_INGOT)), 11F);

    // MARK: Register

    public static void register(Map<String, ItemConfig.Weapon> configs) {
        if (RoguesMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_END)) {
            var repair = ingredient("betterend:aeternium_ingot", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            dagger("aeternium_dagger", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), 5.5F);
            sickle("aeternium_sickle", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), 6.8F);
            axe("aeternium_double_axe", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), 13F);
            glaive("aeternium_glaive", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), 11F);
        }

        if (RoguesMod.tweaksConfig.value.ignore_items_required_mods || FabricLoader.getInstance().isModLoaded(BETTER_NETHER)) {
            var repair = ingredient("betternether:nether_ruby", FabricLoader.getInstance().isModLoaded(BETTER_END), Items.NETHERITE_INGOT);
            dagger("ruby_dagger", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), 5.5F);
            sickle("ruby_sickle", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), 6.8F);
            axe("ruby_double_axe", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), 13F);
            glaive("ruby_glaive", Weapon.CustomMaterial.matching(ToolMaterials.NETHERITE, repair), 11F);
        }

        Weapon.register(configs, entries, Group.KEY);
    }
}
