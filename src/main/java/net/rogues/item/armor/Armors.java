package net.rogues.item.armor;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.item.Group;
import net.rogues.util.RogueSounds;
import net.spell_engine.api.item.ItemConfig;
import net.spell_engine.api.item.armor.Armor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Armors {

    public static RegistryEntry<ArmorMaterial> material(
            String name, int protectionHead, int protectionChest, int protectionLegs, int protectionFeet,
            int enchantability, RegistryEntry<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient) {

        var material = new ArmorMaterial(
                Map.of(
                        ArmorItem.Type.HELMET, protectionHead,
                        ArmorItem.Type.CHESTPLATE, protectionChest,
                        ArmorItem.Type.LEGGINGS, protectionLegs,
                        ArmorItem.Type.BOOTS, protectionFeet),
                enchantability, equipSound, repairIngredient,
                List.of(new ArmorMaterial.Layer(Identifier.of(RoguesMod.NAMESPACE, name))),
                0,0
        );
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(RoguesMod.NAMESPACE, name), material);
    }

    public static RegistryEntry<ArmorMaterial> material_rogue_t1 = material(
            "rogue_armor",
            1, 3, 3, 1,
            9,
            RogueSounds.ROGUE_ARMOR_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.LEATHER); });

    public static RegistryEntry<ArmorMaterial> material_rogue_t2 = material(
            "assassin_armor",
            2, 4, 4, 2,
            10,
            RogueSounds.ROGUE_ARMOR_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.RABBIT_HIDE); });

    public static RegistryEntry<ArmorMaterial> material_rogue_t3 = material(
            "netherite_assassin_armor",
            2, 4, 4, 2,
            15,
            RogueSounds.ROGUE_ARMOR_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });

    public static RegistryEntry<ArmorMaterial> material_warrior_t1 = material(
            "warrior_armor",
            2, 5, 4, 1,
            9,
            RogueSounds.WARRIOR_ARMOR_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.IRON_INGOT); });

    public static RegistryEntry<ArmorMaterial> material_warrior_t2 = material(
            "berserker_armor",
            3, 8, 6, 2,
            10,
            RogueSounds.WARRIOR_ARMOR_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.CHAIN); });

    public static RegistryEntry<ArmorMaterial> material_warrior_t3 = material(
            "netherite_berserker_armor",
            3, 8, 6, 2,
            15,
            RogueSounds.WARRIOR_ARMOR_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });


    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(RegistryEntry<ArmorMaterial> material, Identifier id, int durability,
                                      Armor.Set.ItemFactory factory, ItemConfig.ArmorSet defaults) {
        var entry = Armor.Entry.create(
                material,
                id,
                durability,
                factory,
                defaults);
        entries.add(entry);
        return entry;
    }


    private static final Identifier ATTACK_DAMAGE_ID = Identifier.ofVanilla("generic.attack_damage");
    private static final Identifier ATTACK_SPEED_ID = Identifier.ofVanilla("generic.attack_speed");
    private static final Identifier KNOCKBACK_ID = Identifier.ofVanilla("generic.knockback_resistance");
    private static final Identifier MOVEMENT_SPEED_ID = Identifier.ofVanilla("generic.movement_speed");
    private static final Identifier ARMOR_TOUGHNESS_ID = Identifier.ofVanilla("generic.armor_toughness");

    private static ItemConfig.Attribute damageMultiplier(float value) {
        return new ItemConfig.Attribute(
                ATTACK_DAMAGE_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static ItemConfig.Attribute hasteMultiplier(float value) {
        return new ItemConfig.Attribute(
                ATTACK_SPEED_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static ItemConfig.Attribute knockbackBonus(float value) {
        return new ItemConfig.Attribute(
                KNOCKBACK_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_VALUE);
    }

    private static ItemConfig.Attribute movementSpeed(float value) {
        return new ItemConfig.Attribute(
                MOVEMENT_SPEED_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static ItemConfig.Attribute toughnessBonus(float value) {
        return new ItemConfig.Attribute(
                ARMOR_TOUGHNESS_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_VALUE);
    }

    public static final float rogue_t1_speed = 0.05F;
    public static final float rogue_t1_haste = 0.04F;
    // public static final float rogue_t1_damage = 0.02F;

    public static final float rogue_t2_speed = 0.05F;
    public static final float rogue_t2_haste = 0.05F;
    public static final float rogue_t2_damage = 0.02F;

    public static final float rogue_t3_speed = 0.05F;
    public static final float rogue_t3_haste = 0.05F;
    public static final float rogue_t3_damage = 0.05F;

    public static final float warrior_t1_damage = 0.04F;

    public static final float warrior_t2_damage = 0.05F;
    public static final float warrior_t2_knockback = 0.1F;

    public static final float warrior_t3_damage = 0.05F;
    public static final float warrior_t3_toughness = 1F;
    public static final float warrior_t3_knockback = 0.1F;

    public static final Armor.Set RogueArmorSet_t1 = create(
            material_rogue_t1,
            Identifier.of(RoguesMod.NAMESPACE, "rogue_armor"),
            15,
            RogueArmor::new,
            ItemConfig.ArmorSet.with(
                    new ItemConfig.ArmorSet.Piece(1)
                            .add(movementSpeed(rogue_t1_speed))
                            .add(hasteMultiplier(rogue_t1_haste)),
                    new ItemConfig.ArmorSet.Piece(3)
                            .add(movementSpeed(rogue_t1_speed))
                            .add(hasteMultiplier(rogue_t1_haste)),
                    new ItemConfig.ArmorSet.Piece(3)
                            .add(movementSpeed(rogue_t1_speed))
                            .add(hasteMultiplier(rogue_t1_haste)),
                    new ItemConfig.ArmorSet.Piece(1)
                            .add(movementSpeed(rogue_t1_speed))
                            .add(hasteMultiplier(rogue_t1_haste))
            ))
            .armorSet();

    public static final Armor.Set RogueArmorSet_t2 = create(
            material_rogue_t2,
            Identifier.of(RoguesMod.NAMESPACE, "assassin_armor"),
            25,
            RogueArmor::new,
            ItemConfig.ArmorSet.with(
                    new ItemConfig.ArmorSet.Piece(2)
                            .add(movementSpeed(rogue_t2_speed))
                            .add(hasteMultiplier(rogue_t2_haste))
                            .add(damageMultiplier(rogue_t2_damage)),
                    new ItemConfig.ArmorSet.Piece(4)
                            .add(movementSpeed(rogue_t2_speed))
                            .add(hasteMultiplier(rogue_t2_haste))
                            .add(damageMultiplier(rogue_t2_damage)),
                    new ItemConfig.ArmorSet.Piece(4)
                            .add(movementSpeed(rogue_t2_speed))
                            .add(hasteMultiplier(rogue_t2_haste))
                            .add(damageMultiplier(rogue_t2_damage)),
                    new ItemConfig.ArmorSet.Piece(2)
                            .add(movementSpeed(rogue_t2_speed))
                            .add(hasteMultiplier(rogue_t2_haste))
                            .add(damageMultiplier(rogue_t2_damage))
            ))
            .armorSet();

    public static final Armor.Set RogueArmorSet_t3 = create(
            material_rogue_t3,
            Identifier.of(RoguesMod.NAMESPACE, "netherite_assassin_armor"),
            37,
            RogueArmor::new,
            ItemConfig.ArmorSet.with(
                    new ItemConfig.ArmorSet.Piece(2)
                            .add(movementSpeed(rogue_t3_speed))
                            .add(hasteMultiplier(rogue_t3_haste))
                            .add(damageMultiplier(rogue_t3_damage)),
                    new ItemConfig.ArmorSet.Piece(4)
                            .add(movementSpeed(rogue_t3_speed))
                            .add(hasteMultiplier(rogue_t3_haste))
                            .add(damageMultiplier(rogue_t3_damage)),
                    new ItemConfig.ArmorSet.Piece(4)
                            .add(movementSpeed(rogue_t3_speed))
                            .add(hasteMultiplier(rogue_t3_haste))
                            .add(damageMultiplier(rogue_t3_damage)),
                    new ItemConfig.ArmorSet.Piece(2)
                            .add(movementSpeed(rogue_t3_speed))
                            .add(hasteMultiplier(rogue_t3_haste))
                            .add(damageMultiplier(rogue_t3_damage))
            ))
            .armorSet();

    public static final Armor.Set WarriorArmorSet_t1 = create(
            material_warrior_t1,
            Identifier.of(RoguesMod.NAMESPACE, "warrior_armor"),
            15,
            WarriorArmor::new,
            ItemConfig.ArmorSet.with(
                    new ItemConfig.ArmorSet.Piece(2)
                            .add(damageMultiplier(warrior_t1_damage)),
                    new ItemConfig.ArmorSet.Piece(5)
                            .add(damageMultiplier(warrior_t1_damage)),
                    new ItemConfig.ArmorSet.Piece(4)
                            .add(damageMultiplier(warrior_t1_damage)),
                    new ItemConfig.ArmorSet.Piece(1)
                            .add(damageMultiplier(warrior_t1_damage))
            ))
            .armorSet();

    public static final Armor.Set WarriorArmorSet_t2 = create(
            material_warrior_t2,
            Identifier.of(RoguesMod.NAMESPACE, "berserker_armor"),
            25,
            WarriorArmor::new,
            ItemConfig.ArmorSet.with(
                    new ItemConfig.ArmorSet.Piece(3)
                            .add(damageMultiplier(warrior_t2_damage))
                            .add(knockbackBonus(warrior_t2_knockback)),
                    new ItemConfig.ArmorSet.Piece(8)
                            .add(damageMultiplier(warrior_t2_damage))
                            .add(knockbackBonus(warrior_t2_knockback)),
                    new ItemConfig.ArmorSet.Piece(6)
                            .add(damageMultiplier(warrior_t2_damage))
                            .add(knockbackBonus(warrior_t2_knockback)),
                    new ItemConfig.ArmorSet.Piece(3)
                            .add(damageMultiplier(warrior_t2_damage))
                            .add(knockbackBonus(warrior_t2_knockback))
            ))
            .armorSet();

    public static final Armor.Set WarriorArmorSet_t3 = create(
            material_warrior_t3,
            Identifier.of(RoguesMod.NAMESPACE, "netherite_berserker_armor"),
            37,
            WarriorArmor::new,
            ItemConfig.ArmorSet.with(
                    new ItemConfig.ArmorSet.Piece(3)
                            .add(damageMultiplier(warrior_t3_damage))
                            .add(toughnessBonus(warrior_t3_toughness))
                            .add(knockbackBonus(warrior_t3_knockback)),
                    new ItemConfig.ArmorSet.Piece(8)
                            .add(damageMultiplier(warrior_t3_damage))
                            .add(toughnessBonus(warrior_t3_toughness))
                            .add(knockbackBonus(warrior_t3_knockback)),
                    new ItemConfig.ArmorSet.Piece(6)
                            .add(damageMultiplier(warrior_t3_damage))
                            .add(toughnessBonus(warrior_t3_toughness))
                            .add(knockbackBonus(warrior_t3_knockback)),
                    new ItemConfig.ArmorSet.Piece(3)
                            .add(damageMultiplier(warrior_t3_damage))
                            .add(toughnessBonus(warrior_t3_toughness))
                            .add(knockbackBonus(warrior_t3_knockback))
            ))
            .armorSet();

    public static void register(Map<String, ItemConfig.ArmorSet> configs) {
        Armor.register(configs, entries, Group.KEY);
    }
}

