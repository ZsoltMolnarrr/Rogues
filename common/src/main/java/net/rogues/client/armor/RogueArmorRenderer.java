package net.rogues.client.armor;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;

public class RogueArmorRenderer extends AzArmorRenderer {
    public static RogueArmorRenderer rogue() {
        return new RogueArmorRenderer("rogue_armor", "rogue_armor");
    }
    public static RogueArmorRenderer assassin() {
        return new RogueArmorRenderer("rogue_armor", "assassin_armor");
    }
    public static RogueArmorRenderer netheriteAssassin() {
        return new RogueArmorRenderer("rogue_armor", "netherite_assassin_armor");
    }

    public RogueArmorRenderer(String modelName, String textureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(RoguesMod.NAMESPACE, "geo/" + modelName + ".geo.json"),
                Identifier.of(RoguesMod.NAMESPACE, "textures/armor/" + textureName + ".png")
        ).build());
    }
}