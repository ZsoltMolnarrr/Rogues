package net.rogues.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.rogues.RoguesMod;
import net.rogues.block.MartialWorkbenchBlock;
import net.rogues.effect.StealthEffect;

import java.util.List;
import java.util.Map;

public class SoundHelper {
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
        "charge_activate"
    );

    public static Map<String, Float> soundDistances = Map.of(
        // "sound_name", Float.valueOf(48F)
    );

    public static final SoundEvent WORKBENCH = SoundEvent.of(MartialWorkbenchBlock.ID);

    public static void registerSounds() {
        for (var soundKey: soundKeys) {
            var soundId = new Identifier(RoguesMod.NAMESPACE, soundKey);
            var customTravelDistance = soundDistances.get(soundKey);
            var soundEvent = (customTravelDistance == null)
                    ? SoundEvent.of(soundId)
                    : SoundEvent.of(soundId, customTravelDistance);
            Registry.register(Registries.SOUND_EVENT, soundId, soundEvent);
        }

        Registry.register(Registries.SOUND_EVENT, MartialWorkbenchBlock.ID, WORKBENCH);
        Registry.register(Registries.SOUND_EVENT, StealthEffect.LEAVE_SOUND_ID, StealthEffect.LEAVE_SOUND);
    }

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