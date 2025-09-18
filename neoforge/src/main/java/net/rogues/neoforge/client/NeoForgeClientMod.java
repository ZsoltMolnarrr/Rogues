package net.rogues.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.rogues.RoguesMod;
import net.rogues.client.RoguesClientMod;
import net.spell_engine.client.gui.ConfigMenuScreen;

@EventBusSubscriber(modid = RoguesMod.ID, value = Dist.CLIENT)
public class NeoForgeClientMod {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RoguesClientMod.init();
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, parent) -> new ConfigMenuScreen(parent));
    }
}