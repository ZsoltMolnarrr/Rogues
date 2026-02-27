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
import net.spell_engine.api.config.ArmorSetConfig;
import net.spell_engine.api.config.AttributeModifier;
import net.spell_engine.api.entity.SpellEngineAttributes;
import net.spell_engine.rpg_series.item.Armor;
import net.spell_engine.rpg_series.item.Equipment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RogueArmors {

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
            RogueSounds.WARRIOR_ARMOR_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.IRON_INGOT); });

    public static RegistryEntry<ArmorMaterial> material_warrior_t3 = material(
            "netherite_berserker_armor",
            3, 8, 6, 2,
            15,
            RogueSounds.WARRIOR_ARMOR_EQUIP.entry(), () -> { return Ingredient.ofItems(Items.NETHERITE_INGOT); });


    public static final ArrayList<Armor.Entry> entries = new ArrayList<>();
    private static Armor.Entry create(RegistryEntry<ArmorMaterial> material, Identifier id, int durability,
                                      Armor.Set.ItemFactory factory, ArmorSetConfig defaults, int tier) {
        var entry = Armor.Entry.create(
                material,
                id,
                durability,
                factory,
                defaults,
                Equipment.LootProperties.of(tier)
        );
        entries.add(entry);
        return entry;
    }


    private static final Identifier ATTACK_DAMAGE_ID = Identifier.ofVanilla("generic.attack_damage");
    private static final Identifier ATTACK_SPEED_ID = Identifier.ofVanilla("generic.attack_speed");
    private static final Identifier KNOCKBACK_ID = Identifier.ofVanilla("generic.knockback_resistance");
    private static final Identifier MOVEMENT_SPEED_ID = Identifier.ofVanilla("generic.movement_speed");
    private static final Identifier ARMOR_TOUGHNESS_ID = Identifier.ofVanilla("generic.armor_toughness");
    private static final String CRIT_MOD_ID = "critical_strike";
    private static final Identifier CRIT_CHANCE_ID = Identifier.of(CRIT_MOD_ID, "chance");
    private static final Identifier CRIT_DAMAGE_ID = Identifier.of(CRIT_MOD_ID, "damage");

    private static AttributeModifier damageMultiplier(float value) {
        return new AttributeModifier(
                ATTACK_DAMAGE_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier hasteMultiplier(float value) {
        return new AttributeModifier(
                ATTACK_SPEED_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier knockbackBonus(float value) {
        return new AttributeModifier(
                KNOCKBACK_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier movementSpeed(float value) {
        return new AttributeModifier(
                MOVEMENT_SPEED_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier evasionBonus(float value) {
        return new AttributeModifier(
                SpellEngineAttributes.EVASION_CHANCE.id.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier toughnessBonus(float value) {
        return new AttributeModifier(
                ARMOR_TOUGHNESS_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_VALUE);
    }

    private static AttributeModifier critChance(float value) {
        return new AttributeModifier(
                CRIT_CHANCE_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static AttributeModifier critDamage(float value) {
        return new AttributeModifier(
                CRIT_DAMAGE_ID.toString(),
                value,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    public static final float rogue_t1_evasion = 0.03F;
    public static final float rogue_t1_haste = 0.04F;
    // public static final float rogue_t1_damage = 0.02F;

    public static final float rogue_t2_evasion = 0.04F;
    public static final float rogue_t2_haste = 0.05F;
    public static final float rogue_t2_damage = 0.02F;
    public static final float rogue_t2_crit_chance = 0.02F;

    public static final float rogue_t3_evasion = 0.05F;
    public static final float rogue_t3_haste = 0.05F;
    public static final float rogue_t3_damage = 0.05F;
    public static final float rogue_t3_crit_chance = 0.025F;

    public static final float warrior_t1_damage = 0.04F;

    public static final float warrior_t2_damage = 0.05F;
    public static final float warrior_t2_knockback = 0.1F;
    public static final float warrior_t2_crit_damage = 0.04F;

    public static final float warrior_t3_damage = 0.05F;
    public static final float warrior_t3_toughness = 1F;
    public static final float warrior_t3_knockback = 0.1F;
    public static final float warrior_t3_crit_damage = 0.05F;

    public static final Armor.Set RogueArmorSet_t1 = create(
            material_rogue_t1,
            Identifier.of(RoguesMod.NAMESPACE, "rogue_armor"),
            15,
            RogueArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(1)
                            .add(evasionBonus(rogue_t1_evasion))
                            .add(hasteMultiplier(rogue_t1_haste)),
                    new ArmorSetConfig.Piece(3)
                            .add(evasionBonus(rogue_t1_evasion))
                            .add(hasteMultiplier(rogue_t1_haste)),
                    new ArmorSetConfig.Piece(3)
                            .add(evasionBonus(rogue_t1_evasion))
                            .add(hasteMultiplier(rogue_t1_haste)),
                    new ArmorSetConfig.Piece(1)
                            .add(evasionBonus(rogue_t1_evasion))
                            .add(hasteMultiplier(rogue_t1_haste))
            ),1)
            .armorSet();

    public static final Armor.Set RogueArmorSet_t2 = create(
            material_rogue_t2,
            Identifier.of(RoguesMod.NAMESPACE, "assassin_armor"),
            25,
            RogueArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .add(evasionBonus(rogue_t2_evasion))
                            .add(hasteMultiplier(rogue_t2_haste))
                            .add(damageMultiplier(rogue_t2_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_t2_evasion),
                                    hasteMultiplier(rogue_t2_haste),
                                    critChance(rogue_t2_crit_chance)
                            )),
                    new ArmorSetConfig.Piece(4)
                            .add(evasionBonus(rogue_t2_evasion))
                            .add(hasteMultiplier(rogue_t2_haste))
                            .add(damageMultiplier(rogue_t2_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_t2_evasion),
                                    hasteMultiplier(rogue_t2_haste),
                                    critChance(rogue_t2_crit_chance)
                            )),
                    new ArmorSetConfig.Piece(4)
                            .add(evasionBonus(rogue_t2_evasion))
                            .add(hasteMultiplier(rogue_t2_haste))
                            .add(damageMultiplier(rogue_t2_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_t2_evasion),
                                    hasteMultiplier(rogue_t2_haste),
                                    critChance(rogue_t2_crit_chance)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .add(evasionBonus(rogue_t2_evasion))
                            .add(hasteMultiplier(rogue_t2_haste))
                            .add(damageMultiplier(rogue_t2_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_t2_evasion),
                                    hasteMultiplier(rogue_t2_haste),
                                    critChance(rogue_t2_crit_chance)
                            ))
            ), 2)
            .armorSet();

    public static final Armor.Set RogueArmorSet_t3 = create(
            material_rogue_t3,
            Identifier.of(RoguesMod.NAMESPACE, "netherite_assassin_armor"),
            37,
            RogueArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .add(evasionBonus(rogue_t3_evasion))
                            .add(hasteMultiplier(rogue_t3_haste))
                            .add(damageMultiplier(rogue_t3_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_t3_evasion),
                                    hasteMultiplier(rogue_t3_haste),
                                    critChance(rogue_t3_crit_chance)
                            )),
                    new ArmorSetConfig.Piece(4)
                            .add(evasionBonus(rogue_t3_evasion))
                            .add(hasteMultiplier(rogue_t3_haste))
                            .add(damageMultiplier(rogue_t3_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_t3_evasion),
                                    hasteMultiplier(rogue_t3_haste),
                                    critChance(rogue_t3_crit_chance)
                            )),
                    new ArmorSetConfig.Piece(4)
                            .add(evasionBonus(rogue_t3_evasion))
                            .add(hasteMultiplier(rogue_t3_haste))
                            .add(damageMultiplier(rogue_t3_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_t3_evasion),
                                    hasteMultiplier(rogue_t3_haste),
                                    critChance(rogue_t3_crit_chance)
                            )),
                    new ArmorSetConfig.Piece(2)
                            .add(evasionBonus(rogue_t3_evasion))
                            .add(hasteMultiplier(rogue_t3_haste))
                            .add(damageMultiplier(rogue_t3_damage))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    evasionBonus(rogue_t3_evasion),
                                    hasteMultiplier(rogue_t3_haste),
                                    critChance(rogue_t3_crit_chance)
                            ))
            ), 3)
            .armorSet();

    public static final Armor.Set WarriorArmorSet_t1 = create(
            material_warrior_t1,
            Identifier.of(RoguesMod.NAMESPACE, "warrior_armor"),
            15,
            WarriorArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(2)
                            .add(damageMultiplier(warrior_t1_damage)),
                    new ArmorSetConfig.Piece(5)
                            .add(damageMultiplier(warrior_t1_damage)),
                    new ArmorSetConfig.Piece(4)
                            .add(damageMultiplier(warrior_t1_damage)),
                    new ArmorSetConfig.Piece(1)
                            .add(damageMultiplier(warrior_t1_damage))
            ), 1)
            .armorSet();

    public static final Armor.Set WarriorArmorSet_t2 = create(
            material_warrior_t2,
            Identifier.of(RoguesMod.NAMESPACE, "berserker_armor"),
            25,
            WarriorArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(3)
                            .add(damageMultiplier(warrior_t2_damage))
                            .add(knockbackBonus(warrior_t2_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_t2_damage),
                                    critDamage(warrior_t2_crit_damage)
                            )),
                    new ArmorSetConfig.Piece(8)
                            .add(damageMultiplier(warrior_t2_damage))
                            .add(knockbackBonus(warrior_t2_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_t2_damage),
                                    critDamage(warrior_t2_crit_damage)
                            )),
                    new ArmorSetConfig.Piece(6)
                            .add(damageMultiplier(warrior_t2_damage))
                            .add(knockbackBonus(warrior_t2_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_t2_damage),
                                    critDamage(warrior_t2_crit_damage)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .add(damageMultiplier(warrior_t2_damage))
                            .add(knockbackBonus(warrior_t2_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_t2_damage),
                                    critDamage(warrior_t2_crit_damage)
                            ))
            ), 2)
            .armorSet();

    public static final Armor.Set WarriorArmorSet_t3 = create(
            material_warrior_t3,
            Identifier.of(RoguesMod.NAMESPACE, "netherite_berserker_armor"),
            37,
            WarriorArmor::new,
            ArmorSetConfig.with(
                    new ArmorSetConfig.Piece(3)
                            .add(damageMultiplier(warrior_t3_damage))
                            .add(toughnessBonus(warrior_t3_toughness))
                            .add(knockbackBonus(warrior_t3_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_t3_damage),
                                    toughnessBonus(warrior_t3_toughness),
                                    critDamage(warrior_t3_crit_damage)
                            )),
                    new ArmorSetConfig.Piece(8)
                            .add(damageMultiplier(warrior_t3_damage))
                            .add(toughnessBonus(warrior_t3_toughness))
                            .add(knockbackBonus(warrior_t3_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_t3_damage),
                                    toughnessBonus(warrior_t3_toughness),
                                    critDamage(warrior_t3_crit_damage)
                            )),
                    new ArmorSetConfig.Piece(6)
                            .add(damageMultiplier(warrior_t3_damage))
                            .add(toughnessBonus(warrior_t3_toughness))
                            .add(knockbackBonus(warrior_t3_knockback))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_t3_damage),
                                    toughnessBonus(warrior_t3_toughness),
                                    critDamage(warrior_t3_crit_damage)
                            )),
                    new ArmorSetConfig.Piece(3)
                            .add(damageMultiplier(warrior_t3_damage))
                            .add(toughnessBonus(warrior_t3_toughness))
                            .addConditional(CRIT_MOD_ID, List.of(
                                    damageMultiplier(warrior_t3_damage),
                                    toughnessBonus(warrior_t3_toughness),
                                    critDamage(warrior_t3_crit_damage)
                            ))
            ), 3)
            .armorSet();

    public static void register(Map<String, ArmorSetConfig> configs) {
        Armor.register(configs, entries, Group.KEY);
    }
}

