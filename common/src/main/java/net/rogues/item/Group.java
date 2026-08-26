package net.rogues.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.rogues.RoguesMod;

public class Group {
    public static Identifier ID = Identifier.fromNamespaceAndPath(RoguesMod.NAMESPACE, "generic");
    public static ResourceKey<CreativeModeTab> KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ID);
    public static CreativeModeTab ROGUES;
}
