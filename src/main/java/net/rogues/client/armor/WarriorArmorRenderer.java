package net.rogues.client.armor;

import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelibarmor.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;

public class WarriorArmorRenderer extends AzArmorRenderer {
    public static WarriorArmorRenderer warrior() {
        return new WarriorArmorRenderer("warrior_armor", "warrior_armor");
    }
    public static WarriorArmorRenderer berserker() {
        return new WarriorArmorRenderer("warrior_armor", "berserker_armor");
    }
    public static WarriorArmorRenderer netheriteBerserker() {
        return new WarriorArmorRenderer("warrior_armor", "netherite_berserker_armor");
    }

    public WarriorArmorRenderer(String modelName, String textureName) {
        super(AzArmorRendererConfig.builder(
                Identifier.of(RoguesMod.NAMESPACE, "geo/" + modelName + ".geo.json"),
                Identifier.of(RoguesMod.NAMESPACE, "textures/armor/" + textureName + ".png")
        ).build());
    }
}