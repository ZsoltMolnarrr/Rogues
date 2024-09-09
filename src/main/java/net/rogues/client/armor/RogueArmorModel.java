package net.rogues.client.armor;

import mod.azure.azurelibarmor.common.api.client.model.GeoModel;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.item.armor.RogueArmor;

public class RogueArmorModel extends GeoModel<RogueArmor> {
    @Override
    public Identifier getModelResource(RogueArmor object) {
        return Identifier.of(RoguesMod.NAMESPACE, "geo/rogue_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(RogueArmor armor) {
        var textureId = armor.getFirstLayerId();
        return Identifier.of(textureId.getNamespace(), "textures/armor/" + textureId.getPath() + ".png");
    }

    @Override
    public Identifier getAnimationResource(RogueArmor animatable) {
        return null; // Identifier.of(PaladinsMod.ID, "animations/armor_idle.json");
    }
}
