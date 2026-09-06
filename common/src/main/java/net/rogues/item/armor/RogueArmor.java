package net.rogues.item.armor;

import net.minecraft.item.ArmorMaterial;
import net.spell_engine.rpg_series.item.Armor;

public class RogueArmor extends Armor.CustomItem {
    /// 1.20.1: `ArmorMaterial` is a plain interface, not a `RegistryEntry<ArmorMaterial>`.
    public RogueArmor(ArmorMaterial material, Type slot, Settings settings) {
        super(material, slot, settings);
    }
}