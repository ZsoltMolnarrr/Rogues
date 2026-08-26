package net.rogues.client.armor;

import net.minecraft.resources.Identifier;
import net.rogues.RoguesMod;
import net.rpg_foundation.armor_api.client.GeoArmorRenderer;

public final class WarriorArmorRenderer {
    private WarriorArmorRenderer() { }

    public static GeoArmorRenderer warrior() {
        return make("warrior_armor", "warrior_armor");
    }
    public static GeoArmorRenderer berserker() {
        return make("warrior_armor", "berserker_armor");
    }
    public static GeoArmorRenderer netheriteBerserker() {
        return make("warrior_armor", "netherite_berserker_armor");
    }

    private static GeoArmorRenderer make(String modelName, String textureName) {
        return GeoArmorRenderer.of(
                Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "geo/" + modelName + ".geo.json"),
                Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "textures/armor/" + textureName + ".png"))
                .trim(Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "armor/trim/" + textureName + "_generic"), false);
    }
}
