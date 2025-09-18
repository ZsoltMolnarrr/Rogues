package net.rogues.item.armor;

import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.spell_engine.api.item.armor.Armor;

public class RogueArmor extends Armor.CustomItem {
    public RogueArmor(RegistryEntry<ArmorMaterial> material, Type slot, Settings settings) {
        super(material, slot, settings);
    }
}