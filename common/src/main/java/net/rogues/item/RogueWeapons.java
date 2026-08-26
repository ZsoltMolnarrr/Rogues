package net.rogues.item;

import net.spell_engine.Platform;
import net.minecraft.tags.ItemTags;
import net.rogues.RoguesMod;
import net.spell_engine.rpg_series.config.WeaponConfig;
import net.spell_engine.rpg_series.item.Equipment;
import net.spell_engine.rpg_series.item.Weapon;
import net.spell_engine.rpg_series.item.Weapons;

import java.util.ArrayList;
import java.util.Map;

public class RogueWeapons {
    public static final ArrayList<Weapon.Entry> entries = new ArrayList<>();

    private static Weapon.Entry add(Weapon.Entry entry) {
        entries.add(entry);
        return entry;
    }

    private static final String AETHER = "aether";
    private static final String BETTER_END = "betterend";
    private static final String BETTER_NETHER = "betternether";

    // MARK: Daggers

    public static final Weapon.Entry flint_dagger = add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "flint_dagger", Equipment.Tier.TIER_0, RogueItemTags.REPAIRS_FLINT).translatedName("Flint Dagger"));
    public static final Weapon.Entry iron_dagger = add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "iron_dagger", Equipment.Tier.TIER_1, null).translatedName("Iron Dagger"));
    public static final Weapon.Entry golden_dagger = add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "golden_dagger", Equipment.Tier.GOLDEN, null).translatedName("Golden Dagger"));
    public static final Weapon.Entry diamond_dagger = add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "diamond_dagger", Equipment.Tier.TIER_2, null).translatedName("Diamond Dagger"));
    public static final Weapon.Entry netherite_dagger = add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "netherite_dagger", Equipment.Tier.TIER_3, null).translatedName("Netherite Dagger"));

    // MARK: Sickles

    public static final Weapon.Entry iron_sickle = add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "iron_sickle", Equipment.Tier.TIER_1, null).translatedName("Iron Sickle"));
    public static final Weapon.Entry golden_sickle = add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "golden_sickle", Equipment.Tier.GOLDEN, null).translatedName("Golden Sickle"));
    public static final Weapon.Entry diamond_sickle = add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "diamond_sickle", Equipment.Tier.TIER_2, null).translatedName("Diamond Sickle"));
    public static final Weapon.Entry netherite_sickle = add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "netherite_sickle", Equipment.Tier.TIER_3, null).translatedName("Netherite Sickle"));

    // MARK: Double Axe

    public static final Weapon.Entry stone_double_axe = add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "stone_double_axe", Equipment.Tier.TIER_0, ItemTags.STONE_TOOL_MATERIALS).translatedName("Stone Double Axe"));
    public static final Weapon.Entry iron_double_axe = add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "iron_double_axe", Equipment.Tier.TIER_1, null).translatedName("Iron Double Axe"));
    public static final Weapon.Entry golden_double_axe = add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "golden_double_axe", Equipment.Tier.GOLDEN, null).translatedName("Golden Double Axe"));
    public static final Weapon.Entry diamond_double_axe = add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "diamond_double_axe", Equipment.Tier.TIER_2, null).translatedName("Diamond Double Axe"));
    public static final Weapon.Entry netherite_double_axe = add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "netherite_double_axe", Equipment.Tier.TIER_3, null).translatedName("Netherite Double Axe"));

    // MARK: Glaives

    public static final Weapon.Entry iron_glaive = add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "iron_glaive", Equipment.Tier.TIER_1, null).translatedName("Iron Glaive"));
    public static final Weapon.Entry golden_glaive = add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "golden_glaive", Equipment.Tier.GOLDEN, null).translatedName("Golden Glaive"));
    public static final Weapon.Entry diamond_glaive = add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "diamond_glaive", Equipment.Tier.TIER_2, null).translatedName("Diamond Glaive"));
    public static final Weapon.Entry netherite_glaive = add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "netherite_glaive", Equipment.Tier.TIER_3, null).translatedName("Netherite Glaive"));

    // MARK: Register

    public static void register(Map<String, WeaponConfig> configs) {
        if (RoguesMod.tweaksConfig.value.ignore_items_required_mods || Platform.util().isModLoaded(BETTER_NETHER)) {
            add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "ruby_dagger", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_NETHER_RUBY));
            add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "ruby_sickle", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_NETHER_RUBY));
            add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "ruby_double_axe", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_NETHER_RUBY));
            add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "ruby_glaive", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_NETHER_RUBY));
        }
        if (RoguesMod.tweaksConfig.value.ignore_items_required_mods || Platform.util().isModLoaded(BETTER_END)) {
            add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "aeternium_dagger", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_AETERNIUM));
            add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "aeternium_sickle", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_AETERNIUM));
            add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "aeternium_double_axe", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_AETERNIUM));
            add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "aeternium_glaive", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_AETERNIUM));
        }
        if (RoguesMod.tweaksConfig.value.ignore_items_required_mods || Platform.util().isModLoaded(AETHER)) {
            add(Weapons.daggerWithSkill(RoguesMod.NAMESPACE, "aether_dagger", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_AMBROSIUM)
                    .loot(Equipment.LootProperties.of("aether")));
            add(Weapons.sickleWithSkill(RoguesMod.NAMESPACE, "aether_sickle", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_AMBROSIUM)
                    .loot(Equipment.LootProperties.of("aether")));
            add(Weapons.doubleAxeWithSkill(RoguesMod.NAMESPACE, "aether_double_axe", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_AMBROSIUM)
                    .loot(Equipment.LootProperties.of("aether")));
            add(Weapons.glaiveWithSkill(RoguesMod.NAMESPACE, "aether_glaive", Equipment.Tier.TIER_4, RogueItemTags.REPAIRS_AMBROSIUM)
                    .loot(Equipment.LootProperties.of("aether")));
        }
        Weapon.register(configs, entries, Group.KEY);
    }
}
