package net.rogues.client.armor;

import mod.azure.azurelibarmor.common.api.client.renderer.GeoArmorRenderer;
import net.rogues.item.armor.WarriorArmor;

public class WarriorArmorRenderer extends GeoArmorRenderer<WarriorArmor> {
    public WarriorArmorRenderer() {
        super(new WarriorArmorModel());
    }
}
