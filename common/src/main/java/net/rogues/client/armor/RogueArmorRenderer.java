package net.rogues.client.armor;

import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.rpg_foundation.armor_api.client.GeoArmorRenderer;

public final class RogueArmorRenderer {
    private RogueArmorRenderer() { }

    public static GeoArmorRenderer rogue() {
        return make("rogue_armor", "rogue_armor");
    }
    public static GeoArmorRenderer assassin() {
        return make("rogue_armor", "assassin_armor");
    }
    public static GeoArmorRenderer netheriteAssassin() {
        return make("rogue_armor", "netherite_assassin_armor");
    }

    private static GeoArmorRenderer make(String modelName, String textureName) {
        return GeoArmorRenderer.of(
                new Identifier(RoguesMod.NAMESPACE, "geo/" + modelName + ".geo.json"),
                new Identifier(RoguesMod.NAMESPACE, "textures/armor/" + textureName + ".png"))
                .trim(new Identifier(RoguesMod.NAMESPACE, "armor/trim/" + textureName + "_generic"), false);
    }
}
