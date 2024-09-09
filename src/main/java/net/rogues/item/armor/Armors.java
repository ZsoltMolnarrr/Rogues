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


    private static final Identifier ATTACK_DAMAGE_ID = Identifier.of("generic.attack_damage");
    private static final Identifier ATTACK_SPEED_ID = Identifier.of("generic.attack_speed");
    private static final Identifier KNOCKBACK_ID = Identifier.of("generic.knockback_resistance");
    private static final Identifier MOVEMENT_SPEED_ID = Identifier.of("generic.movement_speed");

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

    private static ItemConfig.Attribute knockbackMultiplier(float value) {
        return new ItemConfig.Attribute(
                KNOCKBACK_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static ItemConfig.Attribute movementSpeed(float value) {
        return new ItemConfig.Attribute(
                MOVEMENT_SPEED_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    public static final float rogue_haste_T1 = 0.04F;
    public static final float rogue_damage_T1 = 0.02F;
    public static final float rogue_speed_T1 = 0.05F;
    public static final float rogue_haste_T2 = 0.05F;
    public static final float rogue_damage_T2 = 0.02F;
    public static final float rogue_speed_T2 = 0.05F;
    public static final float warrior_damage_T1 = 0.04F;
    public static final float warrior_damage_T2 = 0.05F;
    public static final float knockback_T2 = 0.1F;

    public static final Armor.Set RogueArmorSet_t1 = create(
            material_rogue_t1,
            Identifier.of(RoguesMod.NAMESPACE, "rogue_armor"),
            15,
            RogueArmor::new,
            ItemConfig.ArmorSet.with(
                    new ItemConfig.ArmorSet.Piece(1)
                            .add(hasteMultiplier(rogue_haste_T1))
                            .add(movementSpeed(rogue_speed_T1)),
                    new ItemConfig.ArmorSet.Piece(3)
                            .add(hasteMultiplier(rogue_haste_T1))
                            .add(movementSpeed(rogue_speed_T1)),
                    new ItemConfig.ArmorSet.Piece(3)
                            .add(hasteMultiplier(rogue_haste_T1))
                            .add(movementSpeed(rogue_speed_T1)),
                    new ItemConfig.ArmorSet.Piece(1)
                            .add(hasteMultiplier(rogue_haste_T1))
                            .add(movementSpeed(rogue_speed_T1))
            ))
            .armorSet();

    public static final Armor.Set RogueArmorSet_t2 = create(
            material_rogue_t2,
            Identifier.of(RoguesMod.NAMESPACE, "assassin_armor"),
            25,
            RogueArmor::new,
            ItemConfig.ArmorSet.with(
                    new ItemConfig.ArmorSet.Piece(2)
                            .add(damageMultiplier(rogue_damage_T2))
                            .add(hasteMultiplier(rogue_haste_T2))
                            .add(movementSpeed(rogue_speed_T2)),
                    new ItemConfig.ArmorSet.Piece(4)
                            .add(damageMultiplier(rogue_damage_T2))
                            .add(hasteMultiplier(rogue_haste_T2))
                            .add(movementSpeed(rogue_speed_T2)),
                    new ItemConfig.ArmorSet.Piece(4)
                            .add(damageMultiplier(rogue_damage_T2))
                            .add(hasteMultiplier(rogue_haste_T2))
                            .add(movementSpeed(rogue_speed_T2)),
                    new ItemConfig.ArmorSet.Piece(2)
                            .add(damageMultiplier(rogue_damage_T2))
                            .add(hasteMultiplier(rogue_haste_T2))
                            .add(movementSpeed(rogue_speed_T2))
            ))
            .armorSet();

    public static final Armor.Set WarriorArmorSet_t1 = create(
            material_warrior_t1,
            Identifier.of(RoguesMod.NAMESPACE, "warrior_armor"),
            15,
            WarriorArmor::new,
            ItemConfig.ArmorSet.with(
                    new ItemConfig.ArmorSet.Piece(2)
                            .add(damageMultiplier(warrior_damage_T1)),
                    new ItemConfig.ArmorSet.Piece(5)
                            .add(damageMultiplier(warrior_damage_T1)),
                    new ItemConfig.ArmorSet.Piece(4)
                            .add(damageMultiplier(warrior_damage_T1)),
                    new ItemConfig.ArmorSet.Piece(1)
                            .add(damageMultiplier(warrior_damage_T1))
            ))
            .armorSet();

    public static final Armor.Set WarriorArmorSet_t2 = create(
            material_warrior_t2,
            Identifier.of(RoguesMod.NAMESPACE, "berserker_armor"),
            25,
            WarriorArmor::new,
            ItemConfig.ArmorSet.with(
                    new ItemConfig.ArmorSet.Piece(3)
                            .add(damageMultiplier(warrior_damage_T2))
                            .add(knockbackMultiplier(knockback_T2)),
                    new ItemConfig.ArmorSet.Piece(8)
                            .add(damageMultiplier(warrior_damage_T2))
                            .add(knockbackMultiplier(knockback_T2)),
                    new ItemConfig.ArmorSet.Piece(6)
                            .add(damageMultiplier(warrior_damage_T2))
                            .add(knockbackMultiplier(knockback_T2)),
                    new ItemConfig.ArmorSet.Piece(3)
                            .add(damageMultiplier(warrior_damage_T2))
                            .add(knockbackMultiplier(knockback_T2))
            ))
            .armorSet();

    public static void register(Map<String, ItemConfig.ArmorSet> configs) {
        Armor.register(configs, entries, Group.KEY);
    }
}

