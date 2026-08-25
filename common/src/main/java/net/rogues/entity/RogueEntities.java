package net.rogues.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.spell_engine.api.spell.summon.SummonedEntities;
import net.spell_engine.api.spell.summon.SummonedEntityConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RogueEntities {

    /// Pairs a custom entity's type with its display name (for lang datagen) and, optionally, its
    /// summoned-entity attribute defaults. Mirrors the {@code Effects.Entry} pattern: the name lives
    /// next to the registration so it can't drift or be forgotten.
    public static class Entry<T extends Entity> {
        public final Identifier id;
        /// English display name, emitted as {@code entity.<namespace>.<path>} by lang datagen.
        public final String name;
        public final EntityType<T> type;
        /// Inline attribute defaults for spell-power-scaled summons, injected directly as the attribute
        /// source (no config file). Null for entities that aren't such summons (e.g. the bear trap, a cloud).
        @Nullable public final SummonedEntityConfig.Entry summonConfig;

        public Entry(Identifier id, String name, EntityType<T> type) {
            this(id, name, type, null);
        }
        public Entry(Identifier id, String name, EntityType<T> type, @Nullable SummonedEntityConfig.Entry summonConfig) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.summonConfig = summonConfig;
        }
    }

    public static final List<Entry<?>> entries = new ArrayList<>();
    private static <T extends Entity> Entry<T> add(Entry<T> entry) {
        entries.add(entry);
        return entry;
    }

    private static final Identifier BEAR_TRAP_ID = Identifier.of(RoguesMod.NAMESPACE, "bear_trap");

    public static final Entry<BearTrapEntity> BEAR_TRAP = add(new Entry<>(
            BEAR_TRAP_ID,
            "Bear Trap",
            EntityType.Builder.<BearTrapEntity>create(BearTrapEntity::new, SpawnGroup.MISC)
                    .dimensions(1F, 0.5F) // dimensions in Minecraft units of the render
                    .makeFireImmune()
                    .maxTrackingRange(128)
                    .trackingTickInterval(20)
                    // Vanilla build(RegistryKey) — the no-arg build() is a Fabric API interface-injected
                    // default (FabricEntityType.Builder) absent on NeoForge at runtime.
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, BEAR_TRAP_ID))));

    public static void register() {
        for (var entry : entries) {
            Registry.register(Registries.ENTITY_TYPE, entry.id, entry.type);
            if (entry.summonConfig != null) {
                // Only summoned (living) entities carry a config; safe by construction.
                @SuppressWarnings("unchecked")
                var livingType = (EntityType<? extends LivingEntity>) entry.type;
                // Inline-constant source: Rogues needs no config file for its summons, so it injects the
                // in-code default straight in as a Function<Identifier, Entry>. Same seam as the TinyConfig-
                // backed class mods — it just accepts a non-TinyConfig source.
                SummonedEntities.registerAttributes(entry.id, livingType, id -> entry.summonConfig);
            }
        }
    }
}
