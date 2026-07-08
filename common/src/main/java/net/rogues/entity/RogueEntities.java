package net.rogues.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.rogues.RoguesMod;

public class RogueEntities {
    public static final Identifier BEAR_TRAP_ID = Identifier.of(RoguesMod.NAMESPACE, "bear_trap");

    public static void register() {
        BearTrapEntity.ENTITY_TYPE = Registry.register(
                Registries.ENTITY_TYPE,
                BEAR_TRAP_ID,
                EntityType.Builder.<BearTrapEntity>create(BearTrapEntity::new, SpawnGroup.MISC)
                        .dimensions(1F, 0.5F) // dimensions in Minecraft units of the render
                        .makeFireImmune()
                        .maxTrackingRange(128)
                        .trackingTickInterval(20)
                        .build()
        );
    }
}
