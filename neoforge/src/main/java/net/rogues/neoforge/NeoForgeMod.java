package net.rogues.neoforge;

import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.rogues.RoguesMod;

@Mod(RoguesMod.ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        // Run our common setup.
        RoguesMod.init();
        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
    }

    public static void register(RegisterEvent event) {
        event.register(RegistryKeys.SOUND_EVENT, reg -> {
            RoguesMod.registerSounds();
        });
        event.register(RegistryKeys.ITEM, reg -> {
            RoguesMod.registerItems();
        });
        event.register(RegistryKeys.STATUS_EFFECT, reg -> {
            RoguesMod.registerEffects();
        });
        event.register(RegistryKeys.POINT_OF_INTEREST_TYPE, reg -> {
            // Not sure why errors are thrown, but this seems to fix it.
            try {
                RoguesMod.registerPOI();
            } catch (Exception e) { }
        });
        event.register(RegistryKeys.VILLAGER_PROFESSION, reg -> {
            RoguesMod.registerVillagers();
        });
    }
}
