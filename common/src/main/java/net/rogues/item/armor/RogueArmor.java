package net.rogues.item.armor;

import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.spell_engine.rpg_series.item.Armor;

public class RogueArmor extends Armor.CustomItem {
    public RogueArmor(ArmorMaterial material, EquipmentType slot, Settings settings) {
        super(material, slot, settings);
    }
}
