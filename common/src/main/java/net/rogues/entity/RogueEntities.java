package net.rogues.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;
import net.spell_engine.api.spell.summon.SummonedEntities;
import net.spell_engine.api.spell.summon.SummonedEntityConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static final Entry<BearTrapEntity> BEAR_TRAP = add(new Entry<>(
            new Identifier(RoguesMod.NAMESPACE, "bear_trap"),
            "Bear Trap",
            EntityType.Builder.<BearTrapEntity>create(BearTrapEntity::new, SpawnGroup.MISC)
                    .setDimensions(1F, 0.5F) // dimensions in Minecraft units of the render
                    .makeFireImmune()
                    .maxTrackingRange(128)
                    .trackingTickInterval(20)
                    // Vanilla build(String id) — the no-arg build() is a Fabric API interface-injected
                    // default (FabricEntityType.Builder) absent on NeoForge at runtime.
                    .build("bear_trap")));

    public static void register() {
        entityTypesToRegister().forEach((id, type) -> Registry.register(Registries.ENTITY_TYPE, id, type));
    }

    /// Every entity type that still needs registering, keyed by the id it registers under, with the
    /// summoned-entity attribute defaults buffered as a side effect. Creation only — nothing is written
    /// into the entity-type registry here, so a loader that registers entity types itself (Forge)
    /// iterates this instead of calling {@link #register()}.
    ///
    /// **Must run inside the `ENTITY_TYPE` registration window**: class init calls
    /// `EntityType.Builder#build`, which constructs an intrusive registry holder.
    public static Map<Identifier, EntityType<?>> entityTypesToRegister() {
        var types = new LinkedHashMap<Identifier, EntityType<?>>();
        for (var entry : entries) {
            if (Registries.ENTITY_TYPE.containsId(entry.id)) { continue; }
            types.put(entry.id, entry.type);
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
        return types;
    }
}
