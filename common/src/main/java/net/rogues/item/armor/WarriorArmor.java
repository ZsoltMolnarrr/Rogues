package net.rogues.item.armor;

import net.minecraft.item.ArmorMaterial;
import net.spell_engine.rpg_series.item.Armor;

public class WarriorArmor extends Armor.CustomItem {
    /// 1.20.1: `ArmorMaterial` is a plain interface, not a `RegistryEntry<ArmorMaterial>`.
    public WarriorArmor(ArmorMaterial material, Type slot, Settings settings) {
        super(material, slot, settings);
    }
}
