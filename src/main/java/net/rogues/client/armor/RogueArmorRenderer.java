package net.rogues.client.armor;

import mod.azure.azurelibarmor.common.api.client.renderer.GeoArmorRenderer;
import net.rogues.item.armor.RogueArmor;

public class RogueArmorRenderer extends GeoArmorRenderer<RogueArmor> {
    public RogueArmorRenderer() {
        super(new RogueArmorModel());
    }
}
