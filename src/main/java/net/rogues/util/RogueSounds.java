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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RogueSounds {
    public static final class Entry {
        private final Identifier id;
        private final SoundEvent soundEvent;
        private RegistryEntry<SoundEvent> entry;
        private int variants = 1;

        public Entry(Identifier id, SoundEvent soundEvent) {
            this.id = id;
            this.soundEvent = soundEvent;
        }

        public Entry(String name) {
            this(Identifier.of(RoguesMod.NAMESPACE, name));
        }

        public Entry(Identifier id) {
            this(id, SoundEvent.of(id));
        }

        public Entry travelDistance(float distance) {
            return new Entry(id, SoundEvent.of(id, distance));
        }

        public Entry variants(int variants) {
            this.variants = variants;
            return this;
        }

        public Identifier id() {
            return id;
        }

        public SoundEvent soundEvent() {
            return soundEvent;
        }

        public RegistryEntry<SoundEvent> entry() {
            return entry;
        }

        public int variants() {
            return variants;
        }
    }
    public static final List<Entry> entries = new ArrayList<>();
    public static Entry add(Entry entry) {
        entries.add(entry);
        return entry;
    }

    public static final Entry SLICE_AND_DICE = add(new Entry("slice_and_dice"));
    public static final Entry SHOCK_POWDER_RELEASE = add(new Entry("shock_powder_release"));
    public static final Entry SHOCK_POWDER_IMPACT = add(new Entry("shock_powder_impact"));
    public static final Entry SHADOW_STEP_ARRIVE = add(new Entry("shadow_step_arrive"));
    public static final Entry SHADOW_STEP_DEPART = add(new Entry("shadow_step_depart"));
    public static final Entry VANISH_RELEASE = add(new Entry("vanish_release"));
    public static final Entry VANISH_COMBINED = add(new Entry("vanish_combined"));
    public static final Entry THROW = add(new Entry("throw"));
    public static final Entry THROW_IMPACT = add(new Entry("throw_impact"));
    public static final Entry SHOUT_RELEASE = add(new Entry("shout_release"));
    public static final Entry DEMORALIZE_IMPACT = add(new Entry("demoralize_impact"));
    public static final Entry CHARGE_ACTIVATE = add(new Entry("charge_activate"));
    public static final Entry WHIRLWIND = add(new Entry("whirlwind"));
    public static final Entry ROGUE_ARMOR_EQUIP = add(new Entry("rogue_armor"));
    public static final Entry WARRIOR_ARMOR_EQUIP = add(new Entry("warrior_armor"));
    public static final Entry WORKBENCH = add(new Entry("arms_workbench"));
    public static final Entry STEALTH_LEAVE = add(new Entry("stealth_leave"));

    public static void register() {
        for (var entry: entries) {
            entry.entry = Registry.registerReference(Registries.SOUND_EVENT, entry.id(), entry.soundEvent());
        }
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