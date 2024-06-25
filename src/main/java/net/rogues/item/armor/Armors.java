package net.rogues.item.armor;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.item.Group;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.armor.Armor;

import java.util.ArrayList;
import java.util.Map;

public class Armors {
    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    public static final ArrayList<Armor.Entry> warriorEntries = new ArrayList<>();
    public static final ArrayList<Armor.Entry> rogueEntries = new ArrayList<>();
    private static Armor.Entry create(Armor.CustomMaterial material, ItemConfig.ArmorSet defaults) {
        return new Armor.Entry(material, null, defaults);
    }

    private static final Identifier ATTACK_DAMAGE_ID = new Identifier("generic.attack_damage");
    private static final Identifier ATTACK_SPEED_ID = new Identifier("generic.attack_speed");
    private static final Identifier KNOCKBACK_ID = new Identifier("generic.knockback_resistance");

    private static ItemConfig.Attribute damageMultiplier(float value) {
        return new ItemConfig.Attribute(
                ATTACK_DAMAGE_ID.toString(),
                value,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
    }

    private static ItemConfig.Attribute hasteMultiplier(float value) {
        return new ItemConfig.Attribute(
                ATTACK_SPEED_ID.toString(),
                value,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
    }

    private static ItemConfig.Attribute knockbackMultiplier(float value) {
        return new ItemConfig.Attribute(
                KNOCKBACK_ID.toString(),
                value,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static final float rogue_haste_T1 = 0.04F;
    public static final float rogue_damage_T1 = 0.05F;
    public static final float rogue_haste_T2 = 0.05F;
    public static final float rogue_damage_T2 = 0.04F;
    public static final float warrior_damage_T1 = 0.04F;
    public static final float warrior_damage_T2 = 0.05F;
    public static final float knockback_T2 = 0.1F;

    public static final Armor.Set RogueArmorSet_t1 =
            create(
                    new Armor.CustomMaterial(
                            "rogue_armor",
                            10,
                            9,
                            RogueArmor.equipSound,
                            () -> { return Ingredient.ofItems(Items.LEATHER); }
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(2)
                                    .add(hasteMultiplier(rogue_haste_T1)),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .add(hasteMultiplier(rogue_haste_T1)),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .add(hasteMultiplier(rogue_haste_T1)),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .add(hasteMultiplier(rogue_haste_T1))
                    )
                )
                .bundle(material -> new Armor.Set(RoguesMod.NAMESPACE,
                        new RogueArmor(material, ArmorItem.Type.HELMET, new Item.Settings()),
                        new RogueArmor(material, ArmorItem.Type.CHESTPLATE, new Item.Settings()),
                        new RogueArmor(material, ArmorItem.Type.LEGGINGS, new Item.Settings()),
                        new RogueArmor(material, ArmorItem.Type.BOOTS, new Item.Settings())
                ))
                .put(entries)
                .put(rogueEntries)
                .armorSet();

    public static float rogueRobeHaste = 0.05F;
    private static final float specializedRobeSpellPower = 0.25F;
    public static final Armor.Set RogueArmorSet_t2 =
            create(
                    new Armor.CustomMaterial(
                            "assassin_armor",
                            20,
                            10,
                            RogueArmor.equipSound,
                            () -> { return Ingredient.ofItems(Items.RABBIT_HIDE); }
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(2)
                                    .add(damageMultiplier(rogue_damage_T2))
                                    .add(hasteMultiplier(rogue_haste_T2)),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .add(damageMultiplier(rogue_damage_T2))
                                    .add(hasteMultiplier(rogue_haste_T2)),
                            new ItemConfig.ArmorSet.Piece(3)
                                    .add(damageMultiplier(rogue_damage_T2))
                                    .add(hasteMultiplier(rogue_haste_T2)),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .add(damageMultiplier(rogue_damage_T2))
                                    .add(hasteMultiplier(rogue_haste_T2))
                    )
            )
            .bundle(material -> new Armor.Set(RoguesMod.NAMESPACE,
                    new RogueArmor(material, ArmorItem.Type.HELMET, new Item.Settings()),
                    new RogueArmor(material, ArmorItem.Type.CHESTPLATE, new Item.Settings()),
                    new RogueArmor(material, ArmorItem.Type.LEGGINGS, new Item.Settings()),
                    new RogueArmor(material, ArmorItem.Type.BOOTS, new Item.Settings())
            ))
            .put(entries)
            .put(rogueEntries)
            .armorSet();


    public static final Armor.Set WarriorArmorSet_t1 =
            create(
                    new Armor.CustomMaterial(
                            "warrior_armor",
                            15,
                            9,
                            WarriorArmor.equipSound,
                            () -> { return Ingredient.ofItems(Items.IRON_INGOT); }
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(2)
                                    .add(damageMultiplier(warrior_damage_T1)),
                            new ItemConfig.ArmorSet.Piece(6)
                                    .add(damageMultiplier(warrior_damage_T1)),
                            new ItemConfig.ArmorSet.Piece(5)
                                    .add(damageMultiplier(warrior_damage_T1)),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .add(damageMultiplier(warrior_damage_T1)))
            )
            .bundle(material -> new Armor.Set(RoguesMod.NAMESPACE,
                    new WarriorArmor(material, ArmorItem.Type.HELMET, new Item.Settings()),
                    new WarriorArmor(material, ArmorItem.Type.CHESTPLATE, new Item.Settings()),
                    new WarriorArmor(material, ArmorItem.Type.LEGGINGS, new Item.Settings()),
                    new WarriorArmor(material, ArmorItem.Type.BOOTS, new Item.Settings())
            ))
            .put(entries)
            .put(warriorEntries)
            .armorSet();

    public static final Armor.Set WarriorArmorSet_t2 =
            create(
                    new Armor.CustomMaterial(
                            "berserker_armor",
                            25,
                            10,
                            WarriorArmor.equipSound,
                            () -> { return Ingredient.ofItems(Items.CHAIN); }
                    ),
                    ItemConfig.ArmorSet.with(
                            new ItemConfig.ArmorSet.Piece(2)
                                    .add(damageMultiplier(warrior_damage_T2))
                                    .add(knockbackMultiplier(knockback_T2)),
                            new ItemConfig.ArmorSet.Piece(6)
                                    .add(damageMultiplier(warrior_damage_T2))
                                    .add(knockbackMultiplier(knockback_T2)),
                            new ItemConfig.ArmorSet.Piece(5)
                                    .add(damageMultiplier(warrior_damage_T2))
                                    .add(knockbackMultiplier(knockback_T2)),
                            new ItemConfig.ArmorSet.Piece(2)
                                    .add(damageMultiplier(warrior_damage_T2))
                                    .add(knockbackMultiplier(knockback_T2))
                    )
            )
            .bundle(material -> new Armor.Set(RoguesMod.NAMESPACE,
                    new WarriorArmor(material, ArmorItem.Type.HELMET, new Item.Settings()),
                    new WarriorArmor(material, ArmorItem.Type.CHESTPLATE, new Item.Settings()),
                    new WarriorArmor(material, ArmorItem.Type.LEGGINGS, new Item.Settings()),
                    new WarriorArmor(material, ArmorItem.Type.BOOTS, new Item.Settings())
            ))
            .put(entries)
            .put(warriorEntries)
            .armorSet();

    public static void register(Map<String, ItemConfig.ArmorSet> configs) {
        Armor.register(configs, entries, Group.KEY);
    }
}

