package net.rogues.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.rogues.RoguesMod;

import java.util.List;
import java.util.Map;

public class RogueSounds {
    public static List<String> soundKeys = List.of(
        "slice_and_dice",
        "shock_powder_release",
        "shock_powder_impact",
        "shadow_step_arrive",
        "shadow_step_depart",
        "vanish_release",
        "vanish_combined",
        "throw",
        "throw_impact",
        "shout_release",
        "demoralize_impact",
        "charge_activate",
        "whirlwind"
    );

    public static Map<String, Float> soundDistances = Map.of(
        // "sound_name", Float.valueOf(48F)
    );

    public static void registerSounds() {
        for (var soundKey: soundKeys) {
            var soundId = Identifier.of(RoguesMod.NAMESPACE, soundKey);
            var customTravelDistance = soundDistances.get(soundKey);
            var soundEvent = (customTravelDistance == null)
                    ? SoundEvent.of(soundId)
                    : SoundEvent.of(soundId, customTravelDistance);
            Registry.register(Registries.SOUND_EVENT, soundId, soundEvent);
        }
    }

    public record Entry(Identifier id, SoundEvent sound, RegistryEntry<SoundEvent> entry) {}
    private static Entry registerSound(String key) {
        var soundId = Identifier.of(RoguesMod.NAMESPACE, key);
        var event = SoundEvent.of(soundId);
        var entry = Registry.registerReference(Registries.SOUND_EVENT, soundId, event);
        return new Entry(soundId, event, entry);
    }
    public static final Entry ROGUE_ARMOR_EQUIP = registerSound("rogue_armor");
    public static final Entry WARRIOR_ARMOR_EQUIP = registerSound("warrior_armor");
    public static final Entry WORKBENCH = registerSound("arms_workbench");
    public static final Entry STEALTH_LEAVE = registerSound("stealth_leave");

    public static void playSoundEvent(World world, Entity entity, SoundEvent soundEvent) {
        playSoundEvent(world, entity, soundEvent, 1, 1);
    }

    public static void playSoundEvent(World world, Entity entity, SoundEvent soundEvent, float volume, float pitch) {
        world.playSound(
                (PlayerEntity)null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                soundEvent,
                SoundCategory.PLAYERS,
                volume,
                pitch);
    }
}