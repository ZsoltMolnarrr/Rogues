package net.rogues.client.armor;

import mod.azure.azurelibarmor.model.GeoModel;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.item.armor.RogueArmor;

public class RogueArmorModel extends GeoModel<RogueArmor> {
    @Override
    public Identifier getModelResource(RogueArmor object) {
        return new Identifier(RoguesMod.NAMESPACE, "geo/rogue_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(RogueArmor armor) {
        var texture = armor.customMaterial.name();
        return new Identifier(RoguesMod.NAMESPACE, "textures/armor/" + texture + ".png");
    }

    @Override
    public Identifier getAnimationResource(RogueArmor animatable) {
        return null; // new Identifier(PaladinsMod.ID, "animations/armor_idle.json");
    }
}
