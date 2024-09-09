package net.rogues.client.armor;

import mod.azure.azurelibarmor.common.api.client.model.GeoModel;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rogues.item.armor.WarriorArmor;

public class WarriorArmorModel extends GeoModel<WarriorArmor> {
    @Override
    public Identifier getModelResource(WarriorArmor object) {
        return Identifier.of(RoguesMod.NAMESPACE, "geo/warrior_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(WarriorArmor armor) {
        var textureId = armor.getFirstLayerId();
        return Identifier.of(textureId.getNamespace(), "textures/armor/" + textureId.getPath() + ".png");
    }

    @Override
    public Identifier getAnimationResource(WarriorArmor animatable) {
        return null; // Identifier.of(PaladinsMod.ID, "animations/armor_idle.json");
    }
}
