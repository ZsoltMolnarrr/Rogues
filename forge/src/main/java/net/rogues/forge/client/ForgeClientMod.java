package net.rogues.forge.client;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.rogues.client.RoguesClientMod;
import net.rogues.client.entity.BearTrapEntityModel;
import net.rogues.client.entity.BearTrapEntityRenderer;
import net.rogues.entity.RogueEntities;
import net.spell_engine.client.gui.ConfigMenuScreen;

/// Client-only wiring for Forge 47; only touched from {@link net.rogues.forge.ForgeMod} behind a
/// `Dist.CLIENT` check. Mod-bus listeners are registered explicitly (Forge 47's `@EventBusSubscriber`
/// scanning is avoided so the class is never loaded on a dedicated server).
///
/// 1.20.1 port of the NeoForge client entrypoint: `IConfigScreenFactory` becomes
/// `ConfigScreenHandler.ConfigScreenFactory`.
public final class ForgeClientMod {
    public static void register(IEventBus modBus) {
        modBus.addListener(EventPriority.NORMAL, false, FMLClientSetupEvent.class, ForgeClientMod::onClientSetup);
        modBus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.RegisterLayerDefinitions.class,
                ForgeClientMod::onRegisterLayerDefinitions);
        modBus.addListener(EventPriority.NORMAL, false, EntityRenderersEvent.RegisterRenderers.class,
                ForgeClientMod::onRegisterRenderers);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        RoguesClientMod.init();
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> new ConfigMenuScreen(parent)));
    }

    private static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BearTrapEntityModel.LAYER, BearTrapEntityModel::getTexturedModelData);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RogueEntities.BEAR_TRAP.type, BearTrapEntityRenderer::new);
    }
}
