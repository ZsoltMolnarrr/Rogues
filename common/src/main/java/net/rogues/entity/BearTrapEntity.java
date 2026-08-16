package net.rogues.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.world.World;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.entity.SpellCloud;
import net.spell_engine.internals.SpellExecution;

public class BearTrapEntity extends SpellCloud {

    /// Length of the `attack` clip (1.5s). The jaws snap shut over the first ~2 ticks, hold, then the
    /// trap sinks across the last 10 — so the entity is removed exactly as it finishes going under.
    /// Deliberately longer than the cloud's `despawn_ticks`, which still sizes a natural expiry.
    public static final int ATTACK_TICKS = 30;

    private static final TrackedData<Boolean> SPRUNG =
            DataTracker.registerData(BearTrapEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public BearTrapEntity(EntityType<? extends SpellCloud> entityType, World world) {
        super(entityType, world);
    }

    /// Whether this trap wound down because it caught something, rather than timing out. Drives the
    /// renderer's choice of `attack` over `despawn`.
    public boolean isSprung() {
        return getDataTracker().get(SPRUNG);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SPRUNG, false);
    }

    @Override
    protected void onImpactPerformed(LivingEntity owner, World world, Spell.Delivery.Cloud cloudData, SpellExecution.ImpactContext context) {
        super.onImpactPerformed(owner, world, cloudData, context); // impact particles, impactsPerformed++
        // Flag before winding down, so the client never observes DESPAWNING without knowing why and
        // picks the wrong clip for a frame.
        getDataTracker().set(SPRUNG, true);
        // First caller wins: this pre-empts the impact-cap fallback in SpellCloud.tick(), which would
        // otherwise wind down over the (shorter) configured `despawn_ticks`. With `impact_cap = 1`
        // this runs exactly once; raising the cap would need a guard on the trap being unsprung.
        beginDespawn(ATTACK_TICKS);
    }
}
